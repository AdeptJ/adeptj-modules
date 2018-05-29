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
package com.adeptj.modules.jaxrs.core.jwt.filter.internal;

import com.adeptj.modules.jaxrs.core.JaxRSProvider;
import com.adeptj.modules.jaxrs.core.jwt.RequiresJwt;
import com.adeptj.modules.jaxrs.core.jwt.filter.JwtFilter;
import com.adeptj.modules.security.jwt.JwtService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.ext.Provider;
import java.util.Objects;

import static com.adeptj.modules.jaxrs.core.jwt.filter.internal.StaticJwtFilter.FILTER_NAME;
import static javax.ws.rs.Priorities.AUTHENTICATION;

/**
 * This filter will kick in for any resource class/method that is annotated with {@link RequiresJwt}.
 * Filter will try to resolve the Jwt first from HTTP Authorization header and if that resolves to null
 * then try to resolve from Cookies.
 * <p>
 * A Cookie named as per configuration should be present in request.
 * <p>
 * If a non null Jwt is resolved then verify it using {@link JwtService}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@JaxRSProvider(name = "StaticJwtFilter")
@RequiresJwt
@Priority(AUTHENTICATION)
@Provider
@Component(immediate = true, property = FILTER_NAME)
public class StaticJwtFilter implements JwtFilter {

    static final String FILTER_NAME = "jwt.filter.name=static";

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
        this.doFilter(requestContext, this.jwtService);
    }

    // ------------------------------------------ INTERNAL ------------------------------------------

    protected void bindJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    protected void unbindJwtService(JwtService jwtService) {
        if (Objects.equals(jwtService, this.jwtService)) {
            this.jwtService = null;
        }
    }
}
