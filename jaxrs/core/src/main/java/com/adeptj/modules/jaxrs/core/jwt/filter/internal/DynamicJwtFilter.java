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

import com.adeptj.modules.jaxrs.core.jwt.filter.JwtFilter;
import com.adeptj.modules.security.jwt.JwtService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import javax.ws.rs.container.ContainerRequestContext;

/**
 * This filter will kick in for resource classes and methods configured by JwtDynamicFeature.
 * Filter will try to resolve the Jwt from HTTP Authorization header first and if that resolves to null
 * then try to resolve from Cookies.
 * However, in case JwtCookieConfig#enabled returns true then the functionality reversed.
 * <p>
 * A Cookie named as per configuration should be present in request.
 * <p>
 * If a non null Jwt is resolved then verify it using {@link JwtService}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(immediate = true, property = "filter.name=dyna-jwt")
public class DynamicJwtFilter implements JwtFilter {

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
        this.doFilter(requestContext);
    }

    @Override
    public JwtService getJwtService() {
        return this.jwtService;
    }

    // -------------- INTERNAL --------------
    // JwtService lifecycle methods

    protected void bindJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    protected void unbindJwtService(JwtService jwtService) {
        this.jwtService = null;
    }
}
