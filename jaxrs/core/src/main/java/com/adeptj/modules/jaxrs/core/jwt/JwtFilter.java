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

import com.adeptj.modules.jaxrs.core.RequiresJwt;
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
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

/**
 * This filter will kick in for any resource method that is annotated with {@link RequiresJwt} annotation.
 * Filter will try to resolve the Jwt first from HTTP Authorization header and if that resolves to null
 * then try to resolve from Cookies(a Cookie named jwt should be present in request).
 * <p>
 * If a non null Jwt is resolved then verify it using {@link JwtService}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Provider
@RequiresJwt
@Priority(AUTHENTICATION)
public class JwtFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);

    /**
     * Bearer schema string literal length + 1. "Bearer".length() is 6.
     */
    private static final int LEN = 7;

    private static final String JWT_COOKIE_NAME = "jwt";

    private static final String AUTH_SCHEME_BEARER = "Bearer";

    private volatile JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
        LOGGER.info("Initialized JwtFilter with JwtService: [{}]", this.jwtService);
    }

    public void setJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (this.jwtService == null) {
            LOGGER.warn("Can't verify JWT as JwtService unavailable!");
            requestContext.abortWith(Response.status(SERVICE_UNAVAILABLE).build());
        } else {
            this.verifyJwt(requestContext);
        }
    }

    private void verifyJwt(ContainerRequestContext requestContext) {
        String jwt = this.resolveJwt(requestContext);
        if (StringUtils.isEmpty(jwt)) {
            requestContext.abortWith(Response.status(UNAUTHORIZED).build());
        } else if (!this.jwtService.verify(jwt)) {
            requestContext.abortWith(Response.status(FORBIDDEN).build());
        }
    }

    private String resolveJwt(ContainerRequestContext requestContext) {
        return Optional.ofNullable(this.resolveFromHeaders(requestContext)).orElseGet(() ->
                this.resolveFromCookies(requestContext));
    }

    private String resolveFromHeaders(ContainerRequestContext requestContext) {
        return StringUtils.substring(requestContext.getHeaderString(AUTHORIZATION), LEN);
    }

    private String resolveFromCookies(ContainerRequestContext requestContext) {
        Cookie cookie = requestContext.getCookies().get(JWT_COOKIE_NAME);
        String value = null;
        if (cookie != null) {
            value = cookie.getValue();
            if (StringUtils.startsWith(value, AUTH_SCHEME_BEARER)) {
                value = StringUtils.substring(value, AUTH_SCHEME_BEARER.length());
            }
            value = StringUtils.deleteWhitespace(value);
        }
        return value;
    }
}
