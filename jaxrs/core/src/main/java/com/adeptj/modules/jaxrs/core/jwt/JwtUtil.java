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

import com.adeptj.modules.jaxrs.core.CookieBuilder;
import com.adeptj.modules.jaxrs.core.JaxRSResponses;
import com.adeptj.modules.security.jwt.JwtService;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import static com.adeptj.modules.jaxrs.core.JaxRSConstants.AUTH_SCHEME_BEARER;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.apache.commons.lang3.StringUtils.SPACE;

/**
 * Utility resolves Jwt either from request headers or from cookies only if not found in headers
 * and depending upon the outcome further pass the jwt to {@link JwtService} for verification.
 * <p>
 * Also sets response header(401) if JWT is null/empty or JwtService finds token to be malformed, expired etc.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class JwtUtil {

    private static final int JWT_START_POS = 7;

    public static void resolveAndVerifyJwt(ContainerRequestContext requestContext, JwtService jwtService) {
        String jwt = resolveJwt(requestContext);
        // Send Unauthorized if JWT is null/empty or JwtService finds token to be malformed, expired etc.
        // 401 is better suited for token verification failure.
        if (StringUtils.isEmpty(jwt) || !jwtService.verifyJwt(jwt)) {
            requestContext.abortWith(JaxRSResponses.unauthorized());
        }
    }

    static Response responseWithJwt(String jwt) {
        JwtCookieConfig cookieConfig = JwtCookieConfigHolder.INSTANCE.getJwtCookieConfig();
        return cookieConfig.enabled()
                ? JaxRSResponses.okWithCookie(newJwtCookie(jwt, cookieConfig))
                : JaxRSResponses.okWithHeader(AUTHORIZATION, AUTH_SCHEME_BEARER + SPACE + jwt);
    }

    private static String resolveJwt(ContainerRequestContext requestContext) {
        String jwt = null;
        // if JwtCookieConfig is enabled then always resolve the Jwt from cookies first.
        if (JwtCookieConfigHolder.INSTANCE.getJwtCookieConfig().enabled()) {
            jwt = resolveFromCookies(requestContext);
        }
        return StringUtils.isEmpty(jwt) ? resolveFromHeaders(requestContext) : jwt;
    }

    private static String resolveFromHeaders(ContainerRequestContext requestContext) {
        return cleanseJwt(requestContext.getHeaders().getFirst(AUTHORIZATION));
    }

    private static String resolveFromCookies(ContainerRequestContext requestContext) {
        Cookie jwtCookie = requestContext
                .getCookies()
                .get(JwtCookieConfigHolder.INSTANCE.getJwtCookieConfig().name());
        return jwtCookie == null ? null : cleanseJwt(jwtCookie.getValue());
    }

    private static String cleanseJwt(String jwt) {
        return StringUtils.startsWith(jwt, AUTH_SCHEME_BEARER) ?
                StringUtils.substring(jwt, JWT_START_POS) : StringUtils.trim(jwt);
    }

    private static NewCookie newJwtCookie(String jwt, JwtCookieConfig cookieConfig) {
        return CookieBuilder.builder()
                .name(cookieConfig.name())
                .value(jwt)
                .domain(cookieConfig.domain())
                .path(cookieConfig.path())
                .comment(cookieConfig.comment())
                .maxAge(cookieConfig.maxAge())
                .secure(cookieConfig.secure())
                .httpOnly(cookieConfig.httpOnly())
                .build()
                .getCookie();
    }

    // Just static utilities, no instance needed.
    private JwtUtil() {
    }
}
