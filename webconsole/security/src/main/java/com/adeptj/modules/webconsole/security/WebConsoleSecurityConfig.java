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

package com.adeptj.modules.webconsole.security;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Configuration will be used by WebConsoleSecurityProvider for auth purpose.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ObjectClassDefinition(
        name = "AdeptJ OSGi WebConsole Security Roles Configuration",
        description = "Roles configured will be used by WebConsoleSecurityProvider for auth purpose."
)
public @interface WebConsoleSecurityConfig {

    int CARDINALITY = 100;

    String ROLE_OSGI_ADMIN = "OSGiAdmin";

    String TOOLS_LOGOUT_URI = "/tools/logout";

    @AttributeDefinition(
            name = "WebConsole Security Roles",
            description = "Security roles required by WebConsoleSecurityProvider for auth purpose.",
            cardinality = CARDINALITY
    )
    String[] roles() default {ROLE_OSGI_ADMIN,};

    @AttributeDefinition(
            name = "Logout URI",
            description = "URI where user will be redirected after logout."
    )
    String logoutURI() default TOOLS_LOGOUT_URI;
}
