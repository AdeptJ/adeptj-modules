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

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

/**
 * Utilities for JAX-RS {@link Response}, {@link NewCookie} etc.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class JaxRSUtil {

    // Just static utilities, no instance needed.
    private JaxRSUtil() {
    }

    static Response createResponseWithJwt(String jwt) {
        JwtCookieConfig cookieConfig = JwtCookieConfigHolder.getInstance().getJwtCookieConfig();
        return (cookieConfig != null && cookieConfig.enabled())
                ? Response.ok().cookie(newJwtCookie(jwt, cookieConfig)).build()
                : Response.ok().header(AUTHORIZATION, jwt).build();
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
}
