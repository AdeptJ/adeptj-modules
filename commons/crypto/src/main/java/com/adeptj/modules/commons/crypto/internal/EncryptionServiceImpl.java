package com.adeptj.modules.commons.crypto.internal;

import com.adeptj.modules.commons.crypto.CryptoException;
import com.adeptj.modules.commons.crypto.EncryptionService;
import com.adeptj.modules.commons.crypto.Randomness;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.lang.invoke.MethodHandles;
import java.security.GeneralSecurityException;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

@Designate(ocd = EncryptionConfig.class)
@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class EncryptionServiceImpl implements EncryptionService {

    private static final Logger LOGGER = LoggerFactory.getLogger((MethodHandles.lookup().lookupClass()));

    private static final int IV_LENGTH_BYTE = 12;

    private static final String ALGO_GCM = "AES/GCM/NoPadding";

    private static final String ALGO_AES = "AES";

    private final int keySize;

    private final byte[] iv;

    private final byte[] key;

    @Activate
    public EncryptionServiceImpl(EncryptionConfig config) {
        this.keySize = config.keySize();
        this.iv = Randomness.randomBytes(IV_LENGTH_BYTE);
        this.key = Randomness.randomBytes(this.keySize / 8);
    }

    @Override
    public String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGO_GCM);
            cipher.init(ENCRYPT_MODE, new SecretKeySpec(this.key, ALGO_AES), new GCMParameterSpec(this.keySize, this.iv));
            byte[] encrypted = cipher.doFinal(plainText.getBytes(UTF_8));
            return new String(Base64.getEncoder().encode(encrypted), UTF_8);
        } catch (GeneralSecurityException ex) {
            throw new CryptoException(ex);
        }
    }

    @Override
    public String decrypt(String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGO_GCM);
            cipher.init(DECRYPT_MODE, new SecretKeySpec(this.key, ALGO_AES), new GCMParameterSpec(this.keySize, this.iv));
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedText.getBytes(UTF_8)));
            return new String(decrypted, UTF_8);
        } catch (GeneralSecurityException ex) {
            throw new CryptoException(ex);
        }
    }
}