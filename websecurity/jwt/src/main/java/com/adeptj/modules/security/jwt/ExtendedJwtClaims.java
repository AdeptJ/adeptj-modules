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

package com.adeptj.modules.security.jwt;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static io.jsonwebtoken.Claims.SUBJECT;

/**
 * Extended claims map with extra information such as roles etc. if any.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class ExtendedJwtClaims {

    private String subject;

    private Set<String> roles;

    private Map<String, Object> claims;

    public ExtendedJwtClaims() {
        this.claims = new HashMap<>();
    }

    public String getSubject() {
        return subject;
    }

    public Map<String, Object> getClaims() {
        return claims;
    }

    public Set<String> getRoles() {
        return roles;
    }

    @SuppressWarnings("unchecked")
    public ExtendedJwtClaims addClaims(Map<String, Object> claims) {
        this.claims.putAll(claims);
        this.subject = (String) this.claims.get(SUBJECT);
        this.roles = (Set<String>) this.claims.get("roles");
        return this;
    }
}
