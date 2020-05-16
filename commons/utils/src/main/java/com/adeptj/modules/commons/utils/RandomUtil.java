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

package com.adeptj.modules.commons.utils;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.RandomBasedGenerator;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Provides random bytes using {@link SecureRandom}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class RandomUtil {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final RandomBasedGenerator GENERATOR = Generators.randomBasedGenerator(SECURE_RANDOM);

    private static final int DEFAULT_MAX_LENGTH = 64;

    private RandomUtil() {
    }

    public static SecureRandom getSecureRandom() {
        return SECURE_RANDOM;
    }

    public static @NotNull byte[] randomBytes(int length) {
        int maxLength = Integer.getInteger("adeptj.secure.random.max.bytes.length", DEFAULT_MAX_LENGTH);
        Validate.isTrue((length <= maxLength), String.format("length can't be greater than %s", maxLength));
        byte[] bytes = new byte[length];
        SECURE_RANDOM.nextBytes(bytes);
        return bytes;
    }

    public static UUID uuid() {
        return GENERATOR.generate();
    }

    public static String uuidString() {
        return uuid().toString();
    }
}
