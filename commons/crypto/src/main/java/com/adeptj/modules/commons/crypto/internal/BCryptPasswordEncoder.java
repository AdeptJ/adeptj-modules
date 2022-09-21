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
import com.adeptj.modules.commons.crypto.PasswordEncoder;
import com.adeptj.modules.commons.utils.RandomGenerators;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

import static at.favre.lib.crypto.bcrypt.BCrypt.Version.VERSION_2A;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Service implementation for encoding/matching passwords using BCrypt.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = PasswordEncoderConfig.class)
@Component
public class BCryptPasswordEncoder implements PasswordEncoder {

    private final int exponentialCost;

    @Activate
    public BCryptPasswordEncoder(@NotNull PasswordEncoderConfig config) {
        this.exponentialCost = config.exponential_cost();
    }

    @Override
    public String encode(char[] rawPassword) {
        Validate.isTrue(ArrayUtils.isNotEmpty(rawPassword), "rawPassword can't be null!!");
        return BCrypt.with(RandomGenerators.getSecureRandom()).hashToString(this.exponentialCost, rawPassword);
    }

    @Override
    public String encode(byte[] rawPassword) {
        return new String(this.encodeToBytes(rawPassword), UTF_8);
    }

    @Override
    public byte[] encodeToBytes(byte[] rawPassword) {
        Validate.isTrue(ArrayUtils.isNotEmpty(rawPassword), "rawPassword can't be null!!");
        return BCrypt.with(RandomGenerators.getSecureRandom()).hash(this.exponentialCost, rawPassword);
    }

    @Override
    public boolean matches(char[] rawPassword, char[] encodedPassword) {
        Validate.isTrue(ArrayUtils.isNotEmpty(rawPassword), "rawPassword can't be null!!");
        Validate.isTrue(ArrayUtils.isNotEmpty(encodedPassword), "encodedPassword can't be null!!");
        return BCrypt.verifyer(VERSION_2A).verifyStrict(rawPassword, encodedPassword).verified;
    }

    @Override
    public boolean matches(byte[] rawPassword, byte[] encodedPassword) {
        Validate.isTrue(ArrayUtils.isNotEmpty(rawPassword), "rawPassword can't be null!!");
        Validate.isTrue(ArrayUtils.isNotEmpty(encodedPassword), "encodedPassword can't be null!!");
        return BCrypt.verifyer(VERSION_2A).verifyStrict(rawPassword, encodedPassword).verified;
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        Validate.isTrue(StringUtils.isNotEmpty(rawPassword), "rawPassword can't be null!!");
        Validate.isTrue(StringUtils.isNotEmpty(encodedPassword), "encodedPassword can't be null!!");
        return this.matches(rawPassword.toCharArray(), encodedPassword.toCharArray());
    }
}
