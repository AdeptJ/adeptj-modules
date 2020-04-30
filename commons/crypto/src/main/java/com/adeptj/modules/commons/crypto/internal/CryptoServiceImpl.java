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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Base64;

import static at.favre.lib.crypto.bcrypt.BCrypt.SALT_LENGTH;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

/**
 * Service implementation for encrypting/decrypting any text using AES/GCM/NoPadding algo.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = CryptoConfig.class)
@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class CryptoServiceImpl implements CryptoService {

    private static final int IV_LENGTH = 12;

    private static final String ALGO_GCM = "AES/GCM/NoPadding";

    private static final String ALGO_AES = "AES";

    private final int keyLength;

    private final int authTagLength;

    private final int iterationCount;

    private final String algorithm;

    private final char[] cryptoKey;

    @Activate
    public CryptoServiceImpl(@NotNull CryptoConfig config) {
        this.keyLength = config.aes_key_length();
        this.authTagLength = config.auth_tag_length();
        this.iterationCount = config.pbe_key_spec_iteration_count();
        this.algorithm = config.pbe_key_spec_algorithm();
        this.cryptoKey = config.crypto_key().toCharArray();
    }

    @Override
    public String encrypt(String plainText) {
        Validate.isTrue(StringUtils.isNotEmpty(plainText), "plainText can't be null!!");
        byte[] iv = null;
        byte[] salt = null;
        byte[] key = null;
        byte[] cipherBytes = null;
        byte[] compositeCipherBytes = null;
        try {
            iv = CryptoUtil.randomBytes(IV_LENGTH);
            salt = CryptoUtil.randomBytes(SALT_LENGTH);
            key = CryptoUtil.newSecretKey(this.algorithm, this.cryptoKey, salt, this.iterationCount, this.keyLength);
            Cipher cipher = Cipher.getInstance(ALGO_GCM);
            cipher.init(ENCRYPT_MODE, new SecretKeySpec(key, ALGO_AES), new GCMParameterSpec(this.authTagLength, iv));
            cipherBytes = cipher.doFinal(plainText.getBytes(UTF_8));
            compositeCipherBytes = ByteBuffer.allocate(iv.length + salt.length + cipherBytes.length)
                    .put(iv)
                    .put(salt)
                    .put(cipherBytes)
                    .array();
            return new String(Base64.getEncoder().encode(compositeCipherBytes), UTF_8);
        } catch (GeneralSecurityException ex) {
            throw new CryptoException(ex);
        } finally {
            CryptoUtil.nullSafeWipe(iv);
            CryptoUtil.nullSafeWipe(salt);
            CryptoUtil.nullSafeWipe(key);
            CryptoUtil.nullSafeWipe(cipherBytes);
            CryptoUtil.nullSafeWipe(compositeCipherBytes);
        }
    }

    @Override
    public String decrypt(String cipherText) {
        Validate.isTrue(StringUtils.isNotEmpty(cipherText), "cipherText can't be null!!");
        ByteBuffer buffer = ByteBuffer.wrap(Base64.getDecoder().decode(cipherText.getBytes(UTF_8)));
        byte[] iv = null;
        byte[] salt = null;
        byte[] key = null;
        byte[] cipherBytes = null;
        byte[] decryptedBytes = null;
        try {
            iv = new byte[IV_LENGTH];
            buffer.get(iv);
            salt = new byte[SALT_LENGTH];
            buffer.get(salt);
            key = CryptoUtil.newSecretKey(this.algorithm, this.cryptoKey, salt, this.iterationCount, this.keyLength);
            Cipher cipher = Cipher.getInstance(ALGO_GCM);
            cipher.init(DECRYPT_MODE, new SecretKeySpec(key, ALGO_AES), new GCMParameterSpec(this.authTagLength, iv));
            cipherBytes = new byte[buffer.remaining()];
            buffer.get(cipherBytes);
            decryptedBytes = cipher.doFinal(cipherBytes);
            return new String(decryptedBytes, UTF_8);
        } catch (GeneralSecurityException ex) {
            throw new CryptoException(ex);
        } finally {
            CryptoUtil.nullSafeWipe(iv);
            CryptoUtil.nullSafeWipe(salt);
            CryptoUtil.nullSafeWipe(key);
            CryptoUtil.nullSafeWipe(cipherBytes);
            CryptoUtil.nullSafeWipe(decryptedBytes);
        }
    }

    // << ------------------------------------------ OSGi Internal ------------------------------------------>>

    @Deactivate
    protected void stop() {
        Arrays.fill(this.cryptoKey, '\u0000');
    }
}