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

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.adeptj.modules.commons.crypto.CryptoUtil;
import com.adeptj.modules.commons.crypto.PasswordEncoder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.Designate;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Arrays;
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

    private final int specKeyLength;

    private final String passwordEncodingMethod;

    @Activate
    public CompositePasswordEncoder(@NotNull PasswordEncoderConfig config) {
        this.exponentialCost = config.bcrypt_exponential_cost();
        this.iterationCount = config.pbe_key_spec_iteration_count();
        this.specKeyLength = config.pbe_key_length();
        this.passwordEncodingMethod = config.password_encoding_method();
    }

    @Override
    public String encode(String rawPassword) {
        Validate.isTrue(StringUtils.isNotEmpty(rawPassword), "rawPassword can't be null!!");
        if (BCRYPT.equals(this.passwordEncodingMethod)) {
            return BCrypt.withDefaults().hashToString(this.exponentialCost, rawPassword.toCharArray());
        }
        byte[] salt = CryptoUtil.randomBytes(SALT_LENGTH);
        byte[] digest = CryptoUtil.newSecretKey(this.passwordEncodingMethod, rawPassword.toCharArray(), salt,
                this.iterationCount,
                this.specKeyLength);
        byte[] compositeDigest = ByteBuffer.allocate(salt.length + digest.length)
                .put(salt)
                .put(digest)
                .array();
        String encoded = new String(Base64.getEncoder().encode(compositeDigest), UTF_8);
        Arrays.fill(salt, (byte) 0);
        Arrays.fill(digest, (byte) 0);
        Arrays.fill(compositeDigest, (byte) 0);
        return encoded;
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        Validate.isTrue(StringUtils.isNotEmpty(rawPassword), "rawPassword can't be null!!");
        Validate.isTrue(StringUtils.isNotEmpty(encodedPassword), "encodedPassword can't be null!!");
        if (BCRYPT.equals(this.passwordEncodingMethod)) {
            return BCrypt.verifyer().verify(rawPassword.toCharArray(), encodedPassword.toCharArray()).verified;
        }
        ByteBuffer buffer = ByteBuffer.wrap(Base64.getDecoder().decode(encodedPassword.getBytes(UTF_8)));
        byte[] salt = new byte[SALT_LENGTH];
        buffer.get(salt);
        byte[] oldDigest = new byte[buffer.remaining()];
        buffer.get(oldDigest);
        byte[] newDigest = CryptoUtil.newSecretKey(this.passwordEncodingMethod, rawPassword.toCharArray(), salt,
                this.iterationCount,
                this.specKeyLength);
        boolean match = MessageDigest.isEqual(oldDigest, newDigest);
        Arrays.fill(salt, (byte) 0);
        Arrays.fill(oldDigest, (byte) 0);
        Arrays.fill(newDigest, (byte) 0);
        return match;
    }
}
