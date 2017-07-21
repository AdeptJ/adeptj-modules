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

package com.adeptj.modules.jaxrs.core.api;

import com.adeptj.modules.jaxrs.core.JaxRSAuthenticationInfo;

/**
 * Authentication realm to be implemented by clients for providing JaxRSAuthenticationInfo.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public interface JaxRSAuthenticationRealm {

    /**
     * Provides a meaningful name to this AuthenticationRepository.
     *
     * @return a meaningful name to this AuthenticationRepository.
     */
    String getName();

    /**
     * Implementations should validate the credentials supplied and return the populated JaxRSAuthenticationInfo
     * with other useful information.
     *
     * Note: Just the presence of non null JaxRSAuthenticationInfo will be treated a valid auth info by
     * {@link com.adeptj.modules.jaxrs.core.JaxRSAuthenticator} as it has no way to validate the information
     * returned by the implementations.
     *
     * @param subject the user id
     * @param password the password of subject supplied
     *
     * @return JaxRSAuthenticationInfo with credentials validated by the implementations.
     */
    JaxRSAuthenticationInfo getAuthenticationInfo(String subject, String password);
}
