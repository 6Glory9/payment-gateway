package org.example.service.security.symmetric;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SymmetricCryptoFactory {

    private final Map<String, SymmetricCrypto> map;

    @Autowired
    public SymmetricCryptoFactory(Map<String, SymmetricCrypto> map) {
        this.map = map;
    }

    public SymmetricCrypto get(String algorithm) {
        SymmetricCrypto crypto = map.get(algorithm);
        if (crypto == null) throw new RuntimeException("不支持算法：" + algorithm);
        return crypto;
    }
}