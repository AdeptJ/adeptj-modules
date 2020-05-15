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

package com.adeptj.modules.jaxrs.core.jwt;

import com.adeptj.modules.jaxrs.core.JwtPrincipal;
import com.adeptj.modules.security.jwt.JwtClaims;

import javax.ws.rs.core.SecurityContext;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.adeptj.modules.jaxrs.core.JaxRSConstants.AUTH_SCHEME_TOKEN;
import static com.adeptj.modules.jaxrs.core.JaxRSConstants.ROLES;
import static com.adeptj.modules.jaxrs.core.JaxRSConstants.ROLES_DELIMITER;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Jwt based JAX-RS {@link SecurityContext}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class JwtSecurityContext implements SecurityContext {

    private final JwtPrincipal principal;

    private final boolean httpsRequest;

    public JwtSecurityContext(JwtClaims claims, boolean httpsRequest) {
        this.principal = new JwtPrincipal(claims);
        this.httpsRequest = httpsRequest;
    }

    @Override
    public JwtPrincipal getUserPrincipal() {
        return this.principal;
    }

    @Override
    public boolean isUserInRole(String role) {
        return Stream.of(((String) this.principal.getClaims().getOrDefault(ROLES, EMPTY)).split(ROLES_DELIMITER))
                .collect(Collectors.toSet())
                .contains(role);
    }

    @Override
    public boolean isSecure() {
        return this.httpsRequest;
    }

    @Override
    public String getAuthenticationScheme() {
        return AUTH_SCHEME_TOKEN;
    }
}
