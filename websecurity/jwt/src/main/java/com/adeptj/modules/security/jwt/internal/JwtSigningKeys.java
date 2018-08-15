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

    private static final String KEY_HEADER = "-----BEGIN PRIVATE KEY-----";

    private static final String KEY_FOOTER = "-----END PRIVATE KEY-----";

    private static final String ENCRYPTED_KEY_HEADER = "-----BEGIN ENCRYPTED PRIVATE KEY-----";

    private static final String ENCRYPTED_KEY_FOOTER = "-----END ENCRYPTED PRIVATE KEY-----";

    private static final String REGEX_SPACE = "\\s";

    private static final String HMAC_SECRET_KEY_NULL_MSG = "hmacSecretKey can't be blank when algo is Hmac!!";

    private static final String HMAC_SECRET_KEY_SIZE_MSG = "HMAC SecretKey size is less that the required!!";

    private static final String KEYPASS_NULL_MSG = "keyPassword can't be blank!!";

    private JwtSigningKeys() {
    }

    static Key createSigningKey(JwtConfig jwtConfig) {
        try {
            Key hmacSigningKey = getHmacSigningKey(jwtConfig);
            return hmacSigningKey == null ? getRsaSigningKey(jwtConfig) : hmacSigningKey;
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
            throw new KeyInitializationException(ex.getMessage(), ex);
        }
    }

    private static Key getHmacSigningKey(JwtConfig jwtConfig) {
        Key signingKey = null;
        SignatureAlgorithm signatureAlgo = SignatureAlgorithm.forName(jwtConfig.signatureAlgo());
        if (signatureAlgo.isHmac()) {
            Assert.hasText(jwtConfig.hmacSecretKey(), HMAC_SECRET_KEY_NULL_MSG);
            Assert.isTrue(jwtConfig.hmacSecretKey().length() >= jwtConfig.hmacKeySize(), HMAC_SECRET_KEY_SIZE_MSG);
            signingKey = new SecretKeySpec(Base64.getEncoder().encode(jwtConfig.hmacSecretKey().getBytes(UTF_8)),
                    signatureAlgo.getJcaName());
        }
        return signingKey;
    }

    private static Key getRsaSigningKey(JwtConfig jwtConfig) throws Exception { // NOSONAR
        String keyFileLocation = jwtConfig.keyFileLocation();
        if (StringUtils.isEmpty(keyFileLocation) && jwtConfig.searchKeyInUserHome()) {
            keyFileLocation = USER_HOME + separator + jwtConfig.defaultKeyName();
        }
        LOGGER.info("Loading signing key: [{}]", keyFileLocation);
        try (InputStream is = Files.newInputStream(Paths.get(keyFileLocation))) {
            return KeyFactory.getInstance(SignatureAlgorithm.forName(jwtConfig.signatureAlgo()).getFamilyName())
                    .generatePrivate(getEncodedKeySpec(IOUtils.toString(is, UTF_8), jwtConfig.keyPassword()));
        }
    }

    private static EncodedKeySpec getEncodedKeySpec(String keyData, String keyPassword) throws Exception { // NOSONAR
        EncodedKeySpec keySpec;
        if (StringUtils.startsWith(keyData, ENCRYPTED_KEY_HEADER)) {
            Assert.hasText(keyPassword, KEYPASS_NULL_MSG);
            EncryptedPrivateKeyInfo keyInfo = new EncryptedPrivateKeyInfo(decodeKeyData(keyData, true));
            SecretKey secretKey = SecretKeyFactory.getInstance(keyInfo.getAlgName())
                    .generateSecret(new PBEKeySpec(keyPassword.toCharArray()));
            Cipher cipher = Cipher.getInstance(keyInfo.getAlgName());
            cipher.init(DECRYPT_MODE, secretKey, keyInfo.getAlgParameters());
            keySpec = keyInfo.getKeySpec(cipher);
        } else {
            keySpec = new PKCS8EncodedKeySpec(decodeKeyData(keyData, false));
        }
        return keySpec;
    }

    private static byte[] decodeKeyData(String keyData, boolean encryptedKey) {
        return Base64.getDecoder().decode(keyData
                .replace(encryptedKey ? ENCRYPTED_KEY_HEADER : KEY_HEADER, EMPTY)
                .replace(encryptedKey ? ENCRYPTED_KEY_FOOTER : KEY_FOOTER, EMPTY)
                .replaceAll(REGEX_SPACE, EMPTY)
                .trim()
                .getBytes(UTF_8));
    }
}
