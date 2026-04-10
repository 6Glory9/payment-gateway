package org.example.repository;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class MerchantInfo implements Serializable {

    private String merchantNo;

    private String algorithmType;

    private String algorithmKey;

    private String issuerId;

    private String signSerialNumber;

    private String encryptionSerialNumber;

    private String channelUrl;

    public String generateDigitalEnvelope() {
        return "";
    }

    public String sign(String jsonStr) {
        return "sign";
    }

    public String encrypt(String data) {
        return null;
    }

    public String decrypt(String encryptData) {
        return null;
    }

    public void checkSign(String jsonStr, String toMatchSign) {

    }
}
