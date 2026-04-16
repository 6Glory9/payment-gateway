package org.example.service;

import cn.hutool.crypto.symmetric.SymmetricDecryptor;
import cn.hutool.crypto.symmetric.SymmetricEncryptor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.repository.DigitalEnvelope;
import org.example.repository.MerchantInfo;
import org.example.repository.PrivateCert;
import org.example.service.security.SecurityService;
import org.example.service.security.SecurityServiceImpl;
import org.example.utils.XMLUtils;

@Setter
@Getter
@Slf4j
public class EpccContext<UpstreamRequest extends PaymentRequest, UpstreamData, ChannelOutRequest extends EpccChannelOut, ChannelOutResponse extends EpccChannelOut> extends Context<UpstreamRequest, UpstreamData> {

    private ChannelOutRequest channelOutRequest;

    private DigitalEnvelope requestDigitalEnvelope;

    private Root<ChannelOutRequest> requestRoot;

    private String requestSign;


    private Root<ChannelOutResponse> responseRoot;

    private String responseSign;

    private DigitalEnvelope responseDigitalEnvelope;

    private SecurityService securityService;

    public EpccContext(UpstreamRequest upstreamRequest, MerchantInfo merchantInfo) {
        super(upstreamRequest, merchantInfo);
        this.securityService = new SecurityServiceImpl(merchantInfo);
    }


    public void secureRequest() {
        // 1. 生成数字信封（内部持有）
        DigitalEnvelope envelope = this.generateDigitalEnvelope();

        // 2. 敏感信息脱敏/加密
        // 屏蔽 SymmetricEncryptor 的获取细节，直接在 context 内部闭环
        SymmetricEncryptor encryptor = securityService.generateSymmetricEncryptor(envelope);
        AbstractEncryptDecryptHandler.encrypt(this.channelOutRequest, encryptor::encryptBase64);

        // 3. 构建 Root 并加签
        this.requestRoot = new Root<>(this.channelOutRequest, this);
        String xmlToSign = XMLUtils.toXML(this.requestRoot);
        this.requestSign = securityService.sign(xmlToSign);
    }

    /**
     * 【闭环核心】解拆报文：一键完成验签和解密
     */
    public void deconstructResponse(String rawRes) {
        // 1. 结构解析
        XMLUtils.XmlParseInfo parseInfo = XMLUtils.parseDirtyXml(rawRes);
        this.responseRoot = XMLUtils.toRootBean(parseInfo.getCleanXml());

        // 2. 验签（内部调用 securityService）
        boolean isValid = securityService.verify(
                responseRoot.getHeader().getSignSerialNumber(),
                XMLUtils.toXML(responseRoot),
                parseInfo.getSignature()
        );
        if (!isValid) throw new SecurityException("渠道返回报文验签失败");

        // 3. 解密 Body
        DigitalEnvelope resEnvelope = extractResponseDigitalEnvelope();
        SymmetricDecryptor decryptor = securityService.generateSymmetricDecryptor(resEnvelope);
        AbstractEncryptDecryptHandler.decrypt(responseRoot.getChannelOutBody(), decryptor::decryptStr);
    }


    private DigitalEnvelope extractResponseDigitalEnvelope() {
        if (responseRoot == null || responseRoot.getHeader() == null || responseRoot.getHeader().getDigitalEnvelope() == null || responseRoot.getHeader().getEncryptionSerialNumber() == null) {
            log.info("没有机会获取返回的加密信封");
            return null;
        }

        this.responseDigitalEnvelope = securityService.parseDigitalEnvelope(responseRoot.getHeader().getEncryptionSerialNumber(), responseRoot.getHeader().getDigitalEnvelope());
        return responseDigitalEnvelope;
    }

    private DigitalEnvelope generateDigitalEnvelope() {
        requestDigitalEnvelope = securityService.generateDigitalEnvelope();
        return requestDigitalEnvelope;
    }
}
