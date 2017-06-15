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
package com.adeptj.modules.jaxrs.base;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * JaxRSAuthConfigOCD.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@ObjectClassDefinition(name = "AdeptJ REST API Auth Config", description = "AdeptJ REST API Auth Config")
public @interface JaxRSAuthConfigOCD {

    @AttributeDefinition(name = "subject", description = "Subject for JWT issuance")
    String subject();

    @AttributeDefinition(name = "password", description = "Password of the Subject for JWT issuance", type = AttributeType.PASSWORD)
    String password();

    @AttributeDefinition(name = "signingKey", description = "SigningKey for JWT/S signing")
    String signingKey();

    @AttributeDefinition(name = "origins", description = "Origin(s) of the caller", cardinality = Integer.MAX_VALUE)
    String origins();

    @AttributeDefinition(name = "userAgents", description = "User Agents", cardinality = Integer.MAX_VALUE)
    String userAgents();
}
