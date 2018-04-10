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

import com.adeptj.modules.security.jwt.JwtConfig;
import com.adeptj.modules.security.jwt.JwtService;
import com.adeptj.modules.security.jwt.validation.JwtClaimsValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static io.jsonwebtoken.Header.JWT_TYPE;
import static io.jsonwebtoken.Header.TYPE;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * Service for signing and parsing JWT
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = JwtConfig.class)
@Component(configurationPolicy = REQUIRE)
public class JwtServiceImpl implements JwtService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtServiceImpl.class);

    private static final String BIND_CLAIMS_VALIDATOR_SERVICE = "bindClaimsValidator";

    private static final String UNBIND_CLAIMS_VALIDATOR_SERVICE = "unbindClaimsValidator";

    private JwtConfig jwtConfig;

    private SignatureAlgorithm signatureAlgo;

    private volatile Key signingKey;

    // As per Felix SCR, dynamic references should be declared as volatile.
    @Reference(
            cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC,
            bind = BIND_CLAIMS_VALIDATOR_SERVICE,
            unbind = UNBIND_CLAIMS_VALIDATOR_SERVICE
    )
    private volatile JwtClaimsValidator claimsValidator;

    /**
     * {@inheritDoc}
     */
    @Override
    public String createJwt(String subject, Map<String, Object> claims) {
        Validate.isTrue(StringUtils.isNotEmpty(subject), "Subject can't be blank!!");
        Instant now = Instant.now();
        return Jwts.builder()
                .setHeaderParam(TYPE, JWT_TYPE)
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(this.jwtConfig.issuer())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(this.jwtConfig.expirationTime(), MINUTES)))
                .setId(UUID.randomUUID().toString())
                .signWith(this.signatureAlgo, this.signingKey)
                .compact();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean verifyJwt(String jwt) {
        boolean verified = false;
        try {
            Validate.isTrue(StringUtils.isNotEmpty(jwt), "JWT can't be blank!!");
            Claims claims = Jwts.parser()
                    .requireIssuer(this.jwtConfig.issuer())
                    .setSigningKey(this.signingKey)
                    .parseClaimsJws(jwt)
                    .getBody();
            verified = !this.jwtConfig.validateClaims() || this.claimsValidator != null && this.claimsValidator.validate(claims);
        } catch (RuntimeException ex) { // NOSONAR
            // For reducing noise in the logs, set this config to false.
            if (this.jwtConfig.printJwtExceptionTrace()) {
                LOGGER.error(ex.getMessage(), ex);
            } else {
                LOGGER.error(ex.getMessage());
            }
        }
        return verified;
    }

    // ------------------ INTERNAL ------------------
    // Component Lifecycle Methods

    protected void bindClaimsValidator(JwtClaimsValidator claimsValidator) {
        this.claimsValidator = claimsValidator;
    }

    protected void unbindClaimsValidator(JwtClaimsValidator claimsValidator) { // NOSONAR
        if (Objects.equals(claimsValidator, this.claimsValidator)) {
            this.claimsValidator = null;
        }
    }

    @Activate
    protected void start(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.signatureAlgo = SignatureAlgorithm.forName(jwtConfig.signatureAlgo());
        this.signingKey = JwtSigningKeys.createSigningKey(jwtConfig);
    }

    @Modified
    protected void updated(JwtConfig jwtConfig) {
        LOGGER.info("In updated,recreating the signing Key!!");
        this.jwtConfig = null;
        this.signatureAlgo = null;
        this.signingKey = null;
        this.start(jwtConfig);
    }
}
