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
package com.adeptj.modules.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Deserializer;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.lang.Assert;
import org.osgi.annotation.versioning.ConsumerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.lang.invoke.MethodHandles;
import java.security.Key;
import java.security.PublicKey;
import java.util.Map;

/**
 * A common class for verifying the Jwt using the key supplied to the constructor.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ConsumerType
public class JwtVerifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final boolean logJwtVerificationExceptionTrace;

    private final Key verificationKey;

    private final Deserializer<Map<String, ?>> deserializer;

    public JwtVerifier(Key verificationKey, boolean logJwtVerificationExceptionTrace) {
        this.verificationKey = verificationKey;
        this.logJwtVerificationExceptionTrace = logJwtVerificationExceptionTrace;
        this.deserializer = new JacksonDeserializer<>();
    }

    public JwtClaims verify(String jwt) {
        JwtClaims claims = null;
        try {
            Assert.hasText(jwt, "JWT can't be blank!!");
            JwtParserBuilder builder = Jwts.parser();
            if (this.verificationKey instanceof SecretKey secretKey) {
                builder.verifyWith(secretKey);
            } else if (this.verificationKey instanceof PublicKey publicKey) {
                builder.verifyWith(publicKey);
            } else {
                throw new IllegalStateException("Unknown verification key!!");
            }
            claims = builder.json(this.deserializer)
                    .build()
                    .parse(jwt)
                    .accept(new ClaimsConsumer());
        } catch (ExpiredJwtException ex) {
            if (this.logJwtVerificationExceptionTrace) {
                LOGGER.error(ex.getMessage(), ex);
            }
            claims = new JwtClaims(ex.getClaims());
            claims.setExpired(true);
        } catch (JwtException | IllegalArgumentException ex) {
            if (this.logJwtVerificationExceptionTrace) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
        return claims;
    }
}
