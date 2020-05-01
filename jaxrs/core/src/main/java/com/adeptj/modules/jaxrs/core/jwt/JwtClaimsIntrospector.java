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
import com.adeptj.modules.jaxrs.core.SecurityContextUtil;
import com.adeptj.modules.security.jwt.JwtService;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

import javax.ws.rs.container.ContainerRequestContext;

/**
 * Service interface for introspecting the JWT claims(Registered as well as public).
 * <p>
 * This is injected as an optional service in {@link JwtService}, therefore the claims are only
 * validated if an implementation of {@link JwtClaimsIntrospector} is available in OSGi service registry.
 * <p>
 * Callers should inspect the claims passed and validate claims values as per their need,
 * if everything is fine then must return true otherwise false.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ConsumerType
public interface JwtClaimsIntrospector {

    /**
     * Introspect the JWT claims passed.
     * <p>
     * Registered Claims such as iss, sub, exp are already validated by {@link JwtService} while parsing the JWT,
     * therefore should not be validated again.
     * <p>
     * Any public claims like username, roles and other important information can be introspected as per the need.
     * <p>
     * Implementation must check if the jwt is expired by calling {@link #isJwtExpired}, if jwt is expired then take
     * the appropriate action such as abort the request processing by setting a 401.
     *
     * @param requestContext the JaxRS request context
     */
    void introspect(ContainerRequestContext requestContext);

    /**
     * Checks whether the jwt is expired using the current logged in User's claims.
     *
     * @param requestContext the JaxRS request context
     * @return a boolean to indicate whether the jwt is expired or not.
     */
    default boolean isJwtExpired(@NotNull ContainerRequestContext requestContext) {
        JwtPrincipal principal = SecurityContextUtil.getJwtPrincipal(requestContext.getSecurityContext());
        return principal != null && principal.isHoldingExpiredJwt();
    }
}
