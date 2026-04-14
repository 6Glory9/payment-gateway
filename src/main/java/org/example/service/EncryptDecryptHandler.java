package org.example.service;

public interface EncryptDecryptHandler {

    String type();

    String generateKey();

    String encrypt(String value, String key);

    String decrypt(String encryptValue, String key);

}
