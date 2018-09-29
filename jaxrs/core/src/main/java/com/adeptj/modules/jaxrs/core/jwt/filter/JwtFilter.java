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

package com.adeptj.modules.jaxrs.core.jwt.filter;

import com.adeptj.modules.jaxrs.core.jwt.JwtResolver;
import com.adeptj.modules.jaxrs.core.jwt.JwtSecurityContext;
import com.adeptj.modules.security.jwt.ClaimsDecorator;
import com.adeptj.modules.security.jwt.JwtService;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;

import static com.adeptj.modules.jaxrs.core.JaxRSConstants.AUTH_SCHEME_TOKEN;
import static com.adeptj.modules.jaxrs.core.JaxRSConstants.CLAIMS_REQ_ATTR;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

/**
 * Interface helping in exposing {@link JwtFilter} as a service in OSGi service registry.
 * Thereafter can be injected as a reference in other services and components.
 * <p>
 * It is implemented in two variants as described below.
 * <p>
 * 1. {@link com.adeptj.modules.jaxrs.core.jwt.filter.internal.StaticJwtFilter} deals with
 * {@link com.adeptj.modules.jaxrs.core.jwt.RequiresJwt} annotated resource classes and methods.
 * <p>
 * 2. {@link com.adeptj.modules.jaxrs.core.jwt.filter.internal.DynamicJwtFilter} deals with
 * resource classes and methods configured via {@link com.adeptj.modules.jaxrs.core.jwt.feature.JwtDynamicFeature}
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public interface JwtFilter extends ContainerRequestFilter {

    String BIND_JWT_SERVICE = "bindJwtService";

    String UNBIND_JWT_SERVICE = "unbindJwtService";

    default void doFilter(ContainerRequestContext requestContext, JwtService jwtService) {
        if (jwtService == null) {
            requestContext.abortWith(Response.status(SERVICE_UNAVAILABLE).build());
            return;
        }
        String jwt = JwtResolver.resolve(requestContext);
        // Send Unauthorized if JWT is null/empty or JwtService finds token to be malformed, expired etc.
        // 401 is better suited for token verification failure.
        if (StringUtils.isEmpty(jwt)) {
            requestContext.abortWith(Response.status(UNAUTHORIZED).build());
        } else {
            ClaimsDecorator claimsDecorator = jwtService.verifyJwt(jwt);
            if (StringUtils.isEmpty(claimsDecorator.getSubject())) {
                requestContext.abortWith(Response.status(UNAUTHORIZED).build());
            } else {
                requestContext.setProperty(CLAIMS_REQ_ATTR, claimsDecorator.getClaims());
                requestContext.setSecurityContext(JwtSecurityContext.newSecurityContext()
                        .withSubject(claimsDecorator.getSubject())
                        .withRoles(claimsDecorator.getRoles())
                        .withSecure(requestContext.getSecurityContext().isSecure())
                        .withAuthScheme(AUTH_SCHEME_TOKEN));
            }
        }
    }
}
