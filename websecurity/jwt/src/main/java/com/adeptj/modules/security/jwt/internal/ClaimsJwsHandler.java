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

import com.adeptj.modules.security.jwt.JwtClaims;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtHandlerAdapter;

/**
 * Simple implementation of {@link JwtHandlerAdapter}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class ClaimsJwsHandler extends JwtHandlerAdapter<JwtClaims> {

    /**
     * Simply returns the {@link JwtClaims}.
     *
     * @param jws the Json web signature.
     * @return the {@link JwtClaims}.
     */
    @Override
    public JwtClaims onClaimsJws(Jws<Claims> jws) {
        JwtClaims claims = new JwtClaims();
        claims.putAll(jws.getBody());
        return claims;
    }
}
