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
import com.adeptj.modules.jaxrs.core.RequiresAuthentication;
import com.adeptj.modules.jaxrs.core.jwt.JwtClaimsIntrospector;
import com.adeptj.modules.jaxrs.core.jwt.filter.JwtClaimsIntrospectionFilter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.annotation.Priority;
import javax.ws.rs.ext.Provider;

import static com.adeptj.modules.jaxrs.core.jwt.filter.internal.StaticJwtClaimsIntrospectionFilter.FILTER_NAME;
import static javax.ws.rs.Priorities.AUTHORIZATION;
import static org.osgi.service.component.annotations.ReferenceCardinality.OPTIONAL;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

/**
 * This filter will kick in for any resource class/method that is annotated with {@link RequiresAuthentication}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@JaxRSProvider(name = "StaticJwtFilter")
@RequiresAuthentication
@Priority(AUTHORIZATION)
@Provider
@Component(service = JwtClaimsIntrospectionFilter.class, immediate = true, property = FILTER_NAME)
public class StaticJwtClaimsIntrospectionFilter extends AbstractJwtClaimsIntrospectionFilter {

    static final String FILTER_NAME = "jwt.filter.type=static";

    public StaticJwtClaimsIntrospectionFilter() {
        super(DefaultJwtClaimsIntrospector.INSTANCE);
    }

    // <<------------------------------------------- OSGi INTERNAL ------------------------------------------->>

    @Reference(service = JwtClaimsIntrospector.class, cardinality = OPTIONAL, policy = DYNAMIC, policyOption = GREEDY)
    protected void bindJwtClaimsIntrospector(JwtClaimsIntrospector claimsIntrospector) {
        this.claimsIntrospector = claimsIntrospector;
    }

    protected void unbindJwtClaimsIntrospector(JwtClaimsIntrospector claimsIntrospector) {
        if (this.claimsIntrospector == claimsIntrospector) {
            this.claimsIntrospector = null;
        }
    }
}
