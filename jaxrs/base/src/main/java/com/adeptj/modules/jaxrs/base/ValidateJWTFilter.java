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
package com.adeptj.modules.jaxrs.base;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

import static javax.ws.rs.Priorities.AUTHENTICATION;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.apache.commons.lang3.StringUtils.substring;

/**
 * Gets the HTTP Authorization header from the request and checks for the JSon Web Token (the Bearer string).
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@Provider
@ValidateJWT
@Priority(AUTHENTICATION)
public class ValidateJWTFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateJWTFilter.class);

    private static final int LEN = "Bearer".length();

    private JaxRSAuthRepository authRepository;

    public ValidateJWTFilter(JaxRSAuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        try {
            String subject = requestContext.getHeaderString("subject");
            JaxRSAuthConfig authConfig = this.authRepository.getAuthConfig(subject);
            if (authConfig == null) {
                requestContext.abortWith(Response.status(UNAUTHORIZED).build());
            } else {
                Jwts.parser()
                        .setSigningKey(authConfig.getSigningKey())
                        .parseClaimsJws(substring(requestContext.getHeaderString(AUTHORIZATION), LEN));
            }
        } catch (SignatureException | ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException ex) {
            LOGGER.error("Invalid JWT!!", ex);
            requestContext.abortWith(Response.status(UNAUTHORIZED).build());
        }
    }
}
