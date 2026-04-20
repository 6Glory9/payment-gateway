package org.example.service.epcc.security.symmetric;

import cn.hutool.crypto.symmetric.AES;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component("AES")
public class AESCrypto implements SymmetricCrypto {


    @Override
    public String encrypt(String data, String key) {
        return new AES(key.getBytes(StandardCharsets.UTF_8)).encryptBase64(data);
    }

    @Override
    public String decrypt(String data, String key) {
        return new AES(key.getBytes(StandardCharsets.UTF_8)).decryptStr(data);
    }
}