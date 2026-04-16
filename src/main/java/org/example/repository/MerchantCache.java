package org.example.repository;

public interface MerchantCache {

    MerchantInfo get(String merchantNo);

    MerchantInfo getFromChannel(String issuerId);

}
