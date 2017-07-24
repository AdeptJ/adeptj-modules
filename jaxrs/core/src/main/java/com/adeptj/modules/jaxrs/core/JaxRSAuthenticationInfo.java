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

import java.util.HashMap;

/**
 * AuthenticationInfo holding subject, password and other arbitrary data for JWT based JAX-RS resource authorization.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class JaxRSAuthenticationInfo extends HashMap<String, Object> {

    private String subject;

    private char[] password;

    public JaxRSAuthenticationInfo(String subject, char[] password) {
        this.subject = subject;
        this.password = password;
    }

    public String getSubject() {
        return subject;
    }

    public char[] getPassword() {
        return password;
    }
}
