package org.example.repository;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class PrivateCert implements Serializable {

    //私钥的base64
    private String privateCertBase64;


    private PublicCert publicCert;
}
