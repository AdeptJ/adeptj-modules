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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.SystemUtils.USER_DIR;

/**
 * Utility for loading JWT signing key.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class JwtSigningKeys {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtSigningKeys.class);

    private static final String UTF8 = "UTF-8";

    private static final String KEY_HEADER = "-----BEGIN PRIVATE KEY-----";

    private static final String KEY_FOOTER = "-----END PRIVATE KEY-----";

    private static final String ENCRYPTED_KEY_HEADER = "-----BEGIN ENCRYPTED PRIVATE KEY-----";

    private static final String ENCRYPTED_KEY_FOOTER = "-----END ENCRYPTED PRIVATE KEY-----";

    private static final String REGEX_SPACE = "\\s";

    private static final String ALGO_RSA = "RSA";

    private static final String DEFAULT_KEY_FILE = "/jwt-pkcs8.pem";


    static Key createSigningKey(JwtConfig jwtConfig) {
        try {
            SignatureAlgorithm signatureAlgo = SignatureAlgorithm.forName(jwtConfig.signatureAlgo());
            Key hmacSecretKey = JwtSigningKeys.getHmacSecretKey(signatureAlgo, jwtConfig.hmacSecretKey());
            return hmacSecretKey == null ? JwtSigningKeys.getRSAPrivateKey(jwtConfig) : hmacSecretKey;
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
            // Let the exception be rethrown so that SCR would not create a service object of this component.
            throw new RuntimeException(ex); // NOSONAR
        }
    }

    private static Key getHmacSecretKey(SignatureAlgorithm signatureAlgo, String secretKey) throws UnsupportedEncodingException {
        Key signingKey = null;
        if (signatureAlgo.isHmac() && StringUtils.isEmpty(secretKey)) {
            throw new IllegalStateException("hmacSecretKey property can't be empty when SignatureAlgorithm is Hmac!!");
        } else if (signatureAlgo.isHmac()) {
            signingKey = new SecretKeySpec(Base64.getEncoder().encode(secretKey.getBytes(UTF8)), signatureAlgo.getJcaName());
        } else if (StringUtils.isNotEmpty(secretKey)) {
            LOGGER.warn("hmacSecretKey is provided but SignatureAlgorithm is RSA, ignoring Hmac and creating RSA key!!");
        }
        return signingKey;
    }

    private static Key getRSAPrivateKey(JwtConfig jwtConfig) {
        // 1. try the jwtConfig provided keyFileLocation
        Path keyFileLocation = Paths.get(USER_DIR, jwtConfig.keyFileLocation());
        Key signingKey = null;
        if (Files.exists(keyFileLocation)) {
            signingKey = loadSigningKeyFromLocation(jwtConfig, keyFileLocation);
        } else if (jwtConfig.useDefaultKey()) {
            LOGGER.warn("Loading the default signing key, please check if that is intended!!");
            // 2. Use the default one that is embedded with this module.
            signingKey = loadDefaultSigningKey(jwtConfig);
        }
        // if signingKey is still null then throw exception so that SCR won't create the JwtService instance.
        if (signingKey == null) {
            throw new IllegalStateException("Couldn't initialize the RSAPrivateKey!!");
        }
        return signingKey;
    }

    private static Key loadSigningKeyFromLocation(JwtConfig jwtConfig, Path keyFileLocation) {
        LOGGER.info("Loading signing key: [{}]", keyFileLocation);
        Key signingKey = null;
        try (InputStream data = Files.newInputStream(keyFileLocation)) {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = getPKCS8EncodedKeySpec(jwtConfig, data);
            if (pkcs8EncodedKeySpec != null) {
                signingKey = KeyFactory.getInstance(ALGO_RSA).generatePrivate(pkcs8EncodedKeySpec);
            }
        } catch (Exception ex) { //NOSONAR
            LOGGER.error(ex.getMessage(), ex);
        }
        return signingKey;
    }

    private static Key loadDefaultSigningKey(JwtConfig jwtConfig) {
        Key signingKey = null;
        try (InputStream data = JwtSigningKeys.class.getResourceAsStream(DEFAULT_KEY_FILE)) {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = getPKCS8EncodedKeySpec(jwtConfig, data);
            if (pkcs8EncodedKeySpec != null) {
                signingKey = KeyFactory.getInstance(ALGO_RSA).generatePrivate(pkcs8EncodedKeySpec);
            }
        } catch (Exception ex) { //NOSONAR
            LOGGER.error(ex.getMessage(), ex);
        }
        return signingKey;
    }

    private static PKCS8EncodedKeySpec getPKCS8EncodedKeySpec(JwtConfig jwtConfig, InputStream data) {
        PKCS8EncodedKeySpec encodedKeySpec = null;
        try {
            String keyData = IOUtils.toString(data, UTF8);
            boolean isEncryptedKey = StringUtils.startsWith(keyData, ENCRYPTED_KEY_HEADER);
            if (isEncryptedKey && StringUtils.isEmpty(jwtConfig.keyPassword())) {
                throw new IllegalStateException("PrivateKey password is missing in JwtConfig#keyPassword OSGi config!!");
            } else if (isEncryptedKey) {
                EncryptedPrivateKeyInfo privateKeyInfo = new EncryptedPrivateKeyInfo(decodeEncryptedKeyData(keyData));
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(privateKeyInfo.getAlgName());
                SecretKey secretKey = keyFactory.generateSecret(new PBEKeySpec(jwtConfig.keyPassword().toCharArray()));
                Cipher cipher = Cipher.getInstance(privateKeyInfo.getAlgName());
                cipher.init(DECRYPT_MODE, secretKey, privateKeyInfo.getAlgParameters());
                encodedKeySpec = privateKeyInfo.getKeySpec(cipher);
            } else {
                encodedKeySpec = new PKCS8EncodedKeySpec(decodeUnencryptedKeyData(keyData));
            }
        } catch (Exception ex) { //NOSONAR
            LOGGER.error(ex.getMessage(), ex);
        }
        return encodedKeySpec;
    }

    private static byte[] decodeUnencryptedKeyData(String unencryptedKeyData) throws UnsupportedEncodingException {
        return Base64.getDecoder().decode(unencryptedKeyData
                .replace(KEY_HEADER, EMPTY)
                .replace(KEY_FOOTER, EMPTY)
                .replaceAll(REGEX_SPACE, EMPTY)
                .trim()
                .getBytes(UTF8));
    }

    private static byte[] decodeEncryptedKeyData(String encryptedKeyData) throws UnsupportedEncodingException {
        return Base64.getDecoder().decode(encryptedKeyData
                .replace(ENCRYPTED_KEY_HEADER, EMPTY)
                .replace(ENCRYPTED_KEY_FOOTER, EMPTY)
                .replaceAll(REGEX_SPACE, EMPTY)
                .trim()
                .getBytes(UTF8));
    }
}
