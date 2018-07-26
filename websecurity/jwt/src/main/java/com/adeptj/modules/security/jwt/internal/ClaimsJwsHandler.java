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

import com.adeptj.modules.security.jwt.JwtConfig;
import com.adeptj.modules.security.jwt.validation.JwtClaimsValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtHandlerAdapter;
import io.jsonwebtoken.MalformedJwtException;
import org.apache.commons.lang3.StringUtils;

/**
 * Simple implementation of {@link JwtHandlerAdapter}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class ClaimsJwsHandler extends JwtHandlerAdapter<Boolean> {

    private final boolean validateClaims;

    private final JwtClaimsValidator claimsValidator;

    private final String algorithm;

    ClaimsJwsHandler(JwtConfig jwtConfig, JwtClaimsValidator claimsValidator) {
        this.validateClaims = jwtConfig.validateClaims();
        this.claimsValidator = claimsValidator;
        this.algorithm = jwtConfig.signatureAlgo();
    }

    /**
     * Checks the signature algorithm first and then validates the {@link Claims} via {@link JwtClaimsValidator}.
     *
     * @param jws the Json web signature.
     * @return boolean to indicate the outcome of {@link JwtClaimsValidator#validate} method.
     */
    @Override
    public Boolean onClaimsJws(Jws<Claims> jws) {
        if (!StringUtils.equals(this.algorithm, jws.getHeader().getAlgorithm())) {
            throw new MalformedJwtException(String.format("SignatureAlgorithm must be [%s]!!", this.algorithm));
        }
        return !this.validateClaims || this.claimsValidator != null && this.claimsValidator.validate(jws.getBody());
    }
}
