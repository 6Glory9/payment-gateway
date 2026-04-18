package org.example.service.epcc;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.TypeUtil;
import org.example.repository.ChannelMerchant;
import org.example.repository.Envelope;
import org.example.repository.MerchantCache;
import org.example.service.ChannelService;
import org.example.service.GatewayEngine;
import org.example.service.PaymentResultVO;
import org.example.service.Request;
import org.example.service.security.asymmetric.AsymmetricCryptoFactory;
import org.example.service.security.sign.SignatureCryptoFactory;
import org.example.service.security.symmetric.SymmetricCryptoFactory;
import org.example.utils.HttpClient;
import org.example.utils.XMLUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.example.service.impl.MessageType.EPCC_401_001_01;

public abstract class AbstractEpccChannelService<UpstreamRequest extends Request, UpstreamData, ChannelOutRequestBody, ChannelOutResponseBody> implements ChannelService<UpstreamRequest, UpstreamData> {


    @Autowired
    private GatewayEngine<Request, String> gatewayEngine;


    @Autowired
    private MerchantCache channelMerchantCache;

    @Autowired
    private SymmetricCryptoFactory symmetricCryptoFactory;

    @Autowired
    private SignatureCryptoFactory signatureCryptoFactory;


    @Autowired
    private AsymmetricCryptoFactory asymmetricCryptoFactory;


    @Override
    public String generateChannelReqNo(UpstreamRequest upstreamRequest) {
        Request request = new Request();
        request.setChannelCode(EPCC_401_001_01.code());
        request.setUpstreamRequestNo(IdUtil.getSnowflakeNextIdStr());
        // request.setMerchantNo(channelMerchantCache);

        PaymentResultVO<String> resultVO = gatewayEngine.execute(request);

        return resultVO.getData();
    }

    private Root<ChannelOutRequestBody> convertToChannelModel(UpstreamRequest request, ChannelMerchant channelMerchant) {
        Root<ChannelOutRequestBody> channelOutRequestBodyRoot = new Root<>();
        EpccChannelOutRequestHeader header = new EpccChannelOutRequestHeader();
        header.setEncryptionSerialNumber(channelMerchant.getEncryptionSerialNumber());
        header.setSignSerialNumber(channelMerchant.getSignSerialNumber());
        header.setIssuerId(channelMerchant.getIssuerId());
        header.setMessageType(getChannelCode());
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