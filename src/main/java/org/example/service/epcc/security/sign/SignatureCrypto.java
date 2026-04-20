package org.example.service.epcc.security.sign;

import org.example.repository.cache.ChannelMerchant;

public interface SignatureCrypto {

    String sign(String plainText, ChannelMerchant channelMerchant, String certSn);

    boolean verify(String plainText, String sign, ChannelMerchant channelMerchant, String certSn);
}