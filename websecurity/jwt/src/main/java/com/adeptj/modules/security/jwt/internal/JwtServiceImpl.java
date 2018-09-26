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
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

    private String issuer;

    private boolean suppressJwtExceptionTrace;

    private List<String> obligatoryClaims;

    private Duration duration;

    private SignatureAlgorithm signatureAlgo;

    private PrivateKey signingKey;

    private PublicKey verificationKey;

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
                .setIssuer(this.issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(this.duration)))
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
                    .setSigningKey(this.verificationKey)
                    .parse(jwt, this.jwtHandler);
        } catch (RuntimeException ex) { // NOSONAR
            // For reducing noise in the logs, set this config to true.
            if (this.suppressJwtExceptionTrace) {
                LOGGER.error(ex.getMessage());
            } else {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
        return new ExtendedJwtClaims();
    }

    // ------------------------------------------------- OSGi INTERNAL -------------------------------------------------

    @Modified
    @Activate
    protected void start(JwtConfig jwtConfig) {
        this.signatureAlgo = null;
        this.signingKey = null;
        this.verificationKey = null;
        this.duration = null;
        this.issuer = null;
        this.suppressJwtExceptionTrace = false;
        try {
            this.issuer = jwtConfig.issuer();
            this.duration = Duration.of(jwtConfig.expirationTime(), ChronoUnit.valueOf(jwtConfig.expirationTimeUnit()));
            this.suppressJwtExceptionTrace = jwtConfig.suppressJwtExceptionTrace();
            this.signatureAlgo = SignatureAlgorithm.forName(jwtConfig.signatureAlgo());
            LOGGER.info("JWT SignatureAlgorithm: [{}]", this.signatureAlgo.getJcaName());
            this.signingKey = JwtSigningKeys.createRsaSigningKey(jwtConfig);
            this.verificationKey = JwtSigningKeys.createRsaVerificationKey(jwtConfig);
            this.obligatoryClaims = Arrays.asList(jwtConfig.obligatoryClaims());
            this.jwtHandler.setInvokeClaimsValidator(jwtConfig.invokeClaimsValidator());
        } catch (SignatureException | KeyInitializationException | IllegalArgumentException ex) {
            LOGGER.error("Couldn't start the JwtService!!", ex);
            throw ex;
        }
    }
}
