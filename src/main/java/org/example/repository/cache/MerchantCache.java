package org.example.repository.cache;

public interface MerchantCache {

    ChannelMerchant get(String merchantNo);

    ChannelMerchant getFromChannel(String issuerId);

}
