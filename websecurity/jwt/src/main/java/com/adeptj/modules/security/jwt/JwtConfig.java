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

package com.adeptj.modules.security.jwt;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

/**
 * JWT Configurations.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ObjectClassDefinition(name = "AdeptJ JWT Configuration", description = "Configs for JWT Service")
public @interface JwtConfig {

    @AttributeDefinition(name = "JWT Signing Key", description = "Signing Key for JWT")
    String signingKey();

    @AttributeDefinition(name = "JWT Signature Algorithm", description = "Signature Algorithm for JWT", options = {
            @Option(label = "HS256", value = "HS256"),
            @Option(label = "HS384", value = "HS384"),
            @Option(label = "HS512", value = "HS512"),
            @Option(label = "RS256", value = "RS256"),
            @Option(label = "RS384", value = "RS384"),
            @Option(label = "RS512", value = "RS512"),
    })
    String signatureAlgo();

    @AttributeDefinition(name = "JWT Issuer", description = "Issuer of JWT")
    String issuer() default "AdeptJ Runtime JWT Issuer";

    @AttributeDefinition(name = "JWT Expiration Time", description = "JWT Expiration Time in minutes")
    long expirationTime() default 30L;
}
