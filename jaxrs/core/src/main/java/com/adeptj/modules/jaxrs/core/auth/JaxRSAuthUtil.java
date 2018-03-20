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

import com.adeptj.modules.jaxrs.core.auth.api.JaxRSAuthenticationRealm;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Utility methods for {@link JaxRSAuthenticationInfo} and {@link JaxRSAuthenticationRealm}
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class JaxRSAuthUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRSAuthUtil.class);

    private JaxRSAuthUtil() {
    }

    public static void validateJaxRSAuthInfoArgs(String username, char[] password) {
        if (StringUtils.isEmpty(username)) {
            throw new IllegalArgumentException("username can't be null or empty!!");
        }
        if (ArrayUtils.isEmpty(password)) {
            throw new IllegalArgumentException("password can't be null or empty!!");
        }
    }

    public static JaxRSAuthenticationInfo getJaxRSAuthInfo(JaxRSAuthenticationRealm realm, String username, String password) {
        JaxRSAuthenticationInfo authInfo = null;
        try {
            authInfo = realm.getAuthenticationInfo(username, password);
        } catch (Exception ex) { // NOSONAR
            // Gulping everything so that next realms(if any) get a chance.
            LOGGER.error(ex.getMessage(), ex);
        }
        return authInfo;
    }

    public static JaxRSAuthenticationInfo validateJaxRSAuthInfo(JaxRSAuthenticationInfo authInfo, String username, String password) {
        return authInfo != null && StringUtils.equals(authInfo.getUsername(), username)
                && Arrays.equals(password.toCharArray(), authInfo.getPassword())
                ? authInfo : null;
    }
}
