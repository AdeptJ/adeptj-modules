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

import com.adeptj.modules.security.jwt.ExtendedJwtClaims;
import com.adeptj.modules.security.jwt.JwtConfig;
import com.adeptj.modules.security.jwt.JwtService;
import com.adeptj.modules.security.jwt.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.lang.Assert;
import io.jsonwebtoken.security.SignatureException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.jsonwebtoken.Header.JWT_TYPE;
import static io.jsonwebtoken.Header.TYPE;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * Service for signing and parsing JWT
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = JwtConfig.class)
@Component(configurationPolicy = REQUIRE)
public class JwtServiceImpl implements JwtService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private List<String> obligatoryClaims;

    private TemporalUnit expirationTimeUnit;

    private JwtConfig jwtConfig;

    private SignatureAlgorithm signatureAlgo;

    private Key signingKey;

    private Key verificationKey;

    @Reference
    private ClaimsJwsHandler jwtHandler;

    /**
     * {@inheritDoc}
     */
    @Override
    public String createJwt(String subject, Map<String, Object> claims) {
        Assert.hasText(subject, "Subject can't be blank!!");
        Instant now = Instant.now();
        return Jwts.builder()
                .setHeaderParam(TYPE, JWT_TYPE)
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(this.jwtConfig.issuer())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(this.jwtConfig.expirationTime(), this.expirationTimeUnit)))
                .setId(UUID.randomUUID().toString())
                .signWith(this.signingKey, this.signatureAlgo)
                .compact();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createJwt(Map<String, Object> claims) {
        JwtUtil.assertClaims(claims, this.obligatoryClaims);
        return Jwts.builder()
                .setHeaderParam(TYPE, JWT_TYPE)
                .setClaims(claims)
                .signWith(this.signingKey, this.signatureAlgo)
                .compact();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtendedJwtClaims verifyJwt(String jwt) {
        try {
            Assert.hasText(jwt, "JWT can't be blank!!");
            return Jwts.parser()
                    .requireIssuer(this.jwtConfig.issuer())
                    .setSigningKey(this.verificationKey)
                    .parse(jwt, this.jwtHandler);
        } catch (RuntimeException ex) { // NOSONAR
            // For reducing noise in the logs, set this config to false.
            if (this.jwtConfig.printJwtExceptionTrace()) {
                LOGGER.error(ex.getMessage(), ex);
            } else {
                LOGGER.error(ex.getMessage());
            }
        }
        return new ExtendedJwtClaims();
    }

    // ------------------------------------------------- OSGi INTERNAL -------------------------------------------------

    @Modified
    @Activate
    protected void start(JwtConfig jwtConfig) {
        this.jwtConfig = null;
        this.signatureAlgo = null;
        this.signingKey = null;
        this.verificationKey = null;
        this.jwtConfig = jwtConfig;
        try {
            this.signatureAlgo = SignatureAlgorithm.forName(jwtConfig.signatureAlgo());
            if (this.signatureAlgo.isHmac()) {
                this.signingKey = JwtSigningKeys.createHmacSigningKey(jwtConfig.hmacSecretKey(), this.signatureAlgo);
                // In case of HMAC, the signing and verification keys are same.
                this.verificationKey = this.signingKey;
            } else {
                this.signingKey = JwtSigningKeys.createRsaSigningKey(jwtConfig);
                this.verificationKey = JwtSigningKeys.createRsaVerificationKey(jwtConfig);
            }
            this.expirationTimeUnit = ChronoUnit.valueOf(this.jwtConfig.expirationTimeUnit());
            this.obligatoryClaims = Arrays.asList(this.jwtConfig.obligatoryClaims());
            this.jwtHandler.setInvokeClaimsValidator(this.jwtConfig.invokeClaimsValidator());
        } catch (SignatureException | KeyInitializationException | IllegalArgumentException ex) {
            LOGGER.info("Couldn't start the JwtService!!", ex);
            throw ex;
        }
    }
}
