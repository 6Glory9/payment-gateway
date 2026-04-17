package org.example.service.security.asymmetric;

import org.example.repository.ChannelMerchant;

/**
 * 非对称加解密（信封专用）
 * 扩展：RSA / SM2 只需要新增实现类即可
 */
public interface AsymmetricCrypto {

    /**
     * 加密
     *
     * @param plainText 明文（信封JSON）
     * @param merchant  渠道商户
     * @param encCertSn 加密证书序列号（用于找到对应证书）
     * @return 密文
     */
    String encrypt(String plainText, ChannelMerchant merchant, String encCertSn);

    /**
     * 解密
     *
     * @param cipherText 密文
     * @param merchant   渠道商户
     * @param encCertSn  加密证书序列号
     * @return 明文
     */
    String decrypt(String cipherText, ChannelMerchant merchant, String encCertSn);
}