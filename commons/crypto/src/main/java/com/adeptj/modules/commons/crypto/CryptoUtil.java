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

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.util.Arrays;

/**
 * Utility methods for creating {@link SecretKey}.
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

    public static void nullSafeWipeAll(byte[]... arrays) {
        if (arrays == null) {
            return;
        }
        for (byte[] array : arrays) {
            nullSafeWipe(array);
        }
    }

    public static byte[] newSecretKeyBytes(String algorithm,
                                           char[] password,
                                           byte[] salt,
                                           int iterations,
                                           int keyLength) throws GeneralSecurityException {
        return SecretKeyFactory.getInstance(algorithm)
                .generateSecret(new PBEKeySpec(password, salt, iterations, keyLength))
                .getEncoded();
    }
}
