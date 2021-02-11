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

/**
 * KeyInitData holds the information to create crypto keys.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class KeyInitData {

    private final String algorithm;

    private final char[] password;

    private final byte[] salt;

    private final int iterationCount;

    private final int keyLength;

    private KeyInitData(String algorithm, char[] password, byte[] salt, int iterationCount, int keyLength) {
        this.algorithm = algorithm;
        this.password = password;
        this.salt = salt;
        this.iterationCount = iterationCount;
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

    public int getIterationCount() {
        return iterationCount;
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

        private int iterationCount;

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

        public Builder iterationCount(int iterationCount) {
            this.iterationCount = iterationCount;
            return this;
        }

        public Builder keyLength(int keyLength) {
            this.keyLength = keyLength;
            return this;
        }

        public KeyInitData build() {
            return new KeyInitData(this.algorithm, this.password, this.salt, this.iterationCount, this.keyLength);
        }
    }
}
