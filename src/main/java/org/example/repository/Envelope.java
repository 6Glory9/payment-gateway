package org.example.repository;

import lombok.Data;

@Data
public class Envelope {
    //对称加解密KEY
    private String symmetricKey;
    //对称算法(AES/SM4) cn.hutool.crypto.symmetric.SymmetricAlgorithm
    private String symmetricAlgorithm;
    //非对称算法RSA/SM2, 加密解密..对加密方法加密 cn.hutool.crypto.asymmetric.AsymmetricAlgorithm//SM2
    private String asymmetricAlgorithm;
    //非对称算法签名和验签 RSA/SM2 cn.hutool.crypto.asymmetric.SignAlgorithm//SM2
    private String signAlgorithm;
}
