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

import at.favre.lib.bytes.Bytes;
import at.favre.lib.crypto.bcrypt.BCrypt;
import com.adeptj.modules.commons.crypto.CryptoUtil;
import com.adeptj.modules.commons.crypto.KeyInitData;
import com.adeptj.modules.commons.crypto.PasswordEncoder;
import com.adeptj.modules.commons.utils.RandomUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.Designate;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Base64;

import static at.favre.lib.crypto.bcrypt.BCrypt.SALT_LENGTH;
import static com.adeptj.modules.commons.crypto.internal.PasswordEncoderConfig.BCRYPT;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Service implementation for encoding passwords using BCrypt or PBKDF2WithHmacSHA* algo.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = PasswordEncoderConfig.class)
@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class CompositePasswordEncoder implements PasswordEncoder {

    private final int exponentialCost;

    private final int iterationCount;

    private final int keyLength;

    private final String algorithm;

    @Activate
    public CompositePasswordEncoder(@NotNull PasswordEncoderConfig config) {
        this.exponentialCost = config.bcrypt_exponential_cost();
        this.iterationCount = config.pbe_key_spec_iteration_count();
        this.keyLength = config.pbe_key_length();
        this.algorithm = config.password_encoding_method();
    }

    @Override
    public String encode(char[] rawPassword) {
        Validate.isTrue(ArrayUtils.isNotEmpty(rawPassword), "rawPassword can't be null!!");
        if (BCRYPT.equals(this.algorithm)) {
            return BCrypt.with(RandomUtil.getSecureRandom()).hashToString(this.exponentialCost, rawPassword);
        }
        byte[] salt = null;
        byte[] digest = null;
        byte[] compositeDigest = null;
        try {
            salt = RandomUtil.randomBytes(SALT_LENGTH);
            digest = CryptoUtil.newSecretKeyBytes(KeyInitData.builder()
                    .algorithm(this.algorithm)
                    .password(rawPassword)
                    .salt(salt)
                    .iterationCount(this.iterationCount)
                    .keyLength(this.keyLength)
                    .build());
            compositeDigest = ByteBuffer.allocate(salt.length + digest.length)
                    .put(salt)
                    .put(digest)
                    .array();
            return new String(Base64.getEncoder().encode(compositeDigest), UTF_8);
        } finally {
            CryptoUtil.nullSafeWipe(salt);
            CryptoUtil.nullSafeWipe(digest);
            CryptoUtil.nullSafeWipe(compositeDigest);
        }
    }

    @Override
    public boolean matches(char[] rawPassword, char[] encodedPassword) {
        Validate.isTrue(ArrayUtils.isNotEmpty(rawPassword), "rawPassword can't be null!!");
        Validate.isTrue(ArrayUtils.isNotEmpty(encodedPassword), "encodedPassword can't be null!!");
        if (BCRYPT.equals(this.algorithm)) {
            return BCrypt.verifyer().verify(rawPassword, encodedPassword).verified;
        }
        ByteBuffer buffer = ByteBuffer.wrap(Base64.getDecoder().decode(Bytes.from(encodedPassword).array()));
        byte[] salt = null;
        byte[] oldDigest = null;
        byte[] newDigest = null;
        try {
            salt = new byte[SALT_LENGTH];
            buffer.get(salt);
            oldDigest = new byte[buffer.remaining()];
            buffer.get(oldDigest);
            newDigest = CryptoUtil.newSecretKeyBytes(KeyInitData.builder()
                    .algorithm(this.algorithm)
                    .password(rawPassword)
                    .salt(salt)
                    .iterationCount(this.iterationCount)
                    .keyLength(this.keyLength)
                    .build());
            return MessageDigest.isEqual(oldDigest, newDigest);
        } finally {
            CryptoUtil.nullSafeWipe(salt);
            CryptoUtil.nullSafeWipe(oldDigest);
            CryptoUtil.nullSafeWipe(newDigest);
        }
    }
}
