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

package com.adeptj.modules.commons.crypto;

import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

/**
 * Provides random bytes using {@link SecureRandom}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class CryptoUtil {

    private CryptoUtil() {
    }

    public static void nullSafeWipe(byte[] data) {
        if (ArrayUtils.isNotEmpty(data)) {
            Arrays.fill(data, (byte) 0);
        }
    }

    public static byte[] newSecretKey(String algorithm,
                                      char[] password, byte[] salt, int iterationCount, int keyLength) {
        try {
            return SecretKeyFactory.getInstance(algorithm)
                    .generateSecret(new PBEKeySpec(password, salt, iterationCount, keyLength))
                    .getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new CryptoException(ex);
        }
    }
}
