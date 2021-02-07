package com.adeptj.modules.security.jwt.internal;

import com.adeptj.modules.security.jwt.JwtClaims;
import com.adeptj.modules.security.jwt.JwtVerificationService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Deserializer;
import io.jsonwebtoken.io.Serializer;
import io.jsonwebtoken.lang.Assert;
import io.jsonwebtoken.security.SignatureException;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Duration;
import java.util.Map;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * Service for parsing JWT with RSA public key.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = JwtConfig.class)
@Component(configurationPolicy = REQUIRE)
public class JwtVerificationServiceImpl implements JwtVerificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final boolean logJwtVerificationExceptionTrace;

    private final String defaultIssuer;

    private final String[] mandatoryClaims;

    private final Duration expirationDuration;

    private final JwtKeyInfo keyInfo;

    private final ClaimsConsumer claimsConsumer;

    private final Serializer<Map<String, ?>> serializer;

    private final Deserializer<Map<String, ?>> deserializer;

    @Activate
    public JwtVerificationServiceImpl(@NotNull JwtConfig config) {
        this.claimsConsumer = new ClaimsConsumer();
        this.serializer = new JwtSerializer();
        this.deserializer = new JwtDeserializer();
        this.logJwtVerificationExceptionTrace = config.logJwtVerificationExceptionTrace();
        this.defaultIssuer = config.defaultIssuer();
        this.expirationDuration = Duration.of(config.expirationTime(), MINUTES);
        this.mandatoryClaims = config.mandatoryClaims();
        try {
            SignatureAlgorithm algorithm = SignatureAlgorithm.forName(config.signatureAlgorithm());
            LOGGER.info("Selected JWT SignatureAlgorithm: [{}]", algorithm.getJcaName());
            PrivateKey signingKey = JwtKeys.createSigningKey(config, algorithm.getFamilyName());
            PublicKey verificationKey = JwtKeys.createVerificationKey(config, algorithm.getFamilyName());
            this.keyInfo = new JwtKeyInfo(algorithm, signingKey, verificationKey);
        } catch (SignatureException | JwtKeyInitializationException | IllegalArgumentException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public JwtClaims verifyJwt(String jwt) {
        JwtClaims claims = null;
        try {
            Assert.hasText(jwt, "JWT can't be blank!!");
            claims = Jwts.parserBuilder()
                    .setSigningKey(this.keyInfo.getPublicKey())
                    .deserializeJsonWith(this.deserializer)
                    .build()
                    .parse(jwt, this.claimsConsumer);
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
