package org.example.service.security.asymmetric;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AsymmetricCryptoFactory {
    private final Map<String, AsymmetricCrypto> map;

    @Autowired
    public AsymmetricCryptoFactory(Map<String, AsymmetricCrypto> map) {
        this.map = map;
    }

    public AsymmetricCrypto get(String algorithm) {
        AsymmetricCrypto crypto = map.get(algorithm.toLowerCase());
        if (crypto == null) throw new RuntimeException("不支持算法：" + algorithm);
        return crypto;
    }
}