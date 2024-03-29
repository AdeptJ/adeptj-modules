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
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static org.osgi.service.component.annotations.ReferenceCardinality.OPTIONAL;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

/**
 * This filter will kick in for resource classes and methods configured by JwtDynamicFeature.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@JwtFilterType("dynamic")
@Component(service = JwtClaimsIntrospectionFilter.class, immediate = true)
public class DynamicJwtClaimsIntrospectionFilter extends AbstractJwtClaimsIntrospectionFilter {

    // <<------------------------------------------- OSGi Internal ------------------------------------------->>

    @Reference(service = JwtClaimsIntrospector.class, cardinality = OPTIONAL, policy = DYNAMIC)
    protected void bindJwtClaimsIntrospector(JwtClaimsIntrospector claimsIntrospector) {
        this.claimsIntrospector = claimsIntrospector;
    }

    protected void unbindJwtClaimsIntrospector(JwtClaimsIntrospector claimsIntrospector) {
        if (this.claimsIntrospector == claimsIntrospector) {
            this.claimsIntrospector = null;
        }
    }
}
