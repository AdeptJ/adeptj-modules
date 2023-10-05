/*
###############################################################################
#                                                                             #
#    Copyright 2016-2022, AdeptJ (http://www.adeptj.com)                      #
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
package com.adeptj.modules.security.jwt;

import io.jsonwebtoken.security.SignatureAlgorithm;

/**
 * Pojo for holding the Jwt Rsa signing key information.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class RsaSigningKeyInfo {

    private final SignatureAlgorithm algorithm;

    private final String privateKey;

    private String privateKeyPassword;

    public RsaSigningKeyInfo(SignatureAlgorithm algorithm, String privateKey) {
        this.algorithm = algorithm;
        this.privateKey = privateKey;
    }

    public SignatureAlgorithm getAlgorithm() {
        return algorithm;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPrivateKeyPassword() {
        return privateKeyPassword;
    }

    public void setPrivateKeyPassword(String privateKeyPassword) {
        this.privateKeyPassword = privateKeyPassword;
    }
}
