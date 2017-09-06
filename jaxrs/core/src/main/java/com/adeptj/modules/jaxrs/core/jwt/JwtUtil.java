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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.Map;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

/**
 * Resolves Jwt either from request headers or from cookies only if not found in headers.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class JwtUtil {

    /**
     * Bearer auth scheme string literal length + 1. "Bearer".length() is 6.
     */
    private static final int JWT_START_POS = 7;

    private static final String JWT_COOKIE_NAME = "jwt";

    private static final String AUTH_SCHEME_BEARER = "Bearer";

    private static final String HEADER_SUBJECT = "Subject";

    static void handleJwt(ContainerRequestContext requestContext, JwtService jwtService) {
        String subject = requestContext.getHeaderString(HEADER_SUBJECT);
        if (StringUtils.isEmpty(subject)) {
            requestContext.abortWith(Response.status(BAD_REQUEST)
                    .entity("Request missing [Subject] header!!")
                    .build());
        } else {
            String jwt = resolveJwt(requestContext);
            if (StringUtils.isEmpty(jwt)) {
                requestContext.abortWith(Response.status(UNAUTHORIZED).build());
            } else if (!jwtService.verify(subject, jwt)) {
                requestContext.abortWith(Response.status(FORBIDDEN).build());
            }
        }
    }

    private static String resolveJwt(ContainerRequestContext requestContext) {
        String jwt = resolveFromHeaders(requestContext.getHeaders());
        return StringUtils.isEmpty(jwt) ? resolveFromCookies(requestContext.getCookies()) : jwt;
    }

    private static String resolveFromHeaders(MultivaluedMap<String, String> headers) {
        return StringUtils.substring(headers.getFirst(AUTHORIZATION), JWT_START_POS);
    }

    private static String resolveFromCookies(Map<String, Cookie> cookies) {
        Cookie cookie = cookies.get(getJwtCookieName());
        return cookie == null ? null : parseJwtCookie(cookie);
    }

    private static String getJwtCookieName() {
        String jwtCookieName = JwtIssuer.JwtCookieNameProvider.INSTANCE.getJwtCookieName();
        if (StringUtils.isEmpty(jwtCookieName)) {
            jwtCookieName = JWT_COOKIE_NAME;
        }
        return jwtCookieName;
    }

    private static String parseJwtCookie(Cookie cookie) {
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

    // Just static utilities, no instance needed.
    private JwtUtil() {
    }
}
