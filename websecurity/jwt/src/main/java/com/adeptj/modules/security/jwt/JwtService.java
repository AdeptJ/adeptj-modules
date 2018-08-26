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

import com.adeptj.modules.security.jwt.validation.JwtClaimsValidator;
import org.osgi.annotation.versioning.ProviderType;

import java.util.Map;

/**
 * Service for signing and parsing JWT.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ProviderType
public interface JwtService {

    /**
     * Create JWT for given subject with claims information passed.
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
    String createJwt(String subject, Map<String, Object> claims);

    /**
     * Create JWT from claims information passed.
     * <p>
     * Note: This method expects that caller should pass the default claims parameters in claims map
     * such as "sub", "iss", "exp", "iat", "jti" etc.
     * <p>
     * Default claims parameters from JwtConfig are not considered.
     *
     * @param claims Caller supplied JWT claims map
     * @return JWT signed with the configured key.
     * @since 1.1.0.Final
     */
    String createJwt(Map<String, Object> claims);

    /**
     * Verify the passed jwt claim information using configured signing key.
     * <p>
     * If a {@link JwtClaimsValidator} is available then JWT claims map is passed to it for further
     * validation of claims.
     *
     * @param jwt claims information that has to be verified by the {@link io.jsonwebtoken.JwtParser}
     * @return extended claims map with extra information such as roles etc. if a {@link JwtClaimsValidator} is available,
     * otherwise the Jwt {@link io.jsonwebtoken.Claims} map itself is returned.
     */
    ExtendedJwtClaims verifyJwt(String jwt);
}
