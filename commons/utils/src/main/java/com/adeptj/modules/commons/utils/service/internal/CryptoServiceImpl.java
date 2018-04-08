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

package com.adeptj.modules.commons.utils.service.internal;

import com.adeptj.modules.commons.utils.service.CryptoService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * CryptoServiceImpl.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = CryptoConfig.class)
@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class CryptoServiceImpl implements CryptoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CryptoServiceImpl.class);

    private static final SecureRandom DEFAULT_SECURE_RANDOM = new SecureRandom();

    private CryptoConfig cryptoConfig;

    @Override
    public String getSalt() {
        byte[] saltBytes = new byte[this.cryptoConfig.saltSize()];
        DEFAULT_SECURE_RANDOM.nextBytes(saltBytes);
        return new String(Base64.getEncoder().encode(saltBytes), UTF_8);
    }

    @Override
    public String getHashedText(String plainText, String salt) {
        try {
            PBEKeySpec keySpec = new PBEKeySpec(plainText.toCharArray(), salt.getBytes(UTF_8),
                    this.cryptoConfig.iterationCount(),
                    this.cryptoConfig.keyLength());
            byte[] hashedBytes = SecretKeyFactory.getInstance(this.cryptoConfig.secretKeyAlgo())
                    .generateSecret(keySpec)
                    .getEncoded();
            return new String(Base64.getEncoder().encode(hashedBytes), UTF_8);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            LOGGER.error("Exception while generating hashed text!!", ex);
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    // -------------- INTERNAL --------------

    @Activate
    protected void start(CryptoConfig cryptoConfig) {
        this.cryptoConfig = cryptoConfig;
    }
}
