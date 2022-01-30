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

package com.adeptj.modules.security.jwt.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

import static io.jsonwebtoken.Claims.EXPIRATION;
import static io.jsonwebtoken.Claims.ID;
import static io.jsonwebtoken.Claims.ISSUED_AT;
import static io.jsonwebtoken.Claims.ISSUER;
import static io.jsonwebtoken.Claims.SUBJECT;
import static org.osgi.service.metatype.annotations.AttributeType.PASSWORD;

/**
 * JWT Configurations.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ObjectClassDefinition(name = "AdeptJ JWT Service Configuration", description = "Configs for AdeptJ JWT Service")
public @interface JwtConfig {

    /**
     * Default Jwt expiration time is 12 Hours.
     */
    long DEFAULT_EXPIRATION_TIME = 720L;

    @AttributeDefinition(
            name = "JWT Signature Algorithm",
            description = "Signature Algorithm for JWT signing, only RSA is supported at this moment.",
            options = {
                    @Option(label = "RSA 256", value = "RS256"),
                    @Option(label = "RSA 384", value = "RS384"),
                    @Option(label = "RSA 512", value = "RS512"),
            }
    )
    String signature_algorithm();

    @AttributeDefinition(
            name = "Jwt PrivateKey(Signing Key)",
            description = "PrivateKey data (PEM-encoded PKCS#8 format) for JWT signing."
    )
    String private_key();

    @AttributeDefinition(
            name = "Jwt PrivateKey Password",
            description = "Pass phrase of the Jwt PrivateKey, leave unaltered if not a password protected key.",
            type = PASSWORD
    )
    String private_key_password();

    @AttributeDefinition(
            name = "Jwt PublicKey(Verification Key)",
            description = "PublicKey data (PEM-encoded) for JWT verification."
    )
    String public_key();

    @AttributeDefinition(name = "JWT Default Issuer", description = "Default Issuer of JWT")
    String default_issuer() default "AdeptJ Runtime";

    @AttributeDefinition(name = "JWT Expiration Time", description = "JWT Expiration Time in minutes.")
    long expiration_time() default DEFAULT_EXPIRATION_TIME;

    @AttributeDefinition(
            name = "Log Jwt Verification Exception Trace",
            description = "Whether to log the Jwt verification exception trace in server logs."
    )
    boolean log_jwt_verification_exception_trace();

    @AttributeDefinition(
            name = "Mandatory JWT Claims",
            description = "Mandatory JWT claims which need to be checked for null and emptiness."
    )
    String[] mandatory_claims() default {
            SUBJECT,
            ISSUER,
            ID,
            ISSUED_AT,
            EXPIRATION
    };
}
