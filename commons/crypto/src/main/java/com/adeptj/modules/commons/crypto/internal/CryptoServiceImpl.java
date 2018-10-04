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
import com.adeptj.modules.commons.crypto.SaltHashPair;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.lang.invoke.MethodHandles;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Service implementation for generating random salt and hashed text using PBKDF2WithHmacSHA* algo.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = CryptoConfig.class)
@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class CryptoServiceImpl implements CryptoService {

    private static final Logger LOGGER = LoggerFactory.getLogger((MethodHandles.lookup().lookupClass()));

    private static final String PLAINTEXT_NULL_MSG = "plainText can't be blank!!";

    private static final SecureRandom DEFAULT_SECURE_RANDOM = new SecureRandom();

    private CryptoConfig cryptoConfig;

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getSaltBytes() {
        byte[] saltBytes = new byte[this.cryptoConfig.saltSize()];
        DEFAULT_SECURE_RANDOM.nextBytes(saltBytes);
        return saltBytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSaltText() {
        return this.encodeToString(this.getSaltBytes(), UTF_8);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getHashedBytes(String plainText, byte[] salt) {
        Validate.isTrue(StringUtils.isNotEmpty(plainText), PLAINTEXT_NULL_MSG);
        Validate.isTrue(ArrayUtils.isNotEmpty(salt), "salt array can't be empty!!");
        try {
            KeySpec keySpec = new PBEKeySpec(plainText.toCharArray(), salt, this.cryptoConfig.iterationCount(),
                    this.cryptoConfig.keyLength());
            return SecretKeyFactory.getInstance(this.cryptoConfig.secretKeyAlgo())
                    .generateSecret(keySpec)
                    .getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            LOGGER.error("Exception while generating hashed bytes!!", ex);
            throw new CryptoException(ex);
        }
    }

    @Override
    public byte[] getHashedBytes(String plainText, String salt) {
        return this.getHashedBytes(plainText, salt.getBytes(UTF_8));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHashedText(String plainText, String salt) {
        Validate.isTrue(StringUtils.isNotEmpty(salt), "salt can't be blank!!");
        return this.encodeToString(this.getHashedBytes(plainText, salt.getBytes(UTF_8)), UTF_8);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SaltHashPair getSaltHashPair(String plainText) {
        byte[] saltBytes = this.getSaltBytes();
        return new SaltHashPair(this.encodeToString(saltBytes, UTF_8),
                this.encodeToString(this.getHashedBytes(plainText, saltBytes), UTF_8));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean compareHashes(SaltHashPair saltHashPair, String plainText) {
        return StringUtils.equals(saltHashPair.getHash(), this.getHashedText(plainText, saltHashPair.getSalt()));
    }

    // ------------------------------------------ OSGi INTERNAL ------------------------------------------

    @Activate
    protected void start(CryptoConfig cryptoConfig) {
        this.cryptoConfig = cryptoConfig;
    }
}
