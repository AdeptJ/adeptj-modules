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

/**
 * OCD for Hmac external Jwt verification service.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ObjectClassDefinition(
        name = "AdeptJ Hmac External JWT Service Configuration",
        description = "Configs for AdeptJ Hmac External JWT Service."
)
public @interface HmacExternalJwtConfig {

    @AttributeDefinition(
            name = "Jwt Hmac Key(Verification Key)",
            description = "Hmac key for JWT verification."
    )
    String hmac_key();

    @AttributeDefinition(
            name = "Log Jwt Verification Exception Trace",
            description = "Whether to log the Jwt verification exception trace in server logs."
    )
    boolean log_jwt_verification_exception_trace();
}
