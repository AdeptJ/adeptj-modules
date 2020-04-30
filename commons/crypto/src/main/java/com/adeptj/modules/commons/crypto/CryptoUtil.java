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
import org.jetbrains.annotations.NotNull;

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

    /**
     * From : JJWT's SignatureProvider
     * <p>
     * Crypto module's default SecureRandom number generator. This RNG is initialized using the JVM default as follows:
     *
     * <pre><code>
     * static {
     *     DEFAULT_SECURE_RANDOM = new SecureRandom();
     *     DEFAULT_SECURE_RANDOM.nextBytes(new byte[64]);
     * }
     * </code></pre>
     *
     * <p><code>nextBytes</code> is called to force the RNG to initialize itself if not already initialized.  The
     * byte array is not used and discarded immediately for garbage collection.</p>
     */
    private static final SecureRandom DEFAULT_SECURE_RANDOM;

    static {
        DEFAULT_SECURE_RANDOM = new SecureRandom();
        DEFAULT_SECURE_RANDOM.nextBytes(new byte[64]);
    }

    private CryptoUtil() {
    }

    public static byte @NotNull [] randomBytes(int length) {
        byte[] randomBytes = new byte[length];
        DEFAULT_SECURE_RANDOM.nextBytes(randomBytes);
        return randomBytes;
    }

    public static void nullSafeWipe(byte[] data) {
        if (ArrayUtils.isNotEmpty(data)) {
            Arrays.fill(data, (byte) 0);
        }
    }

    public static byte[] newSecretKey(String algorithm, char[] password, byte[] salt, int iterationCount, int keyLength) {
        try {
            return SecretKeyFactory.getInstance(algorithm)
                    .generateSecret(new PBEKeySpec(password, salt, iterationCount, keyLength))
                    .getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new CryptoException(ex);
        }
    }
}
