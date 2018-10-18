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
@ObjectClassDefinition(name = "AdeptJ JWT Configuration", description = "Configs for JWT Service")
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
    String signatureAlgo();

    @AttributeDefinition(
            name = "RSA PrivateKey(Signing Key)",
            description = "PrivateKey data (PEM-encoded PKCS#8 format) for JWT signing."
    )
    String privateKey();

    @AttributeDefinition(
            name = "RSA PrivateKey Password",
            description = "Pass phrase of the RSA PrivateKey, leave unaltered if not a password protected key.",
            type = PASSWORD
    )
    String privateKeyPassword();

    @AttributeDefinition(
            name = "RSA PublicKey(Verification Key)",
            description = "PublicKey data (PEM-encoded X.509 format) for JWT verification."
    )
    String publicKey();

    @AttributeDefinition(name = "JWT Issuer", description = "Issuer of JWT")
    String issuer() default "AdeptJ Runtime";

    @AttributeDefinition(name = "JWT Expiration Time", description = "JWT Expiration Time in minutes.")
    long expirationTime() default DEFAULT_EXPIRATION_TIME;

    @AttributeDefinition(
            name = "Mandatory JWT Claims",
            description = "Mandatory JWT claims which need to be checked for null and emptiness."
    )
    String[] mandatoryClaims() default {
            SUBJECT,
            ISSUER,
            ID,
            ISSUED_AT,
            EXPIRATION
    };

    @AttributeDefinition(
            name = "Suppress Jwt Verification Exception Trace",
            description = "Whether to suppress Jwt verification exception trace for reducing noise in logs."
    )
    boolean suppressVerificationException();
}
