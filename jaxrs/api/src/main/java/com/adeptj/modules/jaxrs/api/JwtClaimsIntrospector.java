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

package com.adeptj.modules.jaxrs.api;

import org.osgi.annotation.versioning.ConsumerType;

import jakarta.ws.rs.container.ContainerRequestContext;

/**
 * Service interface for introspecting the JWT claims(Registered as well as public).
 * <p>
 * Implementation should inspect the claims passed and validate claims values as per their need.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ConsumerType
public interface JwtClaimsIntrospector {

    /**
     * Introspect the JWT claims passed.
     * <p>
     * Registered Claims such as iss, sub, exp are already validated by JwtService while parsing the JWT,
     * therefore should not be validated again.
     * <p>
     * Any public claims like username, roles and other important information can be introspected as per the need.
     * <p>
     * Implementation must check if the jwt is expired, if so then take the appropriate action such as abort
     * the request processing by setting a 401.
     *
     * @param requestContext the JaxRS {@link ContainerRequestContext}
     */
    void introspect(ContainerRequestContext requestContext);
}
