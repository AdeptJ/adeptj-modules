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
import com.adeptj.modules.jaxrs.core.auth.api.JaxRSAuthenticationRealm;
import org.osgi.service.component.annotations.Component;

/**
 * Default implementation of JaxRSAuthenticationRealm for creating {@link JaxRSAuthenticationInfo} instances
 * which are stored by {@link JaxRSAuthenticationInfoHolder}
 * for querying purpose in case no other implementation of {@link JaxRSAuthenticationRealm} is found.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(immediate = true)
public class DefaultJaxRSAuthenticationRealm implements JaxRSAuthenticationRealm {

    /**
     * {@inheritDoc}
     */
    @Override
    public int priority() {
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.getClass().getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JaxRSAuthenticationInfo getAuthenticationInfo(String username, String password) {
        return JaxRSAuthUtil.validateCredentials(username, password);
    }
}
