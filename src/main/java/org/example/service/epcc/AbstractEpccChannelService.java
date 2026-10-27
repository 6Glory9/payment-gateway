package org.example.service.epcc;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.TypeUtil;
import org.example.repository.cache.ChannelMerchant;
import org.example.repository.cache.Envelope;
import org.example.repository.cache.MerchantCache;
import org.example.repository.po.InstructionLogExtPO;
import org.example.repository.service.IInstructionLogExtService;
import org.example.service.core.GatewayEngine;
import org.example.service.core.PaymentResultVO;
import org.example.service.core.Request;
import org.example.service.core.notify.ChannelService;
import org.example.service.epcc.security.asymmetric.AsymmetricCryptoFactory;
import org.example.service.epcc.security.sign.SignatureCryptoFactory;
import org.example.service.epcc.security.symmetric.SymmetricCryptoFactory;
import org.example.utils.HttpClient;
import org.example.utils.XMLUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.example.service.epcc.MessageType.EPCC_401_001_01;

public abstract class AbstractEpccChannelService<UpstreamRequest extends Request, UpstreamData, ChannelOutRequestBody, ChannelOutResponseBody> implements ChannelService<UpstreamRequest, UpstreamData> {


    @Autowired
    @Lazy
    private GatewayEngine<Request, String> gatewayEngine;

    @Autowired
    private IInstructionLogExtService instructionLogExtService;


    @Autowired
    private MerchantCache channelMerchantCache;

    @Autowired
    private SymmetricCryptoFactory symmetricCryptoFactory;

    @Autowired
    private SignatureCryptoFactory signatureCryptoFactory;


    @Autowired
    private AsymmetricCryptoFactory asymmetricCryptoFactory;


    private String masterMerchantNo = "6777777777";


    @Override
    public String generateChannelReqNo(UpstreamRequest upstreamRequest) {
        Request request = new Request();
        request.setChannelCode(EPCC_401_001_01.code());
        request.setUpstreamRequestNo(IdUtil.getSnowflakeNextIdStr());
        request.setMerchantNo(masterMerchantNo);

        PaymentResultVO<String> resultVO = gatewayEngine.execute(request);

        return resultVO.getData();
    }

    private Root<ChannelOutRequestBody> convertToChannelModel(UpstreamRequest request, ChannelMerchant channelMerchant) {
        //header
        EpccChannelOutRequestHeader header = new EpccChannelOutRequestHeader();
        header.setMessageType(getChannelCode());
        header.setIssuerId(channelMerchant.getIssuerId());
        header.setSignSerialNumber(channelMerchant.getSignSerialNumber());
        header.setEncryptionSerialNumber(channelMerchant.getEncryptionSerialNumber());


        Root<ChannelOutRequestBody> channelOutRequestBodyRoot = new Root<>();
        channelOutRequestBodyRoot.setHeader(header);
        channelOutRequestBodyRoot.setChannelBody(convertToChannelOutRequestBody(request, channelMerchant));

        return channelOutRequestBodyRoot;
    }

    protected abstract ChannelOutRequestBody convertToChannelOutRequestBody(UpstreamRequest req, ChannelMerchant channelMerchant);

    protected abstract PaymentResultVO<UpstreamData> convertToResponse(UpstreamRequest request, Root<ChannelOutRequestBody> channelOutRequestBodyRoot, ChannelOutResponseBody channelBody);


    @Override
    public final PaymentResultVO<UpstreamData> call(UpstreamRequest request) {
        ChannelMerchant channelMerchant = channelMerchantCache.get(request.getMerchantNo());
        // 1. 报文转换：平台标准模型 -> 渠道模型
        Root<ChannelOutRequestBody> channelOutRequestBodyRoot = convertToChannelModel(request, channelMerchant);
        // 2. 生成封信
        generateEnvelope(channelOutRequestBodyRoot, channelMerchant);
        // 3. 签名
        doSign(channelOutRequestBodyRoot, channelMerchant);
        // 4. 对敏感信息加密
        encryptSensitiveFields(channelOutRequestBodyRoot);
        // 5. 通讯：发送 HTTP 请求
        String httpResponseBody = doHttpExchange(channelOutRequestBodyRoot, channelMerchant);
        // 6. 解析报文
        return parseResponseTemplate(request, channelOutRequestBodyRoot, httpResponseBody, channelMerchant);
    }


    private PaymentResultVO<UpstreamData> parseResponseTemplate(UpstreamRequest request, Root<ChannelOutRequestBody> channelOutRequestBodyRoot, String httpResponseBody, ChannelMerchant merchant) {
        // 1.解析无规则报文
        Root<ChannelOutResponseBody> channelOutResponseBodyRoot = parseXmlToChannelModel(httpResponseBody);
        // 2. 解密信封
        decryptEnvelope(channelOutResponseBodyRoot, merchant);
        // 3. 解密字段
        decryptSensitiveFields(channelOutResponseBodyRoot);
        // 4. 验签（对方会根据 signCertSn 找证书）
        verify(channelOutResponseBodyRoot, merchant);
        // 5. 子类转换
        PaymentResultVO<UpstreamData> result = convertToResponse(request, channelOutRequestBodyRoot, channelOutResponseBodyRoot.getChannelBody());

        //业务
        sendEvent(request, channelOutRequestBodyRoot, channelOutResponseBodyRoot, result, merchant);

        return result;
    }

