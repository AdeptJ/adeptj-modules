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

package com.adeptj.modules.jaxrs.core.auth;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * Represents simple user ID/password credentials.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class SimpleCredentials {

    private String username;

    private char[] password;

    public SimpleCredentials(String username, char[] password) {
        Validate.isTrue(StringUtils.isNotEmpty(username), "username can't be null or empty!!");
        Validate.isTrue(ArrayUtils.isNotEmpty(password), "password can't be null or empty!!");
        this.username = username;
        this.password = password;
    }

    public static SimpleCredentials of(String username, char[] password) {
        return new SimpleCredentials(username, password);
    }

    public String getUsername() {
        return username;
    }

    public char[] getPassword() {
        return password;
    }
}
