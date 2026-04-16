package org.example.service.security;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.AsymmetricCrypto;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import cn.hutool.crypto.symmetric.SymmetricDecryptor;
import cn.hutool.crypto.symmetric.SymmetricEncryptor;
import org.example.repository.DigitalEnvelope;
import org.example.repository.MerchantInfo;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class SecurityServiceImpl implements SecurityService {

    private MerchantInfo merchantInfo;

    public SecurityServiceImpl(MerchantInfo merchantInfo) {
        this.merchantInfo = merchantInfo;
    }

    @Override
    public String merchantNo() {
        return merchantInfo.getMerchantNo();
    }

    @Override
    public String sign(String value) {
        if ("SM2".equals(merchantInfo.getSignType())) {
            return new SM2(merchantInfo.getCurrentPrivateCert().getPrivateCertBase64(), null).signHex(HexUtil.encodeHexStr(value));
        }

        return new Sign(merchantInfo.getSignType(), merchantInfo.getCurrentPrivateCert().getPrivateCertBase64(), null).signHex(HexUtil.encodeHexStr(value));
    }

    @Override
    public boolean verify(String channelCertSerialNumber, String value, String toCheckSign) {
        String channelCertBase64 = null;
        if (channelCertSerialNumber.equals(merchantInfo.getCurrentPublicCert().getPublicCertSerialNumber())) {
            channelCertBase64 = merchantInfo.getCurrentPublicCert().getPublicCertBase64();
        } else if (merchantInfo.getHistoryPublicCert()!=null && merchantInfo.getHistoryPublicCert().get(channelCertSerialNumber) != null) {
            channelCertBase64 = merchantInfo.getHistoryPublicCert().get(channelCertSerialNumber).getPublicCertBase64();
        }

        if (channelCertBase64 == null) {
            throw new RuntimeException("没有找到渠道的证书");
        }

       return new Sign(merchantInfo.getSignType(), null, channelCertBase64).verify(value.getBytes(StandardCharsets.UTF_8), toCheckSign.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public DigitalEnvelope generateDigitalEnvelope() {
        SecretKey secretKey = SecureUtil.generateKey(merchantInfo.getEncryptType());

        String base64 = null;
        if (merchantInfo.getEncryptDigitalEnvelopeType().equals("SM2")) {
            SM2 sm2 = new SM2(null, merchantInfo.getCurrentPublicCert().getPublicCertBase64());
            base64 = sm2.encryptBase64(secretKey.getAlgorithm() + "|" + StrUtil.utf8Str(secretKey.getEncoded()), KeyType.PublicKey);
        } else if (merchantInfo.getEncryptDigitalEnvelopeType().equals("RSA") || "ECIES".equals(merchantInfo.getEncryptDigitalEnvelopeType())) {
            AsymmetricCrypto asymmetricCrypto = new AsymmetricCrypto(merchantInfo.getEncryptDigitalEnvelopeType(), null, merchantInfo.getCurrentPublicCert().getPublicCertBase64());
            base64 = asymmetricCrypto.encryptBase64(secretKey.getAlgorithm() + "|" + StrUtil.utf8Str(secretKey.getEncoded()), KeyType.PublicKey);
        }
        return new DigitalEnvelope(secretKey.getAlgorithm(), StrUtil.utf8Str(secretKey.getEncoded()), base64);
    }

    @Override
    public DigitalEnvelope parseDigitalEnvelope(String certSerialNumber, String base64) {
        String privateCertBase64 = null;
        if (merchantInfo.getCurrentPrivateCert().getPublicCert().getPublicCertSerialNumber().equals(certSerialNumber)) {
            privateCertBase64 = merchantInfo.getCurrentPrivateCert().getPrivateCertBase64();
        } else if (merchantInfo.getHistoryPrivateCert().get(certSerialNumber) != null) {
            privateCertBase64 = merchantInfo.getHistoryPrivateCert().get(certSerialNumber).getPrivateCertBase64();
        }
        if (privateCertBase64 == null) {
            throw new RuntimeException("没有找到自己的证书");
        }
        String[] pair = null;
        if (merchantInfo.getEncryptDigitalEnvelopeType().equals("SM2")) {
            SM2 sm2 = new SM2(privateCertBase64, null);
            pair = sm2.decryptStr(base64, KeyType.PrivateKey).split("|");
        } else if (merchantInfo.getEncryptDigitalEnvelopeType().equals("RSA") || "ECIES".equals(merchantInfo.getEncryptDigitalEnvelopeType())) {
            AsymmetricCrypto asymmetricCrypto = new AsymmetricCrypto(merchantInfo.getEncryptDigitalEnvelopeType(), privateCertBase64, null);
            pair = asymmetricCrypto.decryptStr(base64, KeyType.PrivateKey).split("|");
        }
        return new DigitalEnvelope(pair[0], pair[1], base64);
    }

    @Override
    public SymmetricEncryptor generateSymmetricEncryptor(DigitalEnvelope digitalEnvelope) {
        return new SymmetricCrypto(digitalEnvelope.getAlgorithmType(), digitalEnvelope.getAlgorithmType().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public SymmetricDecryptor generateSymmetricDecryptor(DigitalEnvelope digitalEnvelope) {
        return new SymmetricCrypto(digitalEnvelope.getAlgorithmType(), digitalEnvelope.getAlgorithmType().getBytes(StandardCharsets.UTF_8));
    }
}
