package com.adeptj.modules.commons.crypto;

public interface EncryptionService {

    String encrypt(String plainText);

    String decrypt(String encryptedText);
}
