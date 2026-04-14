package org.example.service.encode;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;

import java.nio.charset.StandardCharsets;

public class AESEncryptDecryptHandler implements EncryptDecryptHandler {


    @Override
    public String type() {
        return "AES";
    }

    @Override
    public String generateKey() {
        return SecureUtil.generateKey(type()).getFormat();
    }

    @Override
    public String encrypt(String value, String key) {
        AES aes = new AES(key.getBytes(StandardCharsets.UTF_8));
        return aes.encryptBase64(value.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String decrypt(String encryptValue, String key) {
        AES aes = new AES(key.getBytes(StandardCharsets.UTF_8));
        return aes.decryptStr(encryptValue);
    }
}
