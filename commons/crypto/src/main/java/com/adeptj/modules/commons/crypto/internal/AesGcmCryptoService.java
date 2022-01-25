/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://www.adeptj.com)                           #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/

package com.adeptj.modules.commons.crypto.internal;

import com.adeptj.modules.commons.crypto.CryptoException;
import com.adeptj.modules.commons.crypto.CryptoService;
import com.adeptj.modules.commons.crypto.CryptoUtil;
import com.adeptj.modules.commons.utils.RandomUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.lang.invoke.MethodHandles;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

/**
 * Service implementation for encrypting/decrypting any text using AES/GCM/NoPadding algo.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component
public class AesGcmCryptoService implements CryptoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final int IV_LENGTH = 12;

    private static final int SALT_LENGTH = 16;

    private static final String CIPHER_ALGO = "AES/GCM/NoPadding";

    private static final String SECRET_KEY_SPEC_ALGO = "AES";

    private static final String PROPERTY_CRYPTO_KEY = "crypto.key";

    private static final String PROPERTY_CRYPTO_ITERATIONS = "crypto.iterations";

    private static final int PBE_KEY_LENGTH = 128;

    private static final int GCM_AUTH_TAG_LENGTH = PBE_KEY_LENGTH;

    private static final String PBE_ALGO = "PBKDF2WithHmacSHA256";

    private static final int MIN_ITERATIONS = 1000;

    private final BundleContext context;

    /**
     * Obtain the crypto.key and crypto.iterations properties from OSGi framework properties and initialize the
     * cryptoKey and iterations variables.
     *
     * @param context the {@link BundleContext} of crypto module.
     */
    @Activate
    public AesGcmCryptoService(BundleContext context) {
        this.context = context;
    }

    @Override
    public String encrypt(String plainText) {
        Validate.isTrue(StringUtils.isNotEmpty(plainText), "plainText can't be null!!");
        char[] cryptoKey = this.context.getProperty(PROPERTY_CRYPTO_KEY).toCharArray();
        int iterations = Integer.parseInt(this.context.getProperty(PROPERTY_CRYPTO_ITERATIONS));
        this.validateKeyMaterials(cryptoKey, iterations);
        byte[] iv = null;
        byte[] salt = null;
        byte[] cipherBytes = null;
        byte[] compositeCipherBytes = null;
        try {
            // 1. get iv
            iv = RandomUtil.randomBytes(IV_LENGTH);
            // 2. get salt
            salt = RandomUtil.randomBytes(SALT_LENGTH);
            // 3. init encrypt mode cipher
            Cipher cipher = this.initCipher(ENCRYPT_MODE, salt, iv, cryptoKey, iterations);
            // 4. generate cipher bytes
            cipherBytes = cipher.doFinal(plainText.getBytes(UTF_8));
            // 5. put everything in a ByteBuffer
            compositeCipherBytes = ByteBuffer.allocate(iv.length + salt.length + cipherBytes.length)
                    .put(iv)
                    .put(salt)
                    .put(cipherBytes)
                    .array();
            // 6. create a UTF-8 String after Base64 encoding the iv+salt+cipherBytes
            return new String(Base64.getEncoder().encode(compositeCipherBytes), UTF_8);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new CryptoException(ex);
        } finally {
            CryptoUtil.nullSafeWipeAll(iv, salt, cipherBytes, compositeCipherBytes);
            Arrays.fill(cryptoKey, Character.MIN_VALUE);
        }
    }

    @Override
    public String decrypt(String cipherText) {
        Validate.isTrue(StringUtils.isNotEmpty(cipherText), "cipherText can't be null!!");
        byte[] cipherTextDecodedBytes = Base64.getDecoder().decode(cipherText.getBytes(UTF_8));
        Validate.isTrue(cipherTextDecodedBytes.length > (IV_LENGTH + SALT_LENGTH),
                "cipherText doesn't seem to be an encrypted string!!");
        char[] cryptoKey = this.context.getProperty(PROPERTY_CRYPTO_KEY).toCharArray();
        int iterations = Integer.parseInt(this.context.getProperty(PROPERTY_CRYPTO_ITERATIONS));
        this.validateKeyMaterials(cryptoKey, iterations);
        byte[] iv = null;
        byte[] salt = null;
        byte[] cipherBytes = null;
        byte[] decryptedBytes = null;
        try {
            // 1. Base64 decode the passed string.
            ByteBuffer buffer = ByteBuffer.wrap(cipherTextDecodedBytes);
            iv = new byte[IV_LENGTH];
            // 2. extract iv
            buffer.get(iv);
            salt = new byte[SALT_LENGTH];
            // 3. extract salt
            buffer.get(salt);
            // 4. init decrypt mode cipher
            Cipher cipher = this.initCipher(DECRYPT_MODE, salt, iv, cryptoKey, iterations);
            cipherBytes = new byte[buffer.remaining()];
            // 5. extract cipherBytes
            buffer.get(cipherBytes);
            // 6. decrypt cipherBytes
            decryptedBytes = cipher.doFinal(cipherBytes);
            // 7. create a UTF-8 String from decryptedBytes
            return new String(decryptedBytes, UTF_8);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new CryptoException(ex);
        } finally {
            CryptoUtil.nullSafeWipeAll(iv, salt, cipherBytes, decryptedBytes);
            Arrays.fill(cryptoKey, Character.MIN_VALUE);
        }
    }

    private Cipher initCipher(int mode,
                              byte[] salt,
                              byte[] iv, char[] cryptoKey, int iterations) throws GeneralSecurityException {
        byte[] key = null;
        try {
            key = CryptoUtil.newSecretKeyBytes(PBE_ALGO, cryptoKey, salt, iterations, PBE_KEY_LENGTH);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, SECRET_KEY_SPEC_ALGO);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_AUTH_TAG_LENGTH, iv);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
            cipher.init(mode, secretKeySpec, parameterSpec);
            return cipher;
        } finally {
            CryptoUtil.nullSafeWipe(key);
        }
    }

    private void validateKeyMaterials(char[] cryptoKey, int iterations) {
        Validate.isTrue(ArrayUtils.isNotEmpty(cryptoKey), "crypto.key can't be null or empty!!");
        try {
            Validate.isTrue((iterations >= MIN_ITERATIONS),
                    String.format("crypto.iterations should be greater than or equal to [%d]!!", MIN_ITERATIONS));
        } catch (IllegalArgumentException ex) {
            Arrays.fill(cryptoKey, Character.MIN_VALUE);
            throw ex;
        }
    }
}