package org.example.repository.cache;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
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
    private String signType = "SM3withSM2";

    //信封加密公钥的序列号
    private String encryptionSerialNumber;

    //信封加解密类型
    private String encryptDigitalEnvelopeType = "RSA";

    //敏感信息对称加解密类型
    private String encryptType = "SM4";

    private String signSerialNumber;


    private Map<String, ChannelCertificate> channelCertificateMapper;
}
