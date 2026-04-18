package org.example.service.security;

import org.example.service.cache.ChannelCertificate;
import org.example.service.cache.ChannelMerchant;

public abstract class AbstractCrypto {

    protected ChannelCertificate getCertBySn(ChannelMerchant channelMerchant, String certSn) {
        ChannelCertificate channelCertificate = channelMerchant.getChannelCertificateMapper().get(certSn);
        if (channelCertificate == null) {
            throw new RuntimeException("证书不存在：" + certSn);
        }
        return channelCertificate;
    }

}