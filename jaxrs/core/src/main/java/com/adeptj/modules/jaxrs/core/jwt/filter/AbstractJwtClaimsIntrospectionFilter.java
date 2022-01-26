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

import com.adeptj.modules.jaxrs.api.JwtClaimsIntrospector;
import com.adeptj.modules.jaxrs.core.jwt.JwtSecurityContext;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

/**
 * Implements the {@link #filter} from {@link ContainerRequestContext}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public abstract class AbstractJwtClaimsIntrospectionFilter implements JwtClaimsIntrospectionFilter {

    protected JwtClaimsIntrospector claimsIntrospector;

    public AbstractJwtClaimsIntrospectionFilter() {
        this.claimsIntrospector = DefaultJwtClaimsIntrospector.INSTANCE;
    }

    /**
     * This method Checks if the SecurityContext is an instance of {@link JwtSecurityContext}, if yes, then introspect
     * the JwtClaims using either the default implementation of JwtClaimsIntrospector or with the one provided by consumer.
     * <p>
     * Else send an Unauthorized(401) and abort the request processing.
     *
     * @param requestContext the JaxRS request context
     */
    @Override
    public void filter(@NotNull ContainerRequestContext requestContext) {
        if (requestContext.getSecurityContext() instanceof JwtSecurityContext) {
            // Since JwtClaimsIntrospector is a dynamic service so assign this to a local variable.
            JwtClaimsIntrospector introspector = this.claimsIntrospector;
            introspector.introspect(requestContext);
        } else {
            requestContext.abortWith(Response.status(UNAUTHORIZED).build());
        }
    }
}
