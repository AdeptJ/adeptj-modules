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

import com.adeptj.modules.security.jwt.validation.JwtClaimsValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtHandlerAdapter;

import java.util.Map;

/**
 * Factory for creating {@link JwtHandlerAdapter} instances.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class JwtHandlerAdapters {

    private JwtHandlerAdapters() {
    }

    static JwtHandlerAdapter<Boolean> createAdapter(boolean validateClaims, JwtClaimsValidator claimsValidator) {
        return new ClaimsJwsHandler(validateClaims, claimsValidator);
    }

    static class ClaimsJwsHandler extends JwtHandlerAdapter<Boolean> {

        private final boolean validateClaims;

        private final JwtClaimsValidator claimsValidator;

        ClaimsJwsHandler(boolean validateClaims, JwtClaimsValidator claimsValidator) {
            this.validateClaims = validateClaims;
            this.claimsValidator = claimsValidator;
        }

        /**
         * Validates the {@link Claims} via {@link JwtClaimsValidator}.
         *
         * @param jws the Json web signature.
         * @return boolean to indicate the outcome of {@link JwtClaimsValidator#validate(Map)}.
         */
        @Override
        public Boolean onClaimsJws(Jws<Claims> jws) {
            return !this.validateClaims || this.claimsValidator != null && this.claimsValidator.validate(jws.getBody());
        }
    }
}
