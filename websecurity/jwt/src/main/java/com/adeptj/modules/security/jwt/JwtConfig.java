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

    long DEFAULT_EXPIRATION_TIME = 720L; // 1 day

    @AttributeDefinition(
            name = "JWT Signature Algorithm",
            description = "Signature Algorithm for JWT signing, only RSA and HmacSHA* are supported at this moment.",
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

    @AttributeDefinition(
            name = "RSA PrivateKey File Location",
            description = "Location of PrivateKey file (PEM-encoded PKCS#8 format) on file system for JWT signing."
    )
    String keyFileLocation();

    @AttributeDefinition(
            name = "RSA PrivateKey Password",
            description = "Pass phrase of the RSA PrivateKey",
            type = PASSWORD
    )
    String keyPassword();

    @AttributeDefinition(
            name = "RSA PrivateKey Search User Home",
            description = "Whether to search the RSA PrivateKey in user home directory."
    )
    boolean searchKeyInUserHome() default true;

    @AttributeDefinition(
            name = "RSA PrivateKey Search Default Name",
            description = "Default name of the RSA PrivateKey to search in user home directory"
    )
    String defaultKeyName() default "jwt.pem";

    @AttributeDefinition(
            name = "JWT Hmac Secret Key",
            description = "Hmac Secret Key for JWT signing, leave it blank in case RSA algo is selected. "
    )
    String hmacSecretKey();

    @AttributeDefinition(name = "JWT Issuer", description = "Issuer of JWT")
    String issuer() default "AdeptJ Runtime";

    @AttributeDefinition(name = "JWT Expiration Time", description = "JWT Expiration Time as per expirationTimeUnit.")
    long expirationTime() default DEFAULT_EXPIRATION_TIME;

    @AttributeDefinition(
            name = "JWT Expiration Time Unit",
            description = "JWT Expiration Time Unit in Java ChronoUnit.",
            options = {
                    @Option(label = "Seconds", value = "SECONDS"),
                    @Option(label = "Minutes", value = "MINUTES"),
                    @Option(label = "Hours", value = "HOURS"),
                    @Option(label = "Days", value = "DAYS"),
            }
    )
    String expirationTimeUnit() default "MINUTES";

    @AttributeDefinition(
            name = "JWT default obligatory claims which need to be checked",
            description = "JWT default obligatory claims which need to be checked for null and emptiness."
    )
    String[] obligatoryClaims() default {SUBJECT, ISSUER, ID, ISSUED_AT, EXPIRATION};

    @AttributeDefinition(
            name = "Validate JWT Claims",
            description = "Whether to validate the JWT claims further via a JwtClaimsValidator after successful parsing."
    )
    boolean validateClaims();

    @AttributeDefinition(name = "Print JwtException Trace", description = "Whether to print JwtException Trace.")
    boolean printJwtExceptionTrace() default true;
}