    private void sendEvent(UpstreamRequest request, Root<ChannelOutRequestBody> channelOutRequestBodyRoot, Root<ChannelOutResponseBody> channelOutResponseBodyRoot, PaymentResultVO<UpstreamData> result, ChannelMerchant merchant) {
        InstructionLogExtPO instructionLogExtPO = new InstructionLogExtPO();
        instructionLogExtPO.setInstructionType(getInstructionType().name());
        instructionLogExtPO.setMerchantNo(merchant.getMerchantNo());
        instructionLogExtPO.setUpstreamRequestNo(request.getUpstreamRequestNo());
        instructionLogExtPO.setChannelCode(request.getChannelCode());

        instructionLogExtPO.setRawReqContent(XMLUtils.toXML(request));
        instructionLogExtPO.setRawResContent(XMLUtils.toXML(result));
        instructionLogExtPO.setRawChannelRequestContent(XMLUtils.toXML(channelOutRequestBodyRoot));
        instructionLogExtPO.setRawChannelResponseContent(XMLUtils.toXML(channelOutResponseBodyRoot));
        instructionLogExtService.save(instructionLogExtPO);
    }

    @NotNull
    private Root<ChannelOutResponseBody> parseXmlToChannelModel(String rawRes) {
        XMLUtils.XmlParseInfo info = XMLUtils.parseDirtyXml(rawRes);
        Root<ChannelOutResponseBody> channelOutResponseBodyRoot = XMLUtils.toRootBean(info.getCleanXml(), getChannelOutResponseBodyClass());
        channelOutResponseBodyRoot.setSign(info.getSignature());
        return channelOutResponseBodyRoot;
    }

    private Class<?> getChannelOutResponseBodyClass() {
        return (Class<?>) TypeUtil.getTypeArgument(getClass(), 3);
    }


    private void decryptEnvelope(Root<ChannelOutResponseBody> channelOutResponseBodyRoot, ChannelMerchant channelMerchant) {
        Envelope envelope = Envelope.parseEnvelope(asymmetricCryptoFactory.get(channelMerchant.getEncryptDigitalEnvelopeType()).
                decrypt(channelOutResponseBodyRoot.getHeader().getDigitalEnvelope(), channelMerchant, channelOutResponseBodyRoot.getHeader().getEncryptionSerialNumber()));
        channelOutResponseBodyRoot.setEnvelope(envelope);
    }


    private void verify(Root<ChannelOutResponseBody> root, ChannelMerchant channelMerchant) {
        if (!signatureCryptoFactory.get(channelMerchant.getSignType()).verify(XMLUtils.toXML(root), root.getSign(), channelMerchant, root.getHeader().getSignSerialNumber())) {
            throw new RuntimeException("验签失败");
        }
    }


    private void decryptSensitiveFields(Root<ChannelOutResponseBody> root) {
        Envelope envelope = root.getEnvelope();
        AbstractEncryptDecryptHandler.decrypt(root.getChannelBody(), envelope.getSymmetricKey(), symmetricCryptoFactory.get(envelope.getSymmetricAlgorithm())::decrypt);
    }


    private void encryptSensitiveFields(Root<ChannelOutRequestBody> root) {
        Envelope envelope = root.getEnvelope();
        AbstractEncryptDecryptHandler.encrypt(root.getChannelBody(), envelope.getSymmetricKey(), symmetricCryptoFactory.get(envelope.getSymmetricAlgorithm())::encrypt);
    }


    private String encryptEnvelope(Envelope envelope, ChannelMerchant channelMerchant) {
        return asymmetricCryptoFactory.get(channelMerchant.getEncryptDigitalEnvelopeType()).encrypt(envelope.buildEnvelope(), channelMerchant, channelMerchant.getEncryptionSerialNumber());
    }

    private void doSign(Root<ChannelOutRequestBody> root, ChannelMerchant channelMerchant) {
        root.setSign(signatureCryptoFactory.get(channelMerchant.getSignType()).sign(XMLUtils.toXML(root), channelMerchant, channelMerchant.getSignSerialNumber()));
    }


    private void generateEnvelope(Root<ChannelOutRequestBody> root, ChannelMerchant channelMerchant) {
        Envelope envelope = new Envelope();
        envelope.setSymmetricKey(symmetricCryptoFactory.get(channelMerchant.getEncryptType()).generateSymmetricKey());
        envelope.setSymmetricAlgorithm(channelMerchant.getEncryptType());
        root.setEnvelope(envelope);

        String encryptEnvelope = encryptEnvelope(envelope, channelMerchant);
        root.getHeader().setDigitalEnvelope(encryptEnvelope);
    }


    private String doHttpExchange(Root<ChannelOutRequestBody> root, ChannelMerchant channelMerchant) {
        Map<String, String> headers = httpHeaders(root, channelMerchant);
        return HttpClient.post(channelMerchant.getChannelUrl(), headers, XMLUtils.buildDirtyXml(root, root.getSign()), MediaType.APPLICATION_XML_VALUE);
    }

    protected Map<String, String> httpHeaders(Root<ChannelOutRequestBody> root, ChannelMerchant channelMerchant) {
        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("OriIssrId", channelMerchant.getIssuerId());
        httpHeaders.put("MsgTp", root.getHeader().getMessageType());
        httpHeaders.put("PyerAcctTp", "00");
        httpHeaders.put("PyeeAcctTp", "04");
        httpHeaders.put("ReservedField", "1000F0617T8800");
        httpHeaders.put("TrxCtgy", "0202");
        return httpHeaders;
    }


}