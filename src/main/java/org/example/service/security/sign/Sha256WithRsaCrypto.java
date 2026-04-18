package org.example.service.security.sign;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SignUtil;
import org.example.service.cache.ChannelCertificate;
import org.example.service.cache.ChannelMerchant;
import org.example.service.security.AbstractCrypto;
import org.springframework.stereotype.Component;

import static cn.hutool.crypto.asymmetric.SignAlgorithm.SHA256withRSA;

@Component("SHA256withRSA")
public class Sha256WithRsaCrypto extends AbstractCrypto implements SignatureCrypto {

    @Override
    public String sign(String plainText, ChannelMerchant channelMerchant, String certSn) {
        // 1. 根据SN获取我方证书（带私钥）
        ChannelCertificate cert = getCertBySn(channelMerchant, certSn);

        // 2. 从pfx解析私钥（Base64）
        String privateKeyBase64 = cert.getPfxBase64();

        // 3. Hutool SHA256withRSA 签名

        return SignUtil.sign(SHA256withRSA, null, privateKeyBase64).signHex(plainText);
    }

    @Override
    public boolean verify(String plainText, String sign, ChannelMerchant channelMerchant, String certSn) {
        // 1. 根据SN获取渠道证书（只有公钥）
        ChannelCertificate cert = getCertBySn(channelMerchant, certSn);

        // 2. 从证书解析公钥
        String publicKeyBase64 = cert.getCertBase64();

        // 3. Hutool SHA256withRSA 验签
        return SignUtil.sign(SHA256withRSA, publicKeyBase64, null).verify(StrUtil.utf8Bytes(plainText), HexUtil.decodeHex(sign));
    }
}