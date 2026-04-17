package org.example.repository;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class ChannelMerchant implements Serializable {
    //商户号
    private String merchantNo;
    //渠道商户标记
    private String issuerId;
    //渠道请求地址
    private String channelUrl;

    //签名的类型
    private String signType = "SM2";

    //信封加解密类型
    private String encryptDigitalEnvelopeType = "RSA";

    //敏感信息对称加解密类型
    private String encryptType = "SM4";

    //当前商户自己的公私钥
    private PrivateCert currentPrivateCert;

    //当前渠道的公钥
    private PublicCert currentPublicCert;


    //商户自己使用过的公私钥
    private Map<String, PrivateCert> historyPrivateCert = new HashMap<>();

    //渠道下发的公钥
    private Map<String, PublicCert> historyPublicCert = new HashMap<>();

    private List<ChannelCertificate> channelCertificateList = new ArrayList<>();
}
