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

import java.util.HashMap;
import java.util.Set;

import static com.adeptj.modules.jaxrs.api.JaxRSConstants.JWT_CLAIM_ROLES;

/**
 * JaxRSAuthenticationOutcome holding arbitrary data for JWT based JAX-RS resource authorization.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class JaxRSAuthenticationOutcome extends HashMap<String, Object> {

    private static final long serialVersionUID = 7103662084651804227L;

    public JaxRSAuthenticationOutcome addJwtClaim(String name, Object value) {
        if (name != null && !name.isEmpty() && value != null) {
            super.put(name, value);
        }
        return this;
    }

    public JaxRSAuthenticationOutcome addRolesJwtClaim(Set<String> roles) {
        if (roles != null && !roles.isEmpty()) {
            super.put(JWT_CLAIM_ROLES, roles);
        }
        return this;
    }
}
