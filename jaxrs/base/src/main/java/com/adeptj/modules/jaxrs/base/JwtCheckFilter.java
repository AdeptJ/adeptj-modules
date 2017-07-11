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

import com.adeptj.modules.security.jwt.JwtService;

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
 * Gets the HTTP Authorization header from the request and checks for the JWT (the Bearer string).
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@Provider
@RequiresJwtCheck
@Priority(AUTHENTICATION)
public class JwtCheckFilter implements ContainerRequestFilter {

    private static final int LEN = "Bearer".length();

    private JwtService jwtService;

    public JwtCheckFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if(!this.jwtService.parseToken(substring(requestContext.getHeaderString(AUTHORIZATION), LEN))) {
            requestContext.abortWith(Response.status(UNAUTHORIZED).build());
        }
    }
}
