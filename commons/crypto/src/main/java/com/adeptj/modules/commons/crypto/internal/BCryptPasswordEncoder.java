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

import com.adeptj.modules.commons.crypto.PasswordEncoder;
import com.adeptj.modules.commons.utils.RandomGenerators;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.springframework.security.crypto.bcrypt.BCrypt;

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
    public String encode(String rawPassword) {
        Validate.isTrue(StringUtils.isNotEmpty(rawPassword), "rawPassword can't be null!!");
        String salt = BCrypt.gensalt(this.exponentialCost, RandomGenerators.getSecureRandom());
        return BCrypt.hashpw(rawPassword, salt);
    }

    @Override
    public String encode(byte[] rawPassword) {
        Validate.isTrue(ArrayUtils.isNotEmpty(rawPassword), "rawPassword can't be null!!");
        String salt = BCrypt.gensalt(this.exponentialCost, RandomGenerators.getSecureRandom());
        return BCrypt.hashpw(rawPassword, salt);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        Validate.isTrue(StringUtils.isNotEmpty(rawPassword), "rawPassword can't be null!!");
        Validate.isTrue(StringUtils.isNotEmpty(encodedPassword), "encodedPassword can't be null!!");
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }

    @Override
    public boolean matches(byte[] rawPassword, String encodedPassword) {
        Validate.isTrue(ArrayUtils.isNotEmpty(rawPassword), "rawPassword can't be null!!");
        Validate.isTrue(StringUtils.isNotEmpty(encodedPassword), "encodedPassword can't be null!!");
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}
