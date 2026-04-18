package org.example.service.cache;

public interface MerchantCache {

    ChannelMerchant get(String merchantNo);

    ChannelMerchant getFromChannel(String issuerId);

}
