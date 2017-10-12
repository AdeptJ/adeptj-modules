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

import com.adeptj.modules.commons.utils.Loggers;
import com.adeptj.modules.security.jwt.JwtConfig;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.SystemUtils.USER_DIR;

/**
 * Utility for loading JWT signing key.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class JwtSigningKeys {

    private static final String UTF8 = "UTF-8";

    private static final String KEY_HEADER = "-----BEGIN PRIVATE KEY-----";

    private static final String KEY_FOOTER = "-----END PRIVATE KEY-----";

    private static final String REGEX_SPACE = "\\s";

    private static final String ALGO_RSA = "RSA";

    private static final String DEFAULT_KEY_FILE = "/default.pem";

    static byte[] getHmacSecretKey(SignatureAlgorithm signatureAlgo, String secretKey) throws UnsupportedEncodingException {
        byte[] hmacSecretKey = null;
        if (signatureAlgo.isHmac()) {
            if (StringUtils.isEmpty(secretKey)) {
                throw new IllegalStateException("hmacSecretKey property can't be empty when SignatureAlgorithm is Hmac!!");
            } else {
                hmacSecretKey = Base64.getEncoder().encode(secretKey.getBytes(UTF8));
            }
        } else if (StringUtils.isNotEmpty(secretKey)) {
            throw new IllegalStateException("Can't have RSA SignatureAlgorithm when hmacSecretKey property is provided!!");
        }
        return hmacSecretKey;
    }

    static PrivateKey getRsaPrivateKey(JwtConfig jwtConfig) throws Exception {
        // 1. try the jwtConfig provided keyFileLocation
        String keyFileLocation = USER_DIR + File.separator + jwtConfig.keyFileLocation();
        Logger logger = Loggers.get(JwtSigningKeys.class);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGO_RSA);
        PrivateKey privateKey = loadKeyFromLocation(keyFactory, keyFileLocation, logger);
        if (privateKey == null && jwtConfig.useDefaultKey()) {
            logger.warn("Loading the default Key, please check if that is intended!!");
            // 2. Use the default one that is embedded with this module.
            privateKey = loadDefaultKey(keyFactory, logger);
        }
        if (privateKey == null) {
            throw new IllegalStateException("Couldn't initialize the RsaPrivateKey!!");
        }
        return privateKey;
    }

    private static PrivateKey loadKeyFromLocation(KeyFactory keyFactory, String keyFileLocation, Logger logger) {
        PrivateKey privateKey = null;
        try (InputStream data = new FileInputStream(keyFileLocation)) {
            privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey(data)));
        } catch (Exception ex) {
            logger.error("Exception while loading Key file!!", ex);
        }
        return privateKey;
    }

    private static PrivateKey loadDefaultKey(KeyFactory keyFactory, Logger logger) {
        PrivateKey privateKey = null;
        try (InputStream data = JwtSigningKeys.class.getResourceAsStream(DEFAULT_KEY_FILE)) {
            privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey(data)));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return privateKey;
    }

    private static byte[] encodedKey(InputStream data) throws IOException {
        return Base64.getDecoder().decode(IOUtils.toString(data, UTF8)
                .replace(KEY_HEADER, EMPTY)
                .replace(KEY_FOOTER, EMPTY)
                .replaceAll(REGEX_SPACE, EMPTY)
                .getBytes(UTF8));
    }
}
