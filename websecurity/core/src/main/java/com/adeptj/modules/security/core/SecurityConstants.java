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

package com.adeptj.modules.security.core;

/**
 * SecurityConstants.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class SecurityConstants {

    private SecurityConstants() {
    }

    public static final String SERVLET_CONTEXT_NAME = "AdeptJ SecurityHandler";

    public static final String METHOD_POST = "POST";

    public static final String PARAM_USERNAME = "j_username";

    public static final String PARAM_PWD = "j_password";

    public static final String LOGIN_URI_SUFFIX = "j_security_check_osgi";

    public static final String AUTH_SCHEME_BEARER = "Bearer";

    public static final String AUTH_SCHEME_BASIC = "Basic";

    public static final String HEADER_AUTHORIZATION = "Authorization";

    public static final String ATTRIBUTE_TOKEN_CREDENTIAL = ".TOKEN_CREDENTIAL";

    public static final String KEY_REQUEST_ID = "REQ_ID";
}
