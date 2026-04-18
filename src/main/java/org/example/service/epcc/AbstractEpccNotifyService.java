package org.example.service.epcc;


import cn.hutool.core.util.TypeUtil;
import org.example.service.cache.ChannelMerchant;
import org.example.service.cache.Envelope;
import org.example.service.cache.MerchantCache;
import org.example.service.NotifyResultVO;
import org.example.service.NotifyService;
import org.example.service.security.asymmetric.AsymmetricCryptoFactory;
import org.example.service.security.sign.SignatureCryptoFactory;
import org.example.service.security.symmetric.SymmetricCryptoFactory;
import org.example.utils.XMLUtils;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractEpccNotifyService<ChannelInRequest, ChannelInResponse> implements NotifyService<ChannelInRequest, ChannelInResponse> {

    @Autowired
    private MerchantCache merchantCache;

    @Autowired
    private SymmetricCryptoFactory symmetricCryptoFactory;

    @Autowired
    private SignatureCryptoFactory signatureCryptoFactory;


    @Autowired
    private AsymmetricCryptoFactory asymmetricCryptoFactory;


    public NotifyResultVO<ChannelInRequest, ChannelInResponse> notify(String issuerId, String httpRequestBody) {

        ChannelMerchant channelMerchant = merchantCache.getFromChannel(issuerId);

        Root<ChannelInRequest> channelInRequestBodyRoot = parseXmlToChannelModel(httpRequestBody);

        decryptEnvelope(channelInRequestBodyRoot, channelMerchant);
        // 3. 解密字段
        decryptSensitiveFields(channelInRequestBodyRoot);
        // 4. 验签（对方会根据 signCertSn 找证书）
        verify(channelInRequestBodyRoot, channelMerchant);

        NotifyResultVO<ChannelInRequest, ChannelInResponse> result = convertToResponse(channelInRequestBodyRoot.getChannelBody(),channelMerchant);

        //业务
        sendEvent(channelInRequestBodyRoot, result, channelMerchant);

        return result;

    }

    private void sendEvent(Root<ChannelInRequest> channelInRequestBodyRoot, NotifyResultVO<ChannelInRequest, ChannelInResponse> result, ChannelMerchant channelMerchant) {
    }

    protected abstract NotifyResultVO<ChannelInRequest, ChannelInResponse> convertToResponse(ChannelInRequest channelInRequest,ChannelMerchant channelMerchant);


    private void decryptEnvelope(Root<ChannelInRequest> channelOutResponseBodyRoot, ChannelMerchant channelMerchant) {
        Envelope envelope = Envelope.parseEnvelope(asymmetricCryptoFactory.get(channelMerchant.getEncryptDigitalEnvelopeType()).
                decrypt(channelOutResponseBodyRoot.getHeader().getDigitalEnvelope(), channelMerchant, channelOutResponseBodyRoot.getHeader().getEncryptionSerialNumber()));
        channelOutResponseBodyRoot.setEnvelope(envelope);
    }


    private void verify(Root<ChannelInRequest> root, ChannelMerchant channelMerchant) {
        if (!signatureCryptoFactory.get(channelMerchant.getSignType()).verify(XMLUtils.toXML(root), root.getSign(), channelMerchant, root.getHeader().getSignSerialNumber())) {
            throw new RuntimeException("验签失败");
        }
    }


    private void decryptSensitiveFields(Root<ChannelInRequest> root) {
        Envelope envelope = root.getEnvelope();
        AbstractEncryptDecryptHandler.decrypt(root.getChannelBody(), envelope.getSymmetricKey(), symmetricCryptoFactory.get(envelope.getSymmetricAlgorithm())::decrypt);
    }

    private Root<ChannelInRequest> parseXmlToChannelModel(String rawRes) {
        XMLUtils.XmlParseInfo info = XMLUtils.parseDirtyXml(rawRes);
        Root<ChannelInRequest> channelInRequestBodyRoot = XMLUtils.toRootBean(info.getCleanXml(), getChannelInRequestBodyClass());
        channelInRequestBodyRoot.setSign(info.getSignature());
        return channelInRequestBodyRoot;
    }


    private Class<?> getChannelInRequestBodyClass() {
        return (Class<?>) TypeUtil.getTypeArgument(getClass());
    }


}