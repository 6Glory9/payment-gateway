package org.example.service.encode;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.DES;

import java.nio.charset.StandardCharsets;

public class DESEncryptDecryptHandler implements EncryptDecryptHandler {


    @Override
    public String type() {
        return "DES";
    }

    @Override
    public String generateKey() {
        return SecureUtil.generateKey(type()).getFormat();
    }

    @Override
    public String encrypt(String value, String key) {
        DES des = new DES(key.getBytes(StandardCharsets.UTF_8));
        return des.encryptBase64(value.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String decrypt(String encryptValue, String key) {
        DES des = new DES(key.getBytes(StandardCharsets.UTF_8));
        return des.decryptStr(encryptValue);
    }
}
