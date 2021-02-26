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

package com.adeptj.modules.jaxrs.api;

import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * Provides {@link JaxRSAuthenticationOutcome} after validating the credential from a user store such as DB.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ConsumerType
public interface JaxRSAuthenticator {

    /**
     * Implementations should validate the supplied credential and return the {@link JaxRSAuthenticationOutcome}
     * populated with useful information as a key value pair if needed.
     * This information is later used by JwtService for putting this information in JWT claims.
     *
     * @param credential object containing the username and password submitted for authentication
     * @return JaxRSAuthenticationOutcome.
     */
    JaxRSAuthenticationOutcome authenticate(@NotNull UsernamePasswordCredential credential);
}
