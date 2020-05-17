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

package com.adeptj.modules.security.jwt.internal;

import io.jsonwebtoken.lang.Assert;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.crypto.Cipher.DECRYPT_MODE;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Utility for creating JWT signing and verification key.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class JwtKeys {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String PRIVATE_KEY_HEADER = "-----BEGIN PRIVATE KEY-----";

    private static final String PRIVATE_KEY_FOOTER = "-----END PRIVATE KEY-----";

    private static final String PRIVATE_ENCRYPTED_KEY_HEADER = "-----BEGIN ENCRYPTED PRIVATE KEY-----";

    private static final String PRIVATE_ENCRYPTED_KEY_FOOTER = "-----END ENCRYPTED PRIVATE KEY-----";

    private static final String PUB_KEY_HEADER = "-----BEGIN PUBLIC KEY-----";

    private static final String PUB_KEY_FOOTER = "-----END PUBLIC KEY-----";

    private static final String REGEX_SPACE = "\\s";

    private static final String KEYPASS_NULL_MSG = "privateKeyPassword can't be blank!!";

    private static final String INVALID_PUBLIC_KEY_MSG = "Invalid PublicKey, must start with -----BEGIN PUBLIC KEY-----";

    private static final String INVALID_PRIVATE_KEY_MSG = "Invalid PrivateKey, must start either with -----BEGIN PRIVATE KEY-----" +
            " or -----BEGIN ENCRYPTED PRIVATE KEY-----";

    private JwtKeys() {
    }

    static PrivateKey createSigningKey(@NotNull JwtConfig config, String algorithm) {
        LOGGER.info("Creating RSA signing key!!");
        Assert.isTrue(StringUtils.startsWithAny(config.privateKey(), PRIVATE_ENCRYPTED_KEY_HEADER, PRIVATE_KEY_HEADER),
                INVALID_PRIVATE_KEY_MSG);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            if (StringUtils.startsWith(config.privateKey(), PRIVATE_ENCRYPTED_KEY_HEADER)) {
                LOGGER.info("Creating PKCS8EncodedKeySpec from private [encrypted] key !!");
                Assert.hasText(config.privateKeyPassword(), KEYPASS_NULL_MSG);
                byte[] keyBytes = decodePrivateKey(config, true);
                EncryptedPrivateKeyInfo privateKeyInfo = new EncryptedPrivateKeyInfo(keyBytes);
                SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(privateKeyInfo.getAlgName());
                PBEKeySpec keySpec = new PBEKeySpec(config.privateKeyPassword().toCharArray());
                SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
                Cipher cipher = Cipher.getInstance(privateKeyInfo.getAlgName());
                cipher.init(DECRYPT_MODE, secretKey, privateKeyInfo.getAlgParameters());
                return keyFactory.generatePrivate(privateKeyInfo.getKeySpec(cipher));
            }
            LOGGER.info("Creating PKCS8EncodedKeySpec from private key !!");
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decodePrivateKey(config, false)));
        } catch (GeneralSecurityException | IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new JwtKeyInitializationException(ex.getMessage(), ex);
        }
    }

    static PublicKey createVerificationKey(@NotNull JwtConfig config, String algorithm) {
        LOGGER.info("Creating RSA verification key!!");
        Assert.isTrue(StringUtils.startsWith(config.publicKey(), PUB_KEY_HEADER), INVALID_PUBLIC_KEY_MSG);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            byte[] publicKeyData = Base64.getDecoder().decode(config.publicKey()
                    .replace(PUB_KEY_HEADER, EMPTY)
                    .replace(PUB_KEY_FOOTER, EMPTY)
                    .replaceAll(REGEX_SPACE, EMPTY)
                    .trim()
                    .getBytes(UTF_8));
            LOGGER.info("Creating X509EncodedKeySpec from public key !!");
            return keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyData));
        } catch (GeneralSecurityException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new JwtKeyInitializationException(ex.getMessage(), ex);
        }
    }

    private static byte[] decodePrivateKey(@NotNull JwtConfig config, boolean encryptedKey) {
        return Base64.getDecoder().decode(config.privateKey()
                .replace(encryptedKey ? PRIVATE_ENCRYPTED_KEY_HEADER : PRIVATE_KEY_HEADER, EMPTY)
                .replace(encryptedKey ? PRIVATE_ENCRYPTED_KEY_FOOTER : PRIVATE_KEY_FOOTER, EMPTY)
                .replaceAll(REGEX_SPACE, EMPTY)
                .trim()
                .getBytes(UTF_8));
    }
}
