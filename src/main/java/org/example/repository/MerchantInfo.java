package org.example.repository;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class MerchantInfo implements Serializable {

    private String merchantNo;

    private String issuerId;

    private String channelUrl;


    private String algorithmType;

    private String algorithmKey;


    private String signSerialNumber;

    private String encryptionSerialNumber; //


    public String generateDigitalEnvelope() {
        return "";
    }

    public String sign(String jsonStr) {
        return "sign";
    }

    public String encrypt(String algorithmType, String algorithmKey, String data) {
        return null;
    }


    public String getDigitalEnvelope(String digitalEnvelope) {
        return null;
    }

    public String decrypt(String algorithmType, String algorithmKey, String encryptData) {
        return null;
    }

    public void checkSign(String jsonStr, String toMatchSign) {

    }
}
