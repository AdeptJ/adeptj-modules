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

import static com.adeptj.modules.jaxrs.core.JaxRSConstants.AUTH_SCHEME_BEARER;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Utility resolves Jwt either from request headers or cookies.
 * <p>
 * Here is the resolution process.
 * <p>
 * 1. Check if the cookie mechanism is enabled.
 * 2. If enabled then look into cookies
 * 3. If still not found then look into headers.
 * <p>
 * Depending upon the outcome, pass the jwt to {@link JwtService} for verification.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class JwtResolver {

    private static final int JWT_START_POS = 7;

    // Just static utilities, no instance needed.
    private JwtResolver() {
    }

    public static String resolve(ContainerRequestContext requestContext) {
        String jwt = null;
        // if JwtCookieConfig is enabled then always resolve the Jwt from cookies first.
        if (JwtCookieConfigHolder.getInstance().isJwtCookieEnabled()) {
            jwt = resolveFromCookies(requestContext);
        }
        return StringUtils.isEmpty(jwt) ? resolveFromHeaders(requestContext) : jwt;
    }

    private static String resolveFromHeaders(ContainerRequestContext requestContext) {
        return cleanseJwt(requestContext.getHeaders().getFirst(AUTHORIZATION));
    }

    private static String resolveFromCookies(ContainerRequestContext requestContext) {
        Cookie jwtCookie = requestContext.getCookies()
                .get(JwtCookieConfigHolder.getInstance().getJwtCookieConfig().name());
        return jwtCookie == null ? EMPTY : cleanseJwt(jwtCookie.getValue());
    }

    private static String cleanseJwt(String jwt) {
        return StringUtils.startsWith(jwt, AUTH_SCHEME_BEARER) ?
                StringUtils.substring(jwt, JWT_START_POS) : StringUtils.trim(jwt);
    }
}
