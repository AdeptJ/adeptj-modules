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

import com.adeptj.modules.jaxrs.core.auth.JaxRSAuthenticationInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Map based identity store which stores {@link JaxRSAuthenticationInfo} created vis OSGi configs.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
enum SimpleIdentityStore {

    INSTANCE;

    private Map<String, JaxRSAuthenticationInfo> authInfoMap = new ConcurrentHashMap<>();

    void put(String username, JaxRSAuthenticationInfo authInfo) {
        if (this.authInfoMap.containsKey(username)) {
            throw new IllegalStateException(String.format("Username: [%s] already present!!", username));
        }
        this.authInfoMap.put(username, authInfo);
    }

    JaxRSAuthenticationInfo get(String username) {
        return this.authInfoMap.get(username);
    }

    void remove(String username) {
        this.authInfoMap.remove(username);
    }

}
