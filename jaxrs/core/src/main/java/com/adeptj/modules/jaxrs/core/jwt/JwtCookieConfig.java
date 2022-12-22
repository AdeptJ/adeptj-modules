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

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import static jakarta.ws.rs.core.NewCookie.DEFAULT_MAX_AGE;

/**
 * Configuration for jwt cookie
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ObjectClassDefinition(
        name = "AdeptJ JWT Cookie Configuration",
        description = "Configuration for JWT cookie"
)
public @interface JwtCookieConfig {

    @AttributeDefinition(
            name = "JWT Cookie Name",
            description = "Name of the JWT cookie to be sent to client"
    )
    String name() default "jwt";

    @AttributeDefinition(
            name = "JWT Cookie Path",
            description = "Path of JWT cookie"
    )
    String path() default "/";

    @AttributeDefinition(
            name = "JWT Cookie Domain",
            description = "Domain of JWT cookie"
    )
    String domain();

    @AttributeDefinition(
            name = "JWT Cookie Comment",
            description = "Cookie comment, if any"
    )
    String comment();

    @AttributeDefinition(
            name = "JWT Cookie Max-Age",
            description = "Max-Age of JWT cookie in seconds, by default it's a session cookie"
    )
    int max_age() default DEFAULT_MAX_AGE;

    @AttributeDefinition(
            name = "JWT Cookie Http-Only",
            description = "Whether to make JWT cookie as Http-Only"
    )
    boolean http_only() default true;

    @AttributeDefinition(
            name = "JWT Cookie Secure",
            description = "Whether to make JWT cookie secure i.e. make it available only over Https"
    )
    boolean secure();

    @AttributeDefinition(
            name = "JWT Cookie Configuration Enabled",
            description = "Whether JWT cookie enabled"
    )
    boolean enabled();
}
