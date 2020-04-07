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

package com.adeptj.modules.jaxrs.core.auth.api;

import com.adeptj.modules.jaxrs.core.auth.JaxRSAuthenticationOutcome;
import com.adeptj.modules.jaxrs.core.auth.SimpleCredentials;
import com.adeptj.modules.security.jwt.JwtService;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * Authentication realm to be implemented by clients for providing {@link JaxRSAuthenticationOutcome}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ConsumerType
public interface JaxRSAuthenticationRealm {

    /**
     * Priority of the realm, higher priority realm is called before lower priority realms.
     *
     * @return Priority of JaxRSAuthenticationRealm
     */
    int priority();

    /**
     * Provides a meaningful name which can be used by {@link JaxRSAuthenticator}.
     *
     * @return a meaningful name.
     */
    String getName();

    /**
     * Implementations should validate the credentials supplied and return the JaxRSAuthenticationOutcome
     * populated with other useful information that can be put into it as it is a map.
     * This information is later used by {@link JwtService} for
     * putting this information in JWT claims.
     * <p>
     * Note: Just the presence of non null JaxRSAuthenticationInfo will be treated a valid auth info by
     * {@link JaxRSAuthenticator} as it has no way to validate the information
     * returned by the implementations.
     *
     * @param credentials object containing the username and password submitted for authentication
     * @return JaxRSAuthenticationOutcome.
     */
    JaxRSAuthenticationOutcome authenticate(SimpleCredentials credentials);
}
