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
package com.adeptj.modules.jaxrs.core.auth;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import static org.osgi.service.metatype.annotations.AttributeType.PASSWORD;

/**
 * AdeptJ JAX-RS Credentials Configurations.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@ObjectClassDefinition(
        name = "AdeptJ JAX-RS Credentials Configurations",
        description = "AdeptJ JAX-RS Credentials Configurations"
)
public @interface JaxRSCredentialsConfig {

    @AttributeDefinition(name = "Username", description = "Username to be authenticated.")
    String username();

    @AttributeDefinition(name = "Password", description = "Password of username provided.", type = PASSWORD)
    String password();
}
