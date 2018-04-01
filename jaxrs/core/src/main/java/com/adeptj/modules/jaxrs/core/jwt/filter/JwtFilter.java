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

package com.adeptj.modules.jaxrs.core.jwt.filter;

import com.adeptj.modules.jaxrs.core.JaxRSResponses;
import com.adeptj.modules.jaxrs.core.jwt.JwtUtil;
import com.adeptj.modules.security.jwt.JwtService;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

/**
 * Interface helping in exposing {@link JwtFilter} as a service in OSGi service registry.
 * Thereafter can be injected as a reference in other services and components.
 * <p>
 * It is implemented in two variants as described below.
 * <p>
 * 1. {@link com.adeptj.modules.jaxrs.core.jwt.filter.internal.StaticJwtFilter} deals with
 * {@link com.adeptj.modules.jaxrs.core.jwt.RequiresJwt} annotated resource classes and methods.
 * <p>
 * 2. {@link com.adeptj.modules.jaxrs.core.jwt.filter.internal.DynamicJwtFilter} deals with
 * resource classes and methods configured via {@link com.adeptj.modules.jaxrs.core.jwt.feature.JwtDynamicFeature}
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public interface JwtFilter extends ContainerRequestFilter {

    JwtService getJwtService();

    default void doFilter(ContainerRequestContext requestContext) {
        if (this.getJwtService() == null) {
            requestContext.abortWith(JaxRSResponses.unavailable());
            return;
        }
        JwtUtil.resolveAndVerifyJwt(requestContext, this.getJwtService());
    }
}
