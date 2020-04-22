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

import static org.osgi.service.metatype.annotations.AttributeType.PASSWORD;

/**
 * Configuration will be used by WebConsoleSecurityProvider for auth purpose.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ObjectClassDefinition(
        name = "AdeptJ OSGi WebConsole Security Configuration",
        description = "Roles, Admin password will be configured by this service."
)
public @interface WebConsoleSecurityConfig {

    int CARDINALITY = 100;

    String ROLE_OSGI_ADMIN = "OSGiAdmin";

    String DEFAULT_LOGOUT_URI = "/admin/logout";

    String DEFAULT_CREDENTIALS_STORE = "credentials.dat";

    String DEFAULT_ADMIN_CREDENTIALS_H2_MAP_NAME = "adminCredentials";

    @AttributeDefinition(
            name = "WebConsole Security Roles",
            description = "Security roles required by WebConsoleSecurityProvider for auth purpose.",
            cardinality = CARDINALITY
    )
    String[] roles() default {ROLE_OSGI_ADMIN,};

    @AttributeDefinition(
            name = "WebConsole Post Logout URI",
            description = "URI where user will be redirected after logout."
    )
    String logout_uri() default DEFAULT_LOGOUT_URI;

    @AttributeDefinition(
            name = "WebConsole Admin Credentials Store Name",
            description = "Credentials store name for AdeptJ OSGi WebConsole admin."
    )
    String credentials_store_name() default DEFAULT_CREDENTIALS_STORE;

    @AttributeDefinition(
            name = "WebConsole Admin Credentials Map Name",
            description = "Admin credentials map name in the AdeptJ OSGi WebConsole Password Store."
    )
    String credentials_map_name() default DEFAULT_ADMIN_CREDENTIALS_H2_MAP_NAME;

    @AttributeDefinition(
            name = "WebConsole Admin Password",
            description = "Admin Password for AdeptJ OSGi WebConsole Admin.",
            type = PASSWORD
    )
    String admin_password();
}
