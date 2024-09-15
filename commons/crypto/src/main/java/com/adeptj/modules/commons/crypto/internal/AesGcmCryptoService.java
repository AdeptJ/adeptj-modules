/*
###############################################################################
#                                                                             #
#    Copyright 2016-2024, AdeptJ (http://www.adeptj.com)                      #
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;

import javax.crypto.SecretKey;

/**
 * Service implementation for encrypting/decrypting any text using AES/GCM/NoPadding algo.
 *
 * @author Rakesh Kumar, AdeptJ
 */
@Component
public class AesGcmCryptoService implements CryptoService {

    private static final int GCM_IV_LENGTH = 12;

    private static final String PROPERTY_CRYPTO_KEY = "crypto.key";

    private static final String PROPERTY_CRYPTO_SALT = "crypto.salt";

    private static final String PROPERTY_CRYPTO_ITERATIONS = "crypto.iterations";

    private static final int PBE_KEY_LENGTH = 128;

    private static final String PBE_ALGO = "PBKDF2WithHmacSHA256";

    private static final int MIN_ITERATIONS = 1000;

    private final BytesEncryptor encryptor;

    /**
     * Obtain the [crypto.key], [crypto.salt] and [crypto.iterations] properties from
     * the OSGi framework properties while encrypting and decrypting the passed text.
     *
     * @param context the {@link BundleContext} of crypto module.
     */
    @Activate
    public AesGcmCryptoService(final BundleContext context) {
        char[] cryptoKey = this.getFrameworkProperty(context, PROPERTY_CRYPTO_KEY).toCharArray();
        byte[] cryptoSalt = Hex.decode(this.getFrameworkProperty(context, PROPERTY_CRYPTO_SALT));
        int iterations = this.getIterations(context);
        SecretKey secretKey = CryptoUtil.createPBESecretKey(PBE_ALGO, cryptoKey, cryptoSalt, iterations, PBE_KEY_LENGTH);
        BytesKeyGenerator ivGenerator = KeyGenerators.secureRandom(GCM_IV_LENGTH);
        this.encryptor = new AesBytesEncryptor(secretKey, ivGenerator, AesBytesEncryptor.CipherAlgorithm.GCM);
    }

    @Override
    public String encrypt(String plainText) {
        byte[] encryptedBytes = null;
        try {
            Validate.isTrue(StringUtils.isNotEmpty(plainText), "plainText can't be null!!");
            encryptedBytes = this.encryptor.encrypt(Utf8.encode(plainText));
            return String.valueOf(Hex.encode(encryptedBytes));
        } catch (Exception ex) {
            throw new CryptoException(ex);
        } finally {
            CryptoUtil.nullSafeWipe(encryptedBytes);
        }
    }

    @Override
    public String decrypt(String cipherText) {
        byte[] decoded = null;
        byte[] decryptedBytes = null;
        try {
            Validate.isTrue(StringUtils.isNotEmpty(cipherText), "cipherText can't be null!!");
            decoded = Hex.decode(cipherText);
            decryptedBytes = this.encryptor.decrypt(decoded);
            return Utf8.decode(decryptedBytes);
        } catch (Exception ex) {
            throw new CryptoException(ex);
        } finally {
            CryptoUtil.nullSafeWipeAll(decoded, decryptedBytes);
        }
    }

    private String getFrameworkProperty(@NotNull BundleContext context, String propertyName) {
        String value = context.getProperty(propertyName);
        Validate.validState(StringUtils.isNotEmpty(value),
                String.format("OSGi framework property [%s] can't be null or empty!!", propertyName));
        return value;
    }

    private int getIterations(BundleContext context) {
        int iterations = Integer.parseInt(this.getFrameworkProperty(context, PROPERTY_CRYPTO_ITERATIONS));
        Validate.validState((iterations >= MIN_ITERATIONS),
                String.format("OSGi framework property [%s] should be greater than or equal to [%d]!!",
                        PROPERTY_CRYPTO_ITERATIONS,
                        MIN_ITERATIONS));
        return iterations;
    }
}