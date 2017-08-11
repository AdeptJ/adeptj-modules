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

package com.adeptj.modules.jaxrs.core.internal;


import com.adeptj.modules.commons.utils.Loggers;
import com.adeptj.modules.jaxrs.core.JaxRSAuthenticationInfo;
import com.adeptj.modules.jaxrs.core.JaxRSAuthenticator;
import com.adeptj.modules.jaxrs.core.api.JaxRSAuthenticationRealm;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Provides {@link JaxRSAuthenticationInfo} by querying all the registered {@link JaxRSAuthenticationRealm}
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(immediate = true)
public class DefaultJaxRSAuthenticator implements JaxRSAuthenticator {

    // As per Felix SCR, dynamic references should be declared as volatile.
    @Reference(
            service = JaxRSAuthenticationRealm.class,
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC
    )
    private volatile List<JaxRSAuthenticationRealm> realms = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    public JaxRSAuthenticationInfo handleSecurity(String username, String password) {
        return this.realms.stream()
                .sorted((realmOne, realmTwo) -> Integer.compare(realmTwo.priority(), realmOne.priority()))
                .map(realm -> this.getAuthInfo(realm, username, password))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private JaxRSAuthenticationInfo getAuthInfo(JaxRSAuthenticationRealm realm, String username, String password) {
        JaxRSAuthenticationInfo authInfo = null;
        try {
            authInfo = realm.getAuthenticationInfo(username, password);
        } catch (Exception ex) {
            // Gulping everything so that next realm as well get a chance.
            Loggers.get(getClass()).error(ex.getMessage(), ex);
        }
        return authInfo;
    }

    protected void addRealm(JaxRSAuthenticationRealm realm) {
        this.realms.add(realm);
    }
}
