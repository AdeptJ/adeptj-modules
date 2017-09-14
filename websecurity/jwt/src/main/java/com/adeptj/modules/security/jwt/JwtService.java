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

package com.adeptj.modules.security.jwt;

import java.util.Map;

/**
 * Service for signing and parsing JWT.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public interface JwtService {

    /**
     * Issue JWT for given subject with claims information passed.
     * <p>
     * Note: First set the claims, don't allow callers to pass the default claims parameters in claims map
     * such as "iss", "sub", "iat" etc. Since its a map and existing keys will be replaced when the same ones
     * provided in the payload which is not the intended behaviour.
     * <p>
     * Default claims parameters should come from JwtConfig and others can be generated at execution time
     * such as "iat", "exp", "jti" etc.
     *
     * @param subject to whom JWT has to be issued.
     * @param claims  Caller supplied JWT claims map
     * @return JWT signed with the configured key.
     */
    String issueJwt(String subject, Map<String, Object> claims);

    /**
     * Verify the passed jwt claim information using configured signing key.
     * <p>
     * If a {@link JwtClaimsValidator} is available then JWT claims map is passed to it for further
     * validation of claims.
     *
     * @param jwt claims information that has to be verified by the {@link io.jsonwebtoken.JwtParser}
     * @return A boolean indicating {@link JwtService} was able to parse the JWT or not, a false should be treated as
     * an indication of failure so that caller can take action accordingly, such has setting 403 status.
     */
    boolean verifyJwt(String jwt);
}
