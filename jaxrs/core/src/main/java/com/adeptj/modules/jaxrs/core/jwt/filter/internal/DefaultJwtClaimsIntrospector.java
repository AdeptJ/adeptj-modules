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

import com.adeptj.modules.jaxrs.core.jwt.JwtClaimsIntrospector;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

/**
 * Default implementation of {@link JwtClaimsIntrospector}, which just checks whether the Jwt is expired or not.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class DefaultJwtClaimsIntrospector implements JwtClaimsIntrospector {

    static final JwtClaimsIntrospector INSTANCE = new DefaultJwtClaimsIntrospector();

    @Override
    public void introspect(ContainerRequestContext requestContext, Map<String, Object> claims) {
        if (this.isJwtExpiredKeyExists(claims)) {
            // Send Unauthorized if JwtService finds token to be expired.
            // 401 is better suited for token verification failure.
            requestContext.abortWith(Response.status(UNAUTHORIZED).build());
        } else {
            this.setJwtSecurityContext(requestContext, claims);
        }
    }
}
