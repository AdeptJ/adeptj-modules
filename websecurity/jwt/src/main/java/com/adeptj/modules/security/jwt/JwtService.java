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
     * Issue JWT for given subject using claims information passed as payload.
     *
     * @param subject to whom JWT has to be issued.
     * @param payload claims map
     * @return JWT signed with the configured key.
     */
    String issueJwt(String subject, Map<String, Object> payload);

    /**
     * Parses the passed claim information using configured key.
     *
     * @param claimsJws claims information that has to be parsed by the {@link io.jsonwebtoken.JwtParser}
     * @return status to indicate the result of parsing the claim passed.
     */
    boolean parseClaimsJws(String claimsJws);
}
