package org.example.service.encode;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SM4;

import java.nio.charset.StandardCharsets;

public class SM4EncryptDecryptHandler implements EncryptDecryptHandler {



    @Override
    public String type() {
        return "SM4";
    }

    @Override
    public String generateKey() {
        return SecureUtil.generateKey(type()).getFormat();
    }

    @Override
    public String encrypt(String value, String key) {
        SM4 sm4 = new SM4(key.getBytes(StandardCharsets.UTF_8));
        return sm4.encryptBase64(value.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String decrypt(String encryptValue, String key) {
        SM4 sm4 = new SM4(key.getBytes(StandardCharsets.UTF_8));
        return sm4.decryptStr(encryptValue);
    }
}
