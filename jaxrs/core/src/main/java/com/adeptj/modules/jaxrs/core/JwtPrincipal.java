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

package com.adeptj.modules.jaxrs.core;

import com.adeptj.modules.security.jwt.JwtClaims;

import java.security.Principal;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Principal} implementation which holds the Jwt claims.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class JwtPrincipal implements Principal {

    private final JwtClaims claims;

    public JwtPrincipal(JwtClaims claims) {
        this.claims = claims;
    }

    @Override
    public String getName() {
        return this.claims.getSubject();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        JwtPrincipal principal = (JwtPrincipal) other;
        return this.getName().equals(principal.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getName());
    }

    @Override
    public String toString() {
        return "JwtPrincipal {" + "name='" + this.getName() + '\'' + '}';
    }

    public Map<String, Object> getClaims() {
        return this.claims.asMap();
    }

    public boolean isHoldingExpiredJwt() {
        return this.claims.isExpired();
    }
}
