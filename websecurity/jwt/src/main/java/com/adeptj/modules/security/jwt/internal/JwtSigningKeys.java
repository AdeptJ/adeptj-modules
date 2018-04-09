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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
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

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.crypto.Cipher.DECRYPT_MODE;
import static org.apache.commons.lang3.StringUtils.EMPTY;

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

    private static final String KEY_FILE_LOC_NULL_MSG = "keyFileLocation can't be blank when algo is RSA!!";

    private static final String HMAC_SECRET_KEY_NULL_MSG = "hmacSecretKey can't be blank when algo is Hmac!!";

    private static final String KEYPASS_NULL_MSG = "keyPassword can't be blank!!";

    private JwtSigningKeys() {
    }

    static Key createSigningKey(JwtConfig jwtConfig) {
        SignatureAlgorithm signatureAlgo = SignatureAlgorithm.forName(jwtConfig.signatureAlgo());
        try {
            Key hmacSigningKey = JwtSigningKeys.getHmacSigningKey(signatureAlgo, jwtConfig.hmacSecretKey());
            return hmacSigningKey == null && signatureAlgo.isRsa()
                    ? JwtSigningKeys.getRsaSigningKey(signatureAlgo, jwtConfig) : hmacSigningKey;
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
            throw new KeyInitializationException(ex.getMessage(), ex);
        }
    }

    private static Key getHmacSigningKey(SignatureAlgorithm algo, String secretKey) {
        Key signingKey = null;
        if (algo.isHmac()) {
            Validate.isTrue(StringUtils.isNotEmpty(secretKey), HMAC_SECRET_KEY_NULL_MSG);
            signingKey = new SecretKeySpec(Base64.getEncoder().encode(secretKey.getBytes(UTF_8)), algo.getJcaName());
        }
        return signingKey;
    }

    private static Key getRsaSigningKey(SignatureAlgorithm algo, JwtConfig jwtConfig) throws Exception { // NOSONAR
        String keyFileLocation = jwtConfig.keyFileLocation();
        Validate.isTrue(StringUtils.isNotEmpty(keyFileLocation), KEY_FILE_LOC_NULL_MSG);
        LOGGER.info("Loading signing key: [{}]", keyFileLocation);
        try (InputStream is = Files.newInputStream(Paths.get(keyFileLocation))) {
            EncodedKeySpec encodedKeySpec = getEncodedKeySpec(jwtConfig, IOUtils.toString(is, UTF_8));
            return KeyFactory.getInstance(algo.getFamilyName()).generatePrivate(encodedKeySpec);
        }
    }

    private static EncodedKeySpec getEncodedKeySpec(JwtConfig jwtConfig, String keyData) throws Exception { // NOSONAR
        EncodedKeySpec keySpec;
        if (StringUtils.startsWith(keyData, ENCRYPTED_KEY_HEADER)) {
            Validate.isTrue(StringUtils.isNotEmpty(jwtConfig.keyPassword()), KEYPASS_NULL_MSG);
            EncryptedPrivateKeyInfo privateKeyInfo = new EncryptedPrivateKeyInfo(decodeEncryptedKeyData(keyData));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(privateKeyInfo.getAlgName());
            SecretKey secretKey = keyFactory.generateSecret(new PBEKeySpec(jwtConfig.keyPassword().toCharArray()));
            Cipher cipher = Cipher.getInstance(privateKeyInfo.getAlgName());
            cipher.init(DECRYPT_MODE, secretKey, privateKeyInfo.getAlgParameters());
            keySpec = privateKeyInfo.getKeySpec(cipher);
        } else {
            keySpec = new PKCS8EncodedKeySpec(decodeUnencryptedKeyData(keyData));
        }
        return keySpec;
    }

    private static byte[] decodeUnencryptedKeyData(String unencryptedKeyData) {
        return Base64.getDecoder().decode(unencryptedKeyData
                .replace(KEY_HEADER, EMPTY)
                .replace(KEY_FOOTER, EMPTY)
                .replaceAll(REGEX_SPACE, EMPTY)
                .trim()
                .getBytes(UTF_8));
    }

    private static byte[] decodeEncryptedKeyData(String encryptedKeyData) {
        return Base64.getDecoder().decode(encryptedKeyData
                .replace(ENCRYPTED_KEY_HEADER, EMPTY)
                .replace(ENCRYPTED_KEY_FOOTER, EMPTY)
                .replaceAll(REGEX_SPACE, EMPTY)
                .trim()
                .getBytes(UTF_8));
    }
}
