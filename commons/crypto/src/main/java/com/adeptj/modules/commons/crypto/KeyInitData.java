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
import org.apache.commons.lang3.Validate;

/**
 * KeyInitData holds the information to create crypto keys.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class KeyInitData {

    private static final int MIN_ITERATIONS = 1000;

    private final String algorithm;

    private final char[] password;

    private final byte[] salt;

    private final int iterations;

    private final int keyLength;

    private KeyInitData(String algorithm, char[] password, byte[] salt, int iterations, int keyLength) {
        Validate.isTrue(ArrayUtils.isNotEmpty(password), "password can't be null or empty!!");
        Validate.isTrue((iterations >= MIN_ITERATIONS), String.format("iterations should be at least %d!!",
                MIN_ITERATIONS));
        this.algorithm = algorithm;
        this.password = password;
        this.salt = salt;
        this.iterations = iterations;
        this.keyLength = keyLength;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public char[] getPassword() {
        return password;
    }

    public byte[] getSalt() {
        return salt;
    }

    public int getIterations() {
        return iterations;
    }

    public int getKeyLength() {
        return keyLength;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String algorithm;

        private char[] password;

        private byte[] salt;

        private int iterations;

        private int keyLength;

        public Builder algorithm(String algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public Builder password(char[] password) {
            this.password = password;
            return this;
        }

        public Builder salt(byte[] salt) {
            this.salt = salt;
            return this;
        }

        public Builder iterations(int iterations) {
            this.iterations = iterations;
            return this;
        }

        public Builder keyLength(int keyLength) {
            this.keyLength = keyLength;
            return this;
        }

        public KeyInitData build() {
            return new KeyInitData(this.algorithm, this.password, this.salt, this.iterations, this.keyLength);
        }
    }
}
