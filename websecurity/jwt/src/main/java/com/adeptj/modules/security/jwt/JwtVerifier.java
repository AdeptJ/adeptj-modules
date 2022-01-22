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
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Assert;
import org.osgi.annotation.versioning.ConsumerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.security.Key;

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

    public JwtVerifier(Key verificationKey, boolean logJwtVerificationExceptionTrace) {
        this.logJwtVerificationExceptionTrace = logJwtVerificationExceptionTrace;
        this.verificationKey = verificationKey;
    }

    public JwtClaims verify(String jwt) {
        JwtClaims claims = null;
        try {
            Assert.hasText(jwt, "JWT can't be blank!!");
            claims = Jwts.parserBuilder()
                    .setSigningKey(this.verificationKey)
                    .deserializeJsonWith(new JwtDeserializer())
                    .build()
                    .parse(jwt, new ClaimsConsumer());
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
