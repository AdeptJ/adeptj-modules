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
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;

import static com.adeptj.modules.jaxrs.core.JaxRSConstants.AUTH_SCHEME_BEARER;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

/**
 * Utility resolves Jwt either from request headers or from cookies only if not found in headers
 * and depending upon the outcome further pass the jwt for verification to {@link JwtService}.
 * <p>
 * Also sets response header(401) if JWT is null or JwtService finds token to be malformed, expired etc.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class JwtUtil {

    private static final int JWT_START_POS = 7;

    static void handleJwt(ContainerRequestContext requestContext, JwtService jwtService) {
        String jwt = resolveJwt(requestContext);
        // Send Unauthorized if JWT is null or JwtService finds token to be malformed, expired etc.
        // 401 is better suited for token verification failure.
        if (StringUtils.isEmpty(jwt) || !jwtService.verifyJwt(jwt)) {
            requestContext.abortWith(Response.status(UNAUTHORIZED).build());
        }
    }

    private static String resolveJwt(ContainerRequestContext requestContext) {
        String jwt = resolveFromHeaders(requestContext);
        return StringUtils.isEmpty(jwt) ? resolveFromCookies(requestContext) : jwt;
    }

    private static String resolveFromHeaders(ContainerRequestContext requestContext) {
        return cleanseJwt(requestContext.getHeaders().getFirst(AUTHORIZATION));
    }

    private static String resolveFromCookies(ContainerRequestContext requestContext) {
        Cookie jwtCookie = requestContext
                .getCookies()
                .get(JwtIssuer.JwtCookieNameProvider.INSTANCE.getJwtCookieName());
        return jwtCookie == null ? null : cleanseJwt(jwtCookie.getValue());
    }

    private static String cleanseJwt(String jwt) {
        return StringUtils.startsWith(jwt, AUTH_SCHEME_BEARER) ?
                StringUtils.substring(jwt, JWT_START_POS) : StringUtils.trim(jwt);
    }

    // Just static utilities, no instance needed.
    private JwtUtil() {
    }
}
