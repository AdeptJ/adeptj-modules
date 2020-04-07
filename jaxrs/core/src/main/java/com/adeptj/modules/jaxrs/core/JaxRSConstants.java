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

package com.adeptj.modules.jaxrs.core;

/**
 * Constants for Jax-RS modules.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class JaxRSConstants {

    // Just declaring constants, no instances required.
    private JaxRSConstants() {
    }

    public static final String AUTH_SCHEME_BEARER = "Bearer";

    public static final String AUTH_SCHEME_TOKEN = "TOKEN";

    public static final String KEY_JWT_SUBJECT = "sub";

    public static final String ROLES = "roles";

    public static final String ROLES_DELIMITER = ",";

}
