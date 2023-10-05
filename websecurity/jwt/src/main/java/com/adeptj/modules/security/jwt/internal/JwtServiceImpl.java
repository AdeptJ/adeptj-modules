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

import com.adeptj.modules.commons.utils.RandomGenerators;
import com.adeptj.modules.security.jwt.JwtClaims;
import com.adeptj.modules.security.jwt.JwtKeyInitializationException;
import com.adeptj.modules.security.jwt.JwtKeys;
import com.adeptj.modules.security.jwt.JwtService;
import com.adeptj.modules.security.jwt.JwtUtil;
import com.adeptj.modules.security.jwt.JwtVerifier;
import com.adeptj.modules.security.jwt.RsaSigningKeyInfo;
import com.adeptj.modules.security.jwt.RsaVerificationKeyInfo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Serializer;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.lang.Assert;
import io.jsonwebtoken.security.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.security.Key;
import java.security.PrivateKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static io.jsonwebtoken.Claims.ID;
import static io.jsonwebtoken.Claims.ISSUER;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * Service for signing and parsing JWT with RSA private and public keys respectively.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = JwtConfig.class)
@Component(service = JwtService.class, configurationPolicy = REQUIRE)
public class JwtServiceImpl implements JwtService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String JWT_TYPE = "JWT";

    private static final String TYPE = "typ";

    private final String defaultIssuer;

    private final String[] mandatoryClaims;

    private final Duration expirationDuration;

    private final SignatureAlgorithm algorithm;

    private final PrivateKey signingKey;

    private final JwtVerifier jwtVerifier;

    private final Serializer<Map<String, ?>> serializer;

    @Activate
    public JwtServiceImpl(@NotNull JwtConfig config) {
        this.defaultIssuer = config.default_issuer();
        this.expirationDuration = Duration.of(config.expiration_time(), MINUTES);
        this.mandatoryClaims = config.mandatory_claims();
        try {
            this.algorithm = JwtKeys.getSignatureAlgorithm(config.signature_algorithm());
            RsaSigningKeyInfo signingKeyInfo = new RsaSigningKeyInfo(this.algorithm, config.private_key());
            signingKeyInfo.setPrivateKeyPassword(config.private_key_password());
            this.signingKey = JwtKeys.createSigningKey(signingKeyInfo);
            Key verificationKey = JwtKeys.createVerificationKey(new RsaVerificationKeyInfo(this.algorithm, config.public_key()));
            this.jwtVerifier = new JwtVerifier(verificationKey, config.log_jwt_verification_exception_trace());
            this.serializer = new JacksonSerializer<>();
        } catch (SignatureException | JwtKeyInitializationException | IllegalArgumentException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createJwt(String subject, Map<String, Object> claims) {
        Assert.hasText(subject, "Subject can't be blank!!");
        JwtUtil.assertClaims(claims);
        Instant now = Instant.now();
        return Jwts.builder()
                .header()
                .add(TYPE, JWT_TYPE)
                .and()
                .subject(subject)
                .claims(claims) // passed claims map can override the subject claim.
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(this.expirationDuration)))
                .id(claims.containsKey(ID) ? claims.get(ID).toString() : RandomGenerators.uuidString())
                .issuer(claims.containsKey(ISSUER) ? (String) claims.get(ISSUER) : this.defaultIssuer)
                .signWith(this.signingKey, this.algorithm)
                .json(this.serializer)
                .compact();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createJwt(Map<String, Object> claims) {
        JwtUtil.assertClaims(claims, this.mandatoryClaims);
        return Jwts.builder()
                .header()
                .add(TYPE, JWT_TYPE)
                .and()
                .claims(claims)
                .signWith(this.signingKey, this.algorithm)
                .json(this.serializer)
                .compact();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JwtClaims verifyJwt(String jwt) {
        return this.jwtVerifier.verify(jwt);
    }
}
