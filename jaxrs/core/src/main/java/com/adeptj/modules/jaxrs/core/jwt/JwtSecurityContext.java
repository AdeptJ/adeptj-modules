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

package com.adeptj.modules.jaxrs.core.jwt;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.Set;

/**
 * JAX-RS {@link SecurityContext}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class JwtSecurityContext implements SecurityContext {

    private String subject;

    private Set<String> roles;

    private boolean secure;

    private String authScheme;

    private JwtSecurityContext() {
    }

    public static JwtSecurityContext newSecurityContext() {
        return new JwtSecurityContext();
    }

    public JwtSecurityContext withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public JwtSecurityContext withRoles(Set<String> roles) {
        this.roles = roles;
        return this;
    }

    public JwtSecurityContext withSecure(boolean secure) {
        this.secure = secure;
        return this;
    }

    public JwtSecurityContext withAuthScheme(String authScheme) {
        this.authScheme = authScheme;
        return this;
    }

    @Override
    public Principal getUserPrincipal() {
        return () -> this.subject;
    }

    @Override
    public boolean isUserInRole(String role) {
        return this.roles != null && this.roles.contains(role);
    }

    @Override
    public boolean isSecure() {
        return this.secure;
    }

    @Override
    public String getAuthenticationScheme() {
        return this.authScheme;
    }
}
