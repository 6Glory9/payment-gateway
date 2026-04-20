package org.example.service.epcc.security.asymmetric;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import org.example.repository.cache.ChannelCertificate;
import org.example.repository.cache.ChannelMerchant;
import org.example.service.epcc.security.AbstractCrypto;
import org.springframework.stereotype.Component;

/**
 * RSA 实现
 */
@Component("RSA")
public class RsaAsymmetricCrypto extends AbstractCrypto implements AsymmetricCrypto {

    @Override
    public String encrypt(String plainText, ChannelMerchant merchant, String encCertSn) {
        ChannelCertificate certInfo = getCertBySn(merchant, encCertSn);
        String publicKeyBase64 = certInfo.getCertBase64();
        return new RSA(null, publicKeyBase64).encryptBase64(plainText, KeyType.PublicKey);
    }

    @Override
    public String decrypt(String cipherText, ChannelMerchant merchant, String encCertSn) {
        ChannelCertificate certInfo = getCertBySn(merchant, encCertSn);
        String privateKeyBase64 = certInfo.getPfxBase64();
        return new RSA(privateKeyBase64, null).decryptStr(cipherText, KeyType.PrivateKey);
    }
}