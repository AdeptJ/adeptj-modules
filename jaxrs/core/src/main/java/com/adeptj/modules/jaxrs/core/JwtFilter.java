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
package com.adeptj.modules.jaxrs.core;

import com.adeptj.modules.security.jwt.JwtService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Optional;

import static javax.ws.rs.Priorities.AUTHENTICATION;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;

/**
 * Gets the JWT from HTTP Authorization header or Cookie and verify it using JwtService.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Provider
@RequiresJwt
@Priority(AUTHENTICATION)
public class JwtFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);

    /**
     * Bearer schema string length + 1
     */
    private static final int LEN = 7;

    private static final String JWT_COOKIE_NAME = "jwt";

    private volatile JwtService jwtService;

    public void setJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (this.jwtService == null) {
            LOGGER.warn("Can't verify JWT as JwtService unavailable!");
            this.abort(requestContext, SERVICE_UNAVAILABLE, "JwtService unavailable!!");
            return;
        }
        this.handleSecurity(requestContext);
    }

    private void handleSecurity(ContainerRequestContext requestContext) {
        String jwt = this.resolveJwt(requestContext);
        if (StringUtils.isEmpty(jwt)) {
            this.abort(requestContext, BAD_REQUEST, "JWT missing from request!!");
        } else if (!this.jwtService.verify(jwt)) {
            this.abort(requestContext, FORBIDDEN, "Invalid JWT!!");
        }
    }

    private String resolveJwt(ContainerRequestContext requestContext) {
        return Optional.ofNullable(StringUtils.substring(requestContext.getHeaderString(AUTHORIZATION), LEN))
                .orElseGet(() -> {
                    Cookie tokenCookie = requestContext.getCookies().get(JWT_COOKIE_NAME);
                    if (tokenCookie == null) {
                        LOGGER.warn("Exhausted all options to resolve JWT!!");
                    } else {
                        return tokenCookie.getValue();
                    }
                    return null;
                });
    }

    private void abort(ContainerRequestContext ctx, Response.Status status, Object entity) {
        ctx.abortWith(Response.status(status).entity(entity).build());
    }
}
