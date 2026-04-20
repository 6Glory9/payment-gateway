package org.example.service.epcc.security.symmetric;

import cn.hutool.crypto.symmetric.SM4;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component("SM4")
public class Sm4Crypto implements SymmetricCrypto {


    @Override
    public String encrypt(String data, String key) {
        return new SM4(key.getBytes(StandardCharsets.UTF_8)).encryptBase64(data);
    }

    @Override
    public String decrypt(String data, String key) {
        return new SM4(key.getBytes(StandardCharsets.UTF_8)).decryptStr(data);
    }
}