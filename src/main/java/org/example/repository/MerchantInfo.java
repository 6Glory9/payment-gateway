package org.example.repository;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Setter
@Getter
public class MerchantInfo implements Serializable {
    //商户号
    private String merchantNo;
    //渠道商户标记
    private String issuerId;
    //渠道请求地址
    private String channelUrl;

    private String encryptType = "SM4";

    //当前商户自己的公私钥
    private PrivateCert currentPrivateCert;

    //当前渠道的公钥
    private PublicCert currentPublicCert;

    //渠道用商户公钥证书签名的序列号
    private String channelSignSerialNumber;

    //商户用的渠道证书做DigitalEnvelope加密序列号
    private String encryptDigitalEnvelopeSerialNumber;


    //商户自己使用过的公私钥
    private Map<String, PrivateCert> historyPrivateCert;

    //渠道下发的公钥
    private Map<String, PublicCert> historyPublicCert;
}
