package org.example.repository;

import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class Envelope {

    private static final String SEPARATOR = "|";
    //对称加解密KEY
    private String symmetricKey;
    //对称算法(AES/SM4) cn.hutool.crypto.symmetric.SymmetricAlgorithm
    private String symmetricAlgorithm;


    public String buildEnvelope() {
        if (!StringUtils.hasText(symmetricAlgorithm) || !StringUtils.hasText(symmetricKey)) {
            throw new IllegalArgumentException("对称算法/密钥不能为空");
        }
        return String.join(SEPARATOR, symmetricAlgorithm, symmetricKey);
    }

    public static Envelope parseEnvelope(String envelopeStr) {
        if (!StringUtils.hasText(envelopeStr)) {
            throw new IllegalArgumentException("信封内容不能为空");
        }

        String[] parts = envelopeStr.split("\\" + SEPARATOR);
        if (parts.length < 2) {
            throw new IllegalArgumentException("信封格式错误，正确格式：algorithm|key");
        }

        Envelope envelope = new Envelope();
        envelope.setSymmetricAlgorithm(parts[0]);
        envelope.setSymmetricKey(parts[1]);
        return envelope;
    }
}
