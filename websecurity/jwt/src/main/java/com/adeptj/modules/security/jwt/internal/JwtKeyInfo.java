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

package com.adeptj.modules.security.jwt.internal;

import io.jsonwebtoken.SignatureAlgorithm;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Holder of JWT {@link SignatureAlgorithm}, signing and verification keys.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class JwtKeyInfo {

    private final SignatureAlgorithm signatureAlgorithm;

    private final KeyPair keyPair;

    JwtKeyInfo(SignatureAlgorithm signatureAlgorithm, PrivateKey signingKey, PublicKey verificationKey) {
        this.signatureAlgorithm = signatureAlgorithm;
        this.keyPair = new KeyPair(verificationKey, signingKey);
    }

    SignatureAlgorithm getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    PrivateKey getPrivateKey() {
        return this.keyPair.getPrivate();
    }

    PublicKey getPublicKey() {
        return this.keyPair.getPublic();
    }
}
