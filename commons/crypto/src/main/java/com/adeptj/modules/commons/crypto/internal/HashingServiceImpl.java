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
import com.adeptj.modules.commons.crypto.HashingService;
import com.adeptj.modules.commons.crypto.Randomness;
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
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Service implementation for generating random salt and hashed text using PBKDF2WithHmacSHA* algo.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = HashingConfig.class)
@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class HashingServiceImpl implements HashingService {

    private static final Logger LOGGER = LoggerFactory.getLogger((MethodHandles.lookup().lookupClass()));

    private static final String PLAINTEXT_NULL_MSG = "plainText can't be blank!!";

    private final int saltSize;

    private final int iterationCount;

    private final int keyLength;

    private final String secretKeyAlgo;

    private final Charset charset;

    @Activate
    public HashingServiceImpl(HashingConfig config) {
        this.charset = Charset.forName(config.charsetToEncode());
        this.saltSize = config.saltSize();
        this.iterationCount = config.iterationCount();
        this.keyLength = config.keyLength();
        this.secretKeyAlgo = config.secretKeyAlgo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getSaltBytes() {
        return Randomness.randomBytes(this.saltSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSaltText() {
        return this.encodeToString(this.getSaltBytes(), this.charset);
    }

    @Override
    public byte[] getHashedBytes(char[] plainText, byte[] salt) {
        Validate.isTrue(ArrayUtils.isNotEmpty(plainText), "plainText array can't be empty!!");
        Validate.isTrue(ArrayUtils.isNotEmpty(salt), "salt array can't be empty!!");
        try {
            return SecretKeyFactory.getInstance(this.secretKeyAlgo)
                    .generateSecret(new PBEKeySpec(plainText, salt, this.iterationCount, this.keyLength))
                    .getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            LOGGER.error("Exception while generating hashed bytes!!", ex);
            throw new CryptoException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getHashedBytes(String plainText, byte[] salt) {
        Validate.isTrue(StringUtils.isNotEmpty(plainText), PLAINTEXT_NULL_MSG);
        return this.getHashedBytes(plainText.toCharArray(), salt);
    }

    @Override
    public byte[] getHashedBytes(String plainText, String salt) {
        Validate.isTrue(StringUtils.isNotEmpty(salt), "salt can't be blank!!");
        return this.getHashedBytes(plainText, salt.getBytes(this.charset));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHashedText(String plainText, String salt) {
        return this.encodeToString(this.getHashedBytes(plainText, salt), this.charset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SaltHashPair getSaltHashPair(String plainText) {
        byte[] saltBytes = this.getSaltBytes();
        return new SaltHashPair(this.encodeToString(saltBytes, this.charset),
                this.encodeToString(this.getHashedBytes(plainText, saltBytes), this.charset));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean compareHashes(SaltHashPair saltHashPair, String plainText) {
        return StringUtils.equals(saltHashPair.getHash(), this.getHashedText(plainText, saltHashPair.getSalt()));
    }
}
