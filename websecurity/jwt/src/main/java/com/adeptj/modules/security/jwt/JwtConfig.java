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

    long DEFAULT_EXPIRATION_TIME = 720L; // 1 day

    @AttributeDefinition(
            name = "JWT Hmac Secret Key",
            description = "Hmac Secret Key for JWT, must be left blank in case RSA private key is used. " +
                    "Please note that this key will be Base64 encoded when config is saved."
    )
    String hmacSecretKey();

    @AttributeDefinition(
            name = "RSA PrivateKey File Location",
            description = "Location of PrivateKey file (PEM-encoded PKCS#8 format)  for JWT signing, " +
                    "relative to current working directory, <br>" +
                    "Note: Please don't use the default Key file in production environment!")
    String keyFileLocation() default "adeptj-runtime/deployment/jwt-signing-pkcs8.key";

    @AttributeDefinition(
            name = "RSA PrivateKey Password",
            description = "Pass phrase of the RSA PrivateKey")
    String keyPassword();

    @AttributeDefinition(name = "Password Protected Key", description = "Whether the PrivateKey is password protected.")
    boolean pwdProtectedKey();

    @AttributeDefinition(
            name = "JWT Signature Algorithm",
            description = "Signature Algorithm for JWT signing, only RSA and Hmac with SHA are supported at this moment.",
            options = {
                    @Option(label = "RSA 256", value = "RS256"),
                    @Option(label = "RSA 384", value = "RS384"),
                    @Option(label = "RSA 512", value = "RS512"),
                    @Option(label = "HMAC SHA256", value = "HS256"),
                    @Option(label = "HMAC SHA384", value = "HS384"),
                    @Option(label = "HMAC SHA512", value = "HS512"),
            }
    )
    String signatureAlgo();

    @AttributeDefinition(name = "JWT Issuer", description = "Issuer of JWT")
    String issuer() default "AdeptJ Runtime";

    @AttributeDefinition(name = "JWT Expiration Time", description = "JWT Expiration Time in minutes.")
    long expirationTime() default DEFAULT_EXPIRATION_TIME;

    @AttributeDefinition(name = "Use Default Signing Key", description = "Whether to use Default Signing Key.")
    boolean useDefaultKey() default true;

    @AttributeDefinition(
            name = "Validate JWT Claims",
            description = "Whether to validate the JWT claims further via a JwtClaimsValidator after successful parsing."
    )
    boolean validateClaims();

    @AttributeDefinition(name = "Print JwtException Trace", description = "Whether to print JwtException Trace.")
    boolean printJwtExceptionTrace() default true;
}
