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
     * Issue JWT for given subject using claims information passed as claims.
     *
     * @param subject to whom JWT has to be issued.
     * @param claims  claims map
     * @return JWT signed with the configured key.
     */
    String issue(String subject, Map<String, Object> claims);

    /**
     * Verify the passed jwt claim information using configured key.
     *
     * @param jwt claims information that has to be verified by the {@link io.jsonwebtoken.JwtParser}
     * @return boolean flag to indicate the result of verification of the jwt claim is successful or not.
     */
    boolean verify(String jwt);

    /**
     * Verify the passed jwt claim information using configured key.
     *
     * @param subject to whom JWT has to be issued.
     * @param jwt     claims information that has to be verified by the {@link io.jsonwebtoken.JwtParser}
     * @return boolean flag to indicate the result of verification of the jwt claim is successful or not.
     */
    boolean verify(String subject, String jwt);
}
