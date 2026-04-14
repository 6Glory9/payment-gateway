package org.example.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.repository.DigitalEnvelope;
import org.example.repository.PrivateCert;

@Setter
@Getter
@Slf4j
public class EpccContext<ChannelOutRequest extends EpccChannelOut, ChannelOutResponse extends EpccChannelOut> extends Context<ChannelOutRequest, ChannelOutResponse> {


    private Root<ChannelOutRequest> requestRoot;

    private String requestSign;

    private DigitalEnvelope requestDigitalEnvelope;


    private Root<ChannelOutResponse> responseRoot;

    private String responseSign;

    private DigitalEnvelope responseDigitalEnvelope;


    public void extractResponseDigitalEnvelope() {
        if (responseRoot == null || responseRoot.getHeader() == null || responseRoot.getHeader().getDigitalEnvelope() == null || responseRoot.getHeader().getEncryptionSerialNumber() == null) {
            log.info("没有机会获取返回的加密信封");
            return;
        }

        PrivateCert selectPrivateCert = null;
        if (this.getMerchantInfo().getCurrentPrivateCert().getPublicCert().getPublicCertSerialNumber().equals(responseRoot.getHeader().getEncryptionSerialNumber())) {
            log.info("返回序列号选择当前使用的私钥");
            selectPrivateCert = this.getMerchantInfo().getCurrentPrivateCert();
        }

        if (this.getMerchantInfo().getHistoryPrivateCert().get(responseRoot.getHeader().getEncryptionSerialNumber()) != null) {
            log.info("返回序列号选择历史使用的私钥,只是短时出现情况");
            selectPrivateCert = this.getMerchantInfo().getHistoryPrivateCert().get(responseRoot.getHeader().getEncryptionSerialNumber());
        }

        if (selectPrivateCert == null) {
            log.error("没有找到合适的私钥解密");
            throw new RuntimeException("没有找到合适的私钥解密");
        }
        this.responseDigitalEnvelope = AbstractEncryptDecryptHandler.decryptDigitalEnvelope(responseRoot.getHeader().getDigitalEnvelope(), selectPrivateCert.getPrivateCertBase64(), selectPrivateCert.getDecryptDigitalEnvelopeType());
    }

    public DigitalEnvelope generateDigitalEnvelope() {
        return AbstractEncryptDecryptHandler.generateDigitalEnvelope(this.getMerchantInfo().getCurrentPublicCert().getPublicCertBase64(), this.getMerchantInfo().getCurrentPublicCert().getEncryptDigitalEnvelopeType());

    }
}
