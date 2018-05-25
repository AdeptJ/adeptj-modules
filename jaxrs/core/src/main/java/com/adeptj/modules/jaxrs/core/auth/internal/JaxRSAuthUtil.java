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

package com.adeptj.modules.jaxrs.core.auth.internal;

import com.adeptj.modules.commons.utils.Loggers;
import com.adeptj.modules.jaxrs.core.auth.JaxRSAuthenticationInfo;
import com.adeptj.modules.jaxrs.core.auth.SimpleCredentials;
import com.adeptj.modules.jaxrs.core.auth.api.JaxRSAuthenticationRealm;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * Utility methods for {@link JaxRSAuthenticationInfo} and {@link JaxRSAuthenticationRealm}
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class JaxRSAuthUtil {

    private JaxRSAuthUtil() {
    }

    static JaxRSAuthenticationInfo getJaxRSAuthInfo(JaxRSAuthenticationRealm realm, String username, String password) {
        JaxRSAuthenticationInfo authInfo = null;
        try {
            authInfo = realm.getAuthenticationInfo(username, password);
        } catch (Exception ex) { // NOSONAR
            // Gulping everything so that next realms(if any) getAuthInfo a chance.
            Loggers.get(JaxRSAuthUtil.class).error(ex.getMessage(), ex);
        }
        return authInfo;
    }

    static JaxRSAuthenticationInfo validateCredentials(String username, String password) {
        JaxRSAuthenticationInfo authInfo = JaxRSAuthenticationInfoHolder.getInstance().getAuthInfo(username);
        if (authInfo == null) {
            return null;
        }
        SimpleCredentials credentials = authInfo.getCredentials();
        return StringUtils.equals(credentials.getUsername(), username)
                && Arrays.equals(password.toCharArray(), credentials.getPassword()) ? authInfo : null;
    }
}
