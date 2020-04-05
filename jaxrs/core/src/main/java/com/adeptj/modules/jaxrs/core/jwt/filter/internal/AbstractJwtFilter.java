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

package com.adeptj.modules.jaxrs.core.jwt.filter.internal;

import com.adeptj.modules.jaxrs.core.jwt.JwtExtractor;
import com.adeptj.modules.jaxrs.core.jwt.JwtSecurityContext;
import com.adeptj.modules.jaxrs.core.jwt.filter.JwtFilter;
import com.adeptj.modules.security.jwt.JwtClaims;
import com.adeptj.modules.security.jwt.JwtService;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.adeptj.modules.jaxrs.core.JaxRSConstants.AUTH_SCHEME_TOKEN;
import static com.adeptj.modules.jaxrs.core.JaxRSConstants.REQ_URI_INFO;
import static com.adeptj.modules.jaxrs.core.JaxRSConstants.ROLES;
import static com.adeptj.modules.jaxrs.core.JaxRSConstants.ROLES_DELIMITER;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Implements the {@link #filter} from {@link javax.ws.rs.container.ContainerRequestFilter}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public abstract class AbstractJwtFilter implements JwtFilter {

    public void filter(ContainerRequestContext requestContext) {
        JwtService jwtService = this.getJwtService();
        if (jwtService == null) {
            requestContext.abortWith(Response.status(SERVICE_UNAVAILABLE).build());
            return;
        }
        String jwt = JwtExtractor.extract(requestContext);
        // Send Bad Request(400) if JWT is null/empty.
        if (StringUtils.isEmpty(jwt)) {
            requestContext.abortWith(Response.status(BAD_REQUEST).build());
            return;
        }
        JwtClaims claims = jwtService.verifyJwt(jwt);
        claims.augment(REQ_URI_INFO, requestContext.getUriInfo());
        if (this.getClaimsIntrospector().introspect(claims.asMap())) {
            requestContext.setSecurityContext(JwtSecurityContext.newSecurityContext()
                    .withSubject(claims.getSubject())
                    .withRoles(Stream.of(((String) claims.asMap().getOrDefault(ROLES, EMPTY)).split(ROLES_DELIMITER))
                            .collect(Collectors.toSet()))
                    .withSecure(requestContext.getSecurityContext().isSecure())
                    .withAuthScheme(AUTH_SCHEME_TOKEN));
        } else {
            // Send Unauthorized if JwtService finds token to be malformed, expired etc.
            // 401 is better suited for token verification failure.
            requestContext.abortWith(Response.status(UNAUTHORIZED).build());
        }
    }
}
