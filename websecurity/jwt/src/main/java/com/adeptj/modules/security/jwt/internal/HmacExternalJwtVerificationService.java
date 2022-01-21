package com.adeptj.modules.security.jwt.internal;

import com.adeptj.modules.security.jwt.ExternalJwtVerificationService;
import com.adeptj.modules.security.jwt.JwtClaims;
import com.adeptj.modules.security.jwt.JwtVerifier;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.security.Key;

import static java.nio.charset.StandardCharsets.UTF_8;

@Designate(ocd = HmacExternalJwtConfig.class)
@ExternalJwtAlgo("Hmac")
@Component(immediate = true, service = ExternalJwtVerificationService.class)
public class HmacExternalJwtVerificationService implements ExternalJwtVerificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final JwtVerifier jwtVerifier;

    @Activate
    public HmacExternalJwtVerificationService(@NotNull HmacExternalJwtConfig config) {
        try {
            Key verificationKey = Keys.hmacShaKeyFor(config.hmac_key().getBytes(UTF_8));
            this.jwtVerifier = new JwtVerifier(verificationKey, config.log_jwt_verification_exception_trace());
        } catch (WeakKeyException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public JwtClaims verifyJwt(String jwt) {
        return this.jwtVerifier.verify(jwt);
    }
}
