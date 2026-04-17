package org.example.service.security.asymmetric;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import org.example.repository.ChannelCertificate;
import org.example.repository.ChannelMerchant;
import org.example.service.security.AbstractCrypto;
import org.springframework.stereotype.Component;

/**
 * 国密 SM2 实现
 */
@Component("SM2")
public class Sm2AsymmetricCrypto extends AbstractCrypto implements AsymmetricCrypto {

    @Override
    public String encrypt(String plainText, ChannelMerchant merchant, String encCertSn) {
        // 1. 根据证书序列号获取证书（公钥）
        ChannelCertificate certInfo = getCertBySn(merchant, encCertSn);

        // 2. 获取 SM2 公钥
        String publicKeyBase64 = certInfo.getCertBase64();

        // 3. 执行 SM2 加密
        return new SM2(null, publicKeyBase64).encryptBase64(plainText, KeyType.PublicKey);
    }

    @Override
    public String decrypt(String cipherText, ChannelMerchant merchant, String encCertSn) {
        ChannelCertificate certInfo = getCertBySn(merchant, encCertSn);
        String privateKeyBase64 = certInfo.getPfxBase64();
        return new SM2(privateKeyBase64, null).decryptStr(cipherText, KeyType.PrivateKey);
    }
}