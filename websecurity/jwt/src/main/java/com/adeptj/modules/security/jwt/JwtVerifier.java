package com.adeptj.modules.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.security.Key;

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
