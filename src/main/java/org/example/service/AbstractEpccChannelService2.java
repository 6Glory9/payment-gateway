package org.example.service;

import cn.hutool.core.lang.UUID;
import org.example.repository.ChannelMerchant;
import org.example.repository.Envelope;
import org.example.repository.MerchantCache;
import org.example.service.security.asymmetric.AsymmetricCryptoFactory;
import org.example.service.security.sign.SignatureCryptoFactory;
import org.example.service.security.symmetric.SymmetricCryptoFactory;
import org.example.utils.HttpClient;
import org.example.utils.XMLUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractEpccChannelService2<UpstreamRequest, UpstreamData, ChannelOutRequestBody, ChannelOutResponseBody> implements ChannelService<UpstreamRequest, UpstreamData> {


    @Autowired
    private GatewayEngine<PaymentRequest, String> gatewayEngine;


    @Autowired
    private MerchantCache merchantCache;

    @Autowired
    private SymmetricCryptoFactory symmetricCryptoFactory;

    @Autowired
    private SignatureCryptoFactory signatureCryptoFactory;


    @Autowired
    private AsymmetricCryptoFactory asymmetricCryptoFactory;


    @Override
    public String generateChannelReqNo(UpstreamRequest upstreamRequest) {
        return null;
    }

    protected abstract Root<ChannelOutRequestBody> convertToChannelModel(UpstreamRequest req, ChannelMerchant channelMerchant);

    protected abstract PaymentResultVO<UpstreamData> convertToResponse(ChannelOutResponseBody channelBody);


    @Override
    public PaymentResultVO<UpstreamData> call(UpstreamRequest request) {
        ChannelMerchant channelMerchant = merchantCache.get(null);
        // 1. 报文转换：平台标准模型 -> 渠道模型
        Root<ChannelOutRequestBody> root = convertToChannelModel(request, channelMerchant);
        // 2. 生成封信
        Envelope envelope = generateEnvelope(channelMerchant);
        String encryptEnvelope = encryptEnvelope(envelope, channelMerchant);
        root.getHeader().setDigitalEnvelope(encryptEnvelope);
        // 3. 签名
        String sign = doSign(encryptEnvelope, root, channelMerchant);
        // 4. 对敏感信息加密
        encryptSensitiveFields(root.getChannelBody(), envelope, channelMerchant);
        // 5. 构建不规则的报文格式
        String xml = XMLUtils.buildDirtyXml(root, sign);
        // 6. 通讯：发送 HTTP 请求
        String rawRes = doHttpExchange(xml, channelMerchant);
        // 7. 解析报文
        return parseResponseTemplate(rawRes, channelMerchant);
    }


    private PaymentResultVO<UpstreamData> parseResponseTemplate(String rawRes, ChannelMerchant merchant) {
        // 1.解析无规则报文
        XMLUtils.XmlParseInfo info = XMLUtils.parseDirtyXml(rawRes);
        // 2. 报文转成bean(ChannelOutResponseBody)
        Root<ChannelOutResponseBody> channelOutResponseBodyRoot = XMLUtils.toBean(info.getCleanXml());
        // 3. 解密信封
        Envelope envelope = decryptEnvelope(channelOutResponseBodyRoot.getHeader().getDigitalEnvelope(), merchant);
        // 4. 解密字段
        decryptSensitiveFields(channelOutResponseBodyRoot.getChannelBody(), envelope, merchant);
        // 5. 验签（对方会根据 signCertSn 找证书）
        String sign = info.getSignature();
        String plainData = XMLUtils.toXML(channelOutResponseBodyRoot);
        if (!verify(plainData, sign, merchant)) {
            throw new RuntimeException("验签失败");
        }
        // 4. 子类转换
        return convertToResponse(channelOutResponseBodyRoot.getChannelBody());
    }


    private Envelope decryptEnvelope(String encryptEnvelope, ChannelMerchant merchant) {
        return null;
    }

    private void decryptSensitiveFields(ChannelOutResponseBody channelBody, Envelope envelope, ChannelMerchant merchant) {

    }

    private boolean verify(String plainData, String sign, ChannelMerchant merchant) {
        return false;
    }


    private void encryptSensitiveFields(ChannelOutRequestBody channelBody, Envelope envelope, ChannelMerchant channelMerchant) {

    }

    private String doSign(String encryptEnvelope, Root<ChannelOutRequestBody> root, ChannelMerchant channelMerchant) {
        return null;
    }

    private String encryptEnvelope(Envelope envelope, ChannelMerchant channelMerchant) {
        return null;
    }


    private Envelope generateEnvelope(ChannelMerchant channelMerchant) {
        Envelope env = new Envelope();
        env.setSymmetricKey(symmetricCryptoFactory.get(channelMerchant.getEncryptDigitalEnvelopeType()).generateSymmetricKey());
        env.setSymmetricAlgorithm(channelMerchant.getEncryptType());
        env.setAsymmetricAlgorithm(channelMerchant.getEncryptDigitalEnvelopeType());
        env.setSignAlgorithm(channelMerchant.getSignType());
        return env;
    }


    private String doHttpExchange(String requestBody, ChannelMerchant channelMerchant) {
        Map<String, String> headers = httpHeaders();
        return HttpClient.post(channelMerchant.getChannelUrl(), headers, requestBody, MediaType.APPLICATION_XML_VALUE);
    }

    protected Map<String, String> httpHeaders() {
        Map<String, String> httpHeaders = new HashMap<>();
        //httpHeaders.put("OriIssrId", context.getMerchantInfo().getIssuerId());
        // httpHeaders.put("MsgTp", context.getChannelOutRequest().getMessageType());
        httpHeaders.put("PyerAcctTp", "00");
        httpHeaders.put("PyeeAcctTp", "04");
        httpHeaders.put("ReservedField", "1000F0617T8800");
        httpHeaders.put("TrxCtgy", "0202");
        return httpHeaders;
    }


}