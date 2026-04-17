package org.example.service.security.sign;

import org.example.repository.ChannelMerchant;

public interface SignatureCrypto {

    String sign(String plainText, ChannelMerchant channelMerchant, String certSn);

    boolean verify(String plainText, String sign, ChannelMerchant channelMerchant, String certSn);
}