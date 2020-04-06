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

import com.adeptj.modules.security.jwt.JwtService;
import org.osgi.annotation.versioning.ConsumerType;

import javax.ws.rs.container.ContainerRequestContext;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.adeptj.modules.jaxrs.core.JaxRSConstants.AUTH_SCHEME_TOKEN;
import static com.adeptj.modules.jaxrs.core.JaxRSConstants.KEY_JWT_EXPIRED;
import static com.adeptj.modules.jaxrs.core.JaxRSConstants.KEY_JWT_SUBJECT;
import static com.adeptj.modules.jaxrs.core.JaxRSConstants.ROLES;
import static com.adeptj.modules.jaxrs.core.JaxRSConstants.ROLES_DELIMITER;
import static org.apache.commons.lang3.StringUtils.EMPTY;

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
     * Implementation must check if the key JWT_EXPIRED exists in the passed claims by calling {@link #isJwtExpiredKeyExists},
     * if exists, then take appropriate action such as abort the request processing by setting a 401.
     *
     * @param requestContext the JaxRs request context
     * @param claims         the JWT claims
     */
    void introspect(ContainerRequestContext requestContext, Map<String, Object> claims);

    /**
     * Checks whether the claims contains the key JWT_EXPIRED.
     *
     * @param claims the Jwt claims
     * @return a boolean to indicate whether the claims contains the key JWT_EXPIRED.
     */
    default boolean isJwtExpiredKeyExists(Map<String, Object> claims) {
        return Boolean.parseBoolean((String) claims.get(KEY_JWT_EXPIRED));
    }

    /**
     * Sets the {@link JwtSecurityContext} to the {@link ContainerRequestContext}.
     *
     * @param requestContext the JaxRs request context
     * @param claims         the JWT claims
     */
    default void setJwtSecurityContext(ContainerRequestContext requestContext, Map<String, Object> claims) {
        requestContext.setSecurityContext(JwtSecurityContext.newSecurityContext()
                .withSubject((String) claims.get(KEY_JWT_SUBJECT))
                .withRoles(Stream.of(((String) claims.getOrDefault(ROLES, EMPTY)).split(ROLES_DELIMITER))
                        .collect(Collectors.toSet()))
                .withSecure(requestContext.getSecurityContext().isSecure())
                .withAuthScheme(AUTH_SCHEME_TOKEN));
    }
}
