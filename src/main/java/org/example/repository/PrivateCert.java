package org.example.repository;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class PrivateCert implements Serializable {

    //私钥的base64
    private String privateCertBase64;

    //私钥签名类似
    //MD5,SM2,RSA
    private String signType;


    //解密DigitalEnvelope算法
    private String decryptDigitalEnvelopeType;


    private PublicCert publicCert;
}
