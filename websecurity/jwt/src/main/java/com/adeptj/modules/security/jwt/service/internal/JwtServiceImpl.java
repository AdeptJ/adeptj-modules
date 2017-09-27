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

package com.adeptj.modules.security.jwt.service.internal;

import com.adeptj.modules.security.jwt.validation.JwtClaimsValidator;
import com.adeptj.modules.security.jwt.JwtConfig;
import com.adeptj.modules.security.jwt.service.api.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.lang.Assert;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivateKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static io.jsonwebtoken.Header.JWT_TYPE;
import static io.jsonwebtoken.Header.TYPE;
import static io.jsonwebtoken.SignatureAlgorithm.RS256;
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

    private static final String KEY_INIT_FAIL_MSG = "Couldn't initialize the SigningKey!!";

    private static final String BIND_CLAIMS_VALIDATOR_SERVICE = "bindClaimsValidator";

    private static final String UNBIND_CLAIMS_VALIDATOR_SERVICE = "unbindClaimsValidator";

    private JwtConfig jwtConfig;

    private String base64EncodedSigningKey;

    private SignatureAlgorithm signatureAlgo;

    private PrivateKey signingKey;

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
        JwtBuilder jwtBuilder = Jwts.builder()
                .setClaims(claims)
                .setHeaderParam(TYPE, JWT_TYPE)
                .setSubject(subject)
                .setIssuer(this.jwtConfig.issuer())
                .setIssuedAt(Date.from(Instant.now()))
                .setId(UUID.randomUUID().toString())
                .setExpiration(Date.from(LocalDateTime.now()
                        .plusMinutes(this.jwtConfig.expirationTime())
                        .atZone(ZoneId.systemDefault())
                        .toInstant()));
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
        if (this.signingKey == null) {
            jwtBuilder.signWith(this.signatureAlgo, this.base64EncodedSigningKey);
        } else {
            jwtBuilder.signWith(this.signatureAlgo, this.signingKey);
        }
    }

    private void setSigningKey(JwtParser parser) {
        if (this.signingKey == null) {
            parser.setSigningKey(this.base64EncodedSigningKey);
        } else {
            parser.setSigningKey(this.signingKey);
        }
    }

    // Component Lifecycle Methods

    protected void bindClaimsValidator(JwtClaimsValidator claimsValidator) {
        this.claimsValidator = claimsValidator;
    }

    protected void unbindClaimsValidator(JwtClaimsValidator claimsValidator ) { // NOSONAR
        this.claimsValidator = null;
    }

    @Activate
    protected void start(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        try {
            this.signatureAlgo = SignatureAlgorithm.forName(jwtConfig.signatureAlgo());
            String signKey = jwtConfig.signingKey();
            if (StringUtils.isNotEmpty(signKey) && this.signatureAlgo.isHmac()) {
                this.base64EncodedSigningKey = new String(Base64.getEncoder().encode(signKey.getBytes(UTF8)));
            } else if (StringUtils.isEmpty(signKey) && this.signatureAlgo.isHmac()) {
                this.signatureAlgo = RS256;
            } else {
                // This is to safeguard from situation where there was no signingKey provided
                // and HMAC is chosen from  signatureAlgo dropdown. Configuring RS256 as default.
                if (this.signatureAlgo.isHmac()) {
                    this.signatureAlgo = RS256;
                }
            }
            if (this.signatureAlgo.isRsa() && (this.signingKey = SigningKeys.getSigningKey(this.jwtConfig)) == null) {
                throw new IllegalStateException(KEY_INIT_FAIL_MSG);
            }
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
            // Let the exception be rethrown so that SCR would not create a service object of this component.
            throw new RuntimeException(ex); // NOSONAR
        }
    }
}
