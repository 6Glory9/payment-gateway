package org.example.service.epcc.security.symmetric;

import cn.hutool.core.lang.UUID;

public interface SymmetricCrypto {

    default String generateSymmetricKey() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    String encrypt(String data, String key);

    String decrypt(String data, String key);
}