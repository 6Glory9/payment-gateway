package org.example.service.security;

import org.example.repository.ChannelCertificate;
import org.example.repository.ChannelMerchant;

import java.util.Objects;

public abstract class AbstractCrypto {

    protected ChannelCertificate getCertBySn(ChannelMerchant channelMerchant, String certSn) {
        return channelMerchant.getChannelCertificateList().stream()
                .filter(c -> Objects.equals(c.getCertSn(), certSn))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("证书不存在：" + certSn));
    }
}