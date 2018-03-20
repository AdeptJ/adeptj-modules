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
import com.adeptj.modules.jaxrs.core.JaxRSResponses;
import com.adeptj.modules.security.jwt.JwtService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import static com.adeptj.modules.jaxrs.core.jwt.JwtFilter.PROVIDER_OSGI_PROPERTY;
import static javax.ws.rs.Priorities.AUTHENTICATION;

/**
 * This filter will kick in for any resource method that is annotated with {@link RequiresJwt} annotation.
 * Filter will try to resolve the Jwt first from HTTP Authorization header and if that resolves to null
 * then try to resolve from Cookies.
 * <p>
 * A Cookie named as per configuration should be present in request.
 * <p>
 * If a non null Jwt is resolved then verify it using {@link JwtService}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@RequiresJwt
@Priority(AUTHENTICATION)
@Provider
@Component(immediate = true, property = PROVIDER_OSGI_PROPERTY)
public class JwtFilter implements ContainerRequestFilter {

    static final String PROVIDER_OSGI_PROPERTY = "osgi.jaxrs.provider=JwtFilter";

    private static final String BIND_JWT_SERVICE = "bindJwtService";

    private static final String UNBIND_JWT_SERVICE = "unbindJwtService";

    /**
     * The {@link JwtService} is optionally referenced.
     * If unavailable this filter will set a Service Unavailable (503) status.
     * <p>
     * Note: As per Felix SCR, dynamic references should be declared as volatile.
     */
    @Reference(
            bind = BIND_JWT_SERVICE,
            unbind = UNBIND_JWT_SERVICE,
            cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC
    )
    private volatile JwtService jwtService;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (this.jwtService == null) {
            requestContext.abortWith(JaxRSResponses.unavailable());
            return;
        }
        JwtUtil.resolveAndVerifyJwt(requestContext, this.jwtService);
    }

    // JwtService lifecycle methods

    protected void bindJwtService(JwtService jwtService) {
        Loggers.get(JwtFilter.class).info("JwtFilter injected with JwtService: [{}]", jwtService);
        this.jwtService = jwtService;
    }

    protected void unbindJwtService(JwtService jwtService) {
        this.jwtService = null;
    }
}
