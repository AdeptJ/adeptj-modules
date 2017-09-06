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

import com.adeptj.modules.commons.utils.Loggers;
import com.adeptj.modules.security.jwt.JwtService;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

import static javax.ws.rs.Priorities.AUTHENTICATION;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;

/**
 * This filter will kick in for any resource method that is annotated with {@link RequiresJwt} annotation.
 * Filter will try to resolveJwt the Jwt first from HTTP Authorization header and if that resolves to null
 * then try to resolveJwt from Cookies.
 * A Cookie named (as per configuration or default jwt) should be present in request.
 * <p>
 * If a non null Jwt is resolved then verify it using {@link JwtService}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@RequiresJwt
@Priority(AUTHENTICATION)
@Provider
public class JwtFilter implements ContainerRequestFilter {

    private volatile JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
        Loggers.get(JwtFilter.class).info("Initialized JwtFilter with JwtService: [{}]", this.jwtService);
    }

    public void setJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (this.jwtService == null) {
            Loggers.get(JwtFilter.class).warn("Can't verify JWT as JwtService unavailable!");
            requestContext.abortWith(Response.status(SERVICE_UNAVAILABLE).build());
        } else {
            JwtUtil.handleJwt(requestContext, this.jwtService);
        }
    }
}
