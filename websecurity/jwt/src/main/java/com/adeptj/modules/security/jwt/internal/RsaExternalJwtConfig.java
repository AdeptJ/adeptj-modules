/*
###############################################################################
#                                                                             #
#    Copyright 2016-2022, AdeptJ (http://www.adeptj.com)                      #
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
package com.adeptj.modules.security.jwt.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

/**
 * OCD for Rsa external Jwt verification service.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ObjectClassDefinition(
        name = "AdeptJ Rsa External JWT Service Configuration",
        description = "Configs for AdeptJ Rsa External JWT Service Configuration"
)
public @interface RsaExternalJwtConfig {

    @AttributeDefinition(
            name = "JWT Signature Verification Algorithm",
            description = "RSA Signature algorithm for external JWT verification.",
            options = {
                    @Option(label = "RSA 256", value = "RS256"),
                    @Option(label = "RSA 384", value = "RS384"),
                    @Option(label = "RSA 512", value = "RS512"),
            }
    )
    String signature_algorithm();

    @AttributeDefinition(
            name = "Jwt PublicKey(Verification Key)",
            description = "PublicKey data (PEM-encoded) for JWT verification."
    )
    String public_key();

    @AttributeDefinition(
            name = "Log Jwt Verification Exception Trace",
            description = "Whether to log the Jwt verification exception trace in server logs."
    )
    boolean log_jwt_verification_exception_trace();
}
