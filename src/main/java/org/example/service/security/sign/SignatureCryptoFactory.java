package org.example.service.security.sign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SignatureCryptoFactory {
    private final Map<String, SignatureCrypto> map;

    @Autowired
    public SignatureCryptoFactory(Map<String, SignatureCrypto> map) {
        this.map = map;
    }

    public SignatureCrypto get(String algorithm) {
        SignatureCrypto crypto = map.get(algorithm);
        if (crypto == null) throw new RuntimeException("不支持算法：" + algorithm);
        return crypto;
    }
}