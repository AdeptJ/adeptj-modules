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
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Utility for loading JWT signing key.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class SigningKeys {

    private static final String UTF8 = "UTF-8";

    private static final String KEY_HEADER = "-----BEGIN PRIVATE KEY-----";

    private static final String KEY_FOOTER = "-----END PRIVATE KEY-----";

    private static final String REGEX_SPACE = "\\s";

    private static final String ALGO_RSA = "RSA";

    private static final String SYS_PROP_CURRENT_DIR = "user.dir";

    private static final String DEFAULT_KEY_FILE = "/default.pem";

    static PrivateKey getSigningKey(JwtConfig jwtConfig) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGO_RSA);
        // 1. try the jwtConfig provided keyFileLocation
        String keyFileLocation = System.getProperty(SYS_PROP_CURRENT_DIR) +
                File.separator +
                jwtConfig.keyFileLocation();
        Logger logger = Loggers.get(SigningKeys.class);
        logger.info("Loading Key file from location: [{}]", keyFileLocation);
        PrivateKey privateKey = loadKeyFromLocation(keyFactory, keyFileLocation);
        if (privateKey == null && jwtConfig.useDefaultKey()) {
            logger.warn("Couldn't load Key file from location [{}], using the default one!!", keyFileLocation);
            // 2. Use the default one that is embedded with this module.
            privateKey = loadDefaultKey(keyFactory, logger);
        }
        return privateKey;
    }

    private static PrivateKey loadKeyFromLocation(KeyFactory keyFactory, String keyFileLocation) throws Exception {
        PrivateKey privateKey = null;
        try (FileInputStream inputStream = new FileInputStream(keyFileLocation)) {
            privateKey = generatePrivateKey(keyFactory, inputStream);
        } catch (FileNotFoundException ex) {
            // Gulp it. Try to load the embedded one next.
        }
        return privateKey;
    }

    private static PrivateKey loadDefaultKey(KeyFactory keyFactory, Logger logger) {
        PrivateKey privateKey = null;
        try (InputStream inputStream = JwtServiceImpl.class.getResourceAsStream(DEFAULT_KEY_FILE)) {
            privateKey = generatePrivateKey(keyFactory, inputStream);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return privateKey;
    }

    private static PrivateKey generatePrivateKey(KeyFactory keyFactory, InputStream stream) throws Exception {
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey(stream)));
    }

    private static byte[] encodedKey(InputStream stream) throws IOException {
        return Base64.getDecoder().decode(IOUtils.toString(stream, UTF8)
                .replace(KEY_HEADER, EMPTY)
                .replace(KEY_FOOTER, EMPTY)
                .replaceAll(REGEX_SPACE, EMPTY)
                .getBytes(UTF8));
    }
}
