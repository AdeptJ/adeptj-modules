package com.adeptj.modules.commons.crypto.internal;

import com.adeptj.modules.commons.crypto.EncryptionService;
import org.osgi.service.component.annotations.Component;

@Component
public class EncryptionServiceImpl implements EncryptionService {

    @Override
    public String encrypt(String plainText) {
        return null;
    }

    @Override
    public String decrypt(String encryptedText) {
        return null;
    }
}
