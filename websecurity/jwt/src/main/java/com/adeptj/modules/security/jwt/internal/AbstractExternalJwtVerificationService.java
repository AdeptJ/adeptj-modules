package com.adeptj.modules.security.jwt.internal;

import com.adeptj.modules.security.jwt.ExternalJwtVerificationService;
import com.adeptj.modules.security.jwt.JwtClaims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;

public abstract class AbstractExternalJwtVerificationService implements ExternalJwtVerificationService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Key verificationKey;

    public Key getVerificationKey() {
        return verificationKey;
    }

    public void setVerificationKey(Key verificationKey) {
        this.verificationKey = verificationKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JwtClaims verifyJwt(String jwt) {
        JwtClaims claims = null;
        try {
            Assert.hasText(jwt, "JWT can't be blank!!");
            claims = Jwts.parserBuilder()
                    .setSigningKey(this.getVerificationKey())
                    .deserializeJsonWith(new JwtDeserializer())
                    .build()
                    .parse(jwt, new ClaimsConsumer());
        } catch (ExpiredJwtException ex) {
            claims = new JwtClaims(ex.getClaims());
            claims.setExpired(true);
        } catch (JwtException | IllegalArgumentException ex) {
            this.logger.error(ex.getMessage(), ex);
        }
        return claims;
    }
}
