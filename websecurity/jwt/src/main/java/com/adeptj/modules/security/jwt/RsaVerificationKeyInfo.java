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
 * Pojo for holding the Jwt Rsa verification key information.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class RsaVerificationKeyInfo {

    private final SignatureAlgorithm algorithm;

    private final String publicKey;

    public RsaVerificationKeyInfo(SignatureAlgorithm algorithm, String publicKey) {
        this.algorithm = algorithm;
        this.publicKey = publicKey;
    }

    public SignatureAlgorithm getAlgorithm() {
        return algorithm;
    }

    public String getPublicKey() {
        return publicKey;
    }
}
