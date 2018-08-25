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

import com.adeptj.modules.security.jwt.JwtConfig;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.lang.Assert;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static java.io.File.separator;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.crypto.Cipher.DECRYPT_MODE;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.SystemUtils.USER_HOME;

/**
 * Utility for creating JWT signing key.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class JwtSigningKeys {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtSigningKeys.class);

    private static final String PRIVATE_KEY_HEADER = "-----BEGIN PRIVATE KEY-----";

    private static final String PRIVATE_KEY_FOOTER = "-----END PRIVATE KEY-----";

    private static final String PRIVATE_ENCRYPTED_KEY_HEADER = "-----BEGIN ENCRYPTED PRIVATE KEY-----";

    private static final String PRIVATE_ENCRYPTED_KEY_FOOTER = "-----END ENCRYPTED PRIVATE KEY-----";

    private static final String PUB_KEY_HEADER = "-----BEGIN PUBLIC KEY-----";

    private static final String PUB_KEY_FOOTER = "-----END PUBLIC KEY-----";

    private static final String REGEX_SPACE = "\\s";

    private static final String HMAC_SECRET_KEY_NULL_MSG = "hmacSecretKey can't be blank when algo is Hmac!!";

    private static final String KEYPASS_NULL_MSG = "keyPassword can't be blank!!";

    private JwtSigningKeys() {
    }

    static Key createRsaSigningKey(JwtConfig jwtConfig) {
        try {
            return getRsaSigningKey(jwtConfig);
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
            throw new KeyInitializationException(ex.getMessage(), ex);
        }
    }

    static Key createRsaVerificationKey(JwtConfig jwtConfig) {
        try {
            return getRsaVerificationKey(jwtConfig);
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
            throw new KeyInitializationException(ex.getMessage(), ex);
        }
    }

    static Key createHmacSigningKey(String hmacSecretKey, SignatureAlgorithm algorithm) {
        Assert.hasText(hmacSecretKey, HMAC_SECRET_KEY_NULL_MSG);
        return new SecretKeySpec(Base64.getEncoder().encode(hmacSecretKey.getBytes(UTF_8)), algorithm.getJcaName());
    }

    private static Key getRsaSigningKey(JwtConfig jwtConfig) throws Exception { // NOSONAR
        String keyFileLocation = jwtConfig.privateKeyFileLocation();
        if (StringUtils.isEmpty(keyFileLocation) && jwtConfig.searchKeysInUserHome()) {
            keyFileLocation = USER_HOME + separator + jwtConfig.defaultPrivateKeyName();
        }
        LOGGER.info("Loading signing key: [{}]", keyFileLocation);
        try (InputStream is = Files.newInputStream(Paths.get(keyFileLocation))) {
            return KeyFactory.getInstance(SignatureAlgorithm.forName(jwtConfig.signatureAlgo()).getFamilyName())
                    .generatePrivate(getEncodedKeySpec(IOUtils.toString(is, UTF_8), jwtConfig.keyPassword()));
        }
    }

    private static Key getRsaVerificationKey(JwtConfig jwtConfig) throws Exception { // NOSONAR
        String keyFileLocation = jwtConfig.publicKeyFileLocation();
        if (StringUtils.isEmpty(keyFileLocation) && jwtConfig.searchKeysInUserHome()) {
            keyFileLocation = USER_HOME + separator + jwtConfig.defaultPublicKeyName();
        }
        LOGGER.info("Loading verification key: [{}]", keyFileLocation);
        try (InputStream is = Files.newInputStream(Paths.get(keyFileLocation))) {
            return KeyFactory.getInstance(SignatureAlgorithm.forName(jwtConfig.signatureAlgo()).getFamilyName())
                    .generatePublic(new X509EncodedKeySpec(decodePublicKeyData(IOUtils.toString(is, UTF_8))));
        }
    }

    private static EncodedKeySpec getEncodedKeySpec(String keyData, String keyPassword) throws Exception { // NOSONAR
        EncodedKeySpec keySpec;
        if (StringUtils.startsWith(keyData, PRIVATE_ENCRYPTED_KEY_HEADER)) {
            Assert.hasText(keyPassword, KEYPASS_NULL_MSG);
            EncryptedPrivateKeyInfo keyInfo = new EncryptedPrivateKeyInfo(decodePrivateKeyData(keyData, true));
            SecretKey secretKey = SecretKeyFactory.getInstance(keyInfo.getAlgName())
                    .generateSecret(new PBEKeySpec(keyPassword.toCharArray()));
            Cipher cipher = Cipher.getInstance(keyInfo.getAlgName());
            cipher.init(DECRYPT_MODE, secretKey, keyInfo.getAlgParameters());
            keySpec = keyInfo.getKeySpec(cipher);
        } else {
            keySpec = new PKCS8EncodedKeySpec(decodePrivateKeyData(keyData, false));
        }
        return keySpec;
    }

    private static byte[] decodePrivateKeyData(String keyData, boolean encryptedKey) {
        return Base64.getDecoder().decode(keyData
                .replace(encryptedKey ? PRIVATE_ENCRYPTED_KEY_HEADER : PRIVATE_KEY_HEADER, EMPTY)
                .replace(encryptedKey ? PRIVATE_ENCRYPTED_KEY_FOOTER : PRIVATE_KEY_FOOTER, EMPTY)
                .replaceAll(REGEX_SPACE, EMPTY)
                .trim()
                .getBytes(UTF_8));
    }

    private static byte[] decodePublicKeyData(String keyData) {
        return Base64.getDecoder().decode(keyData
                .replace(PUB_KEY_HEADER, EMPTY)
                .replace(PUB_KEY_FOOTER, EMPTY)
                .replaceAll(REGEX_SPACE, EMPTY)
                .trim()
                .getBytes(UTF_8));
    }
}
