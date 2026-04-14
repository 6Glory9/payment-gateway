package org.example.repository;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class PublicCert implements Serializable {

    //公钥序列号
    private String publicCertSerialNumber;


    //公钥的base64
    private String publicCertBase64;
}
