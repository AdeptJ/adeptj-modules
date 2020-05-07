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
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Provides random bytes using {@link SecureRandom}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class Randomizer {

    public static final SecureRandom DEFAULT_SECURE_RANDOM;

    private static final RandomBasedGenerator RANDOM_BASED_GENERATOR;

    static {
        DEFAULT_SECURE_RANDOM = new SecureRandom();
        DEFAULT_SECURE_RANDOM.nextBytes(new byte[64]);
        RANDOM_BASED_GENERATOR = Generators.randomBasedGenerator(DEFAULT_SECURE_RANDOM);
    }

    private Randomizer() {
    }

    public static @NotNull byte[] random16Bytes() {
        return randomBytes(16);
    }

    public static @NotNull byte[] random32Bytes() {
        return randomBytes(32);
    }

    private static @NotNull byte[] randomBytes(int length) {
        byte[] bytes = new byte[length];
        DEFAULT_SECURE_RANDOM.nextBytes(bytes);
        return bytes;
    }

    public static UUID randomUUID() {
        return RANDOM_BASED_GENERATOR.generate();
    }

    public static String randomUUIDString() {
        return randomUUID().toString();
    }
}
