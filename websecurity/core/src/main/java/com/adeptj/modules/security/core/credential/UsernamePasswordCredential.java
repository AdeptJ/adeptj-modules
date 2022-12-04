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

package com.adeptj.modules.security.core.credential;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;

import static com.adeptj.modules.security.core.SecurityConstants.LOGIN_URI_SUFFIX;
import static com.adeptj.modules.security.core.SecurityConstants.METHOD_POST;
import static com.adeptj.modules.security.core.SecurityConstants.PARAM_J_PASSWORD;
import static com.adeptj.modules.security.core.SecurityConstants.PARAM_J_USERNAME;

/**
 * Represents simple username/password credentials.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class UsernamePasswordCredential implements Credential {

    private static final char[] EMPTY_CHAR_ARRAY = new char[0];

    private final String username;

    private char[] password;

    private UsernamePasswordCredential(String username, char[] password) {
        this.username = username;
        this.password = password;
    }

    public static @Nullable UsernamePasswordCredential from(@NotNull HttpServletRequest request) {
        String username = request.getParameter(PARAM_J_USERNAME);
        String password = request.getParameter(PARAM_J_PASSWORD);
        if (METHOD_POST.equals(request.getMethod()) && StringUtils.isNoneEmpty(username, password)
                && StringUtils.endsWith(request.getRequestURI(), LOGIN_URI_SUFFIX)) {
            return new UsernamePasswordCredential(username, password.toCharArray());
        }
        return null;
    }

    public String getUsername() {
        return this.username;
    }

    public char[] getPassword() {
        return this.password;
    }

    @Override
    public void clear() {
        if (EMPTY_CHAR_ARRAY == this.password) {
            return;
        }
        char[] temp = this.password;
        this.password = EMPTY_CHAR_ARRAY;
        Arrays.fill(temp, (char) 0x00);
    }

    // <---------------- Generated ----------------->

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsernamePasswordCredential that = (UsernamePasswordCredential) o;
        return Objects.equals(this.username, that.username) && Arrays.equals(this.password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.username);
    }
}
