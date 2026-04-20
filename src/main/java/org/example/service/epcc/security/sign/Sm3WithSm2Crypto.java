package org.example.service.epcc.security.sign;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.asymmetric.SM2;
import org.example.repository.cache.ChannelCertificate;
import org.example.repository.cache.ChannelMerchant;
import org.example.service.epcc.security.AbstractCrypto;
import org.springframework.stereotype.Component;

@Component("SM3withSM2")
public class Sm3WithSm2Crypto extends AbstractCrypto implements SignatureCrypto {

    @Override
    public String sign(String plainText, ChannelMerchant config, String certSn) {
        ChannelCertificate cert = getCertBySn(config, certSn);
        String privateKeyBase64 = cert.getPfxBase64();
        return new SM2(privateKeyBase64, null).signHex(HexUtil.encodeHexStr(plainText));
    }

    @Override
    public boolean verify(String plainText, String sign, ChannelMerchant config, String certSn) {
        ChannelCertificate cert = getCertBySn(config, certSn);
        String publicKeyBase64 = cert.getCertBase64();
        return new SM2(null, publicKeyBase64).verifyHex(HexUtil.encodeHexStr(plainText), sign);
    }
}