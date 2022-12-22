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

package com.adeptj.modules.jaxrs.core.jwt.resource;

import com.adeptj.modules.security.jwt.JwtService;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Cookie;

import static com.adeptj.modules.jaxrs.api.JaxRSConstants.AUTH_SCHEME_BEARER_WITH_SPACE;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;

/**
 * Utility extracts Jwt either from request headers or cookies.
 * <p>
 * Here is the extraction process.
 * <p>
 * 1. Check if the cookie mechanism is enabled.
 * 2. If enabled then look into cookies
 * 3. If still not found then look into headers.
 * <p>
 * Depending upon the outcome, pass the jwt to {@link JwtService} for verification.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class JwtExtractor {

    // Just static utilities, no instance needed.
    private JwtExtractor() {
    }

    public static String extract(ContainerRequestContext requestContext, JwtCookieConfig cookieConfig) {
        String jwt = null;
        // if JwtCookieConfig is enabled then always extract the Jwt from cookies first.
        if (cookieConfig != null && cookieConfig.enabled()) {
            Cookie jwtCookie = requestContext.getCookies().get(cookieConfig.name());
            if (jwtCookie != null) {
                jwt = StringUtils.trim(jwtCookie.getValue());
            }
        }
        return StringUtils.isEmpty(jwt) ? extractFromAuthorizationHeader(requestContext) : jwt;
    }

    private static @Nullable String extractFromAuthorizationHeader(@NotNull ContainerRequestContext requestContext) {
        String bearerToken = requestContext.getHeaderString(AUTHORIZATION);
        return StringUtils.startsWith(bearerToken, AUTH_SCHEME_BEARER_WITH_SPACE)
                ? StringUtils.substring(bearerToken, AUTH_SCHEME_BEARER_WITH_SPACE.length()).trim()
                : null;
    }
}
