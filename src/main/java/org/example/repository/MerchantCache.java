package org.example.repository;

public interface MerchantCache {

    ChannelMerchant get(String merchantNo);

    ChannelMerchant getFromChannel(String issuerId);

}
