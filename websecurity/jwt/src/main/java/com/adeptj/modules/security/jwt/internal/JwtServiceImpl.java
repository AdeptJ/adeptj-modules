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
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.lang.Assert;
import org.apache.commons.lang3.ArrayUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivateKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtService.class);

    private static final String UTF8 = "UTF-8";

    private static final String BIND_CLAIMS_VALIDATOR_SERVICE = "bindClaimsValidator";

    private static final String UNBIND_CLAIMS_VALIDATOR_SERVICE = "unbindClaimsValidator";

    private JwtConfig jwtConfig;

    private SignatureAlgorithm signatureAlgo;

    // For Hmac algorithms
    private byte[] hmacSecretKey;

    // For Rsa algorithms
    private PrivateKey rsaPrivateKey;

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
    public String issueJwt(String subject, Map<String, Object> claims) {
        Assert.hasText(subject, "Subject can't be null or empty!!");
        Instant now = Instant.now();
        JwtBuilder jwtBuilder = Jwts.builder()
                .setHeaderParam(TYPE, JWT_TYPE)
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(this.jwtConfig.issuer())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(this.jwtConfig.expirationTime(), MINUTES)))
                .setId(UUID.randomUUID().toString());
        this.signWith(jwtBuilder);
        return jwtBuilder.compact();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean verifyJwt(String jwt) {
        boolean verified = false;
        try {
            Assert.hasText(jwt, "JWT can't be null or empty!!");
            JwtParser jwtParser = Jwts.parser().requireIssuer(this.jwtConfig.issuer());
            this.setSigningKey(jwtParser);
            Jws<Claims> claimsJws = jwtParser.parseClaimsJws(jwt);
            verified = !this.jwtConfig.validateClaims() ||
                    this.claimsValidator != null && this.claimsValidator.validate(claimsJws.getBody());
        } catch (RuntimeException ex) {
            // For reducing noise in the logs, set this config to false.
            if (this.jwtConfig.printJwtExceptionTrace()) {
                LOGGER.error(ex.getMessage(), ex);
            } else {
                LOGGER.error(ex.getMessage());
            }
        }
        return verified;
    }


    private void signWith(JwtBuilder jwtBuilder) {
        if (this.rsaPrivateKey == null) {
            jwtBuilder.signWith(this.signatureAlgo, this.hmacSecretKey);
        } else {
            jwtBuilder.signWith(this.signatureAlgo, this.rsaPrivateKey);
        }
    }

    private void setSigningKey(JwtParser jwtParser) {
        if (this.rsaPrivateKey == null) {
            jwtParser.setSigningKey(this.hmacSecretKey);
        } else {
            jwtParser.setSigningKey(this.rsaPrivateKey);
        }
    }

    private void handleSigningKey(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        try {
            this.signatureAlgo = SignatureAlgorithm.forName(jwtConfig.signatureAlgo());
            this.hmacSecretKey = JwtSigningKeys.getHmacSecretKey(this.signatureAlgo, jwtConfig.hmacSecretKey());
            if (ArrayUtils.isEmpty(this.hmacSecretKey)) {
                this.rsaPrivateKey = JwtSigningKeys.getRsaPrivateKey(this.jwtConfig);
            }
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
            // Let the exception be rethrown so that SCR would not create a service object of this component.
            throw new RuntimeException(ex); // NOSONAR
        }
    }

    // Component Lifecycle Methods

    protected void bindClaimsValidator(JwtClaimsValidator claimsValidator) {
        this.claimsValidator = claimsValidator;
    }

    protected void unbindClaimsValidator(JwtClaimsValidator claimsValidator) { // NOSONAR
        this.claimsValidator = null;
    }

    @Activate
    protected void start(JwtConfig jwtConfig) {
        this.handleSigningKey(jwtConfig);
    }

    @Modified
    protected void updated(JwtConfig jwtConfig) {
        this.handleSigningKey(jwtConfig);
    }
}
