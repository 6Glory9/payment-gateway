package org.example.service.security;

import org.example.repository.ChannelCertificate;
import org.example.repository.ChannelMerchant;

public abstract class AbstractCrypto {

    protected ChannelCertificate getCertBySn(ChannelMerchant channelMerchant, String certSn) {
        ChannelCertificate channelCertificate = channelMerchant.getChannelCertificateMapper().get(certSn);
        if (channelCertificate == null) {
            throw new RuntimeException("证书不存在：" + certSn);
        }
        return channelCertificate;
    }

}