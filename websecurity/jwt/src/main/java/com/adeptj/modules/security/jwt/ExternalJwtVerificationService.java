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

import org.osgi.annotation.versioning.ProviderType;

/**
 * A Jwt verification service for verifying 3rd party Jwt(s).
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ProviderType
public interface ExternalJwtVerificationService {

    /**
     * Verify the passed jwt claim information using configured signing key.
     *
     * @param jwt claims information that has to be verified by the {@link io.jsonwebtoken.JwtParser}
     * @return the {@link JwtClaims} object containing the claims information.
     */
    JwtClaims verifyJwt(String jwt);
}
