package com.adeptj.modules.security.jwt.internal;

import com.adeptj.modules.security.jwt.ExternalJwtVerificationService;
import com.adeptj.modules.security.jwt.JwtClaims;
import com.adeptj.modules.security.jwt.JwtKeyInitializationException;
import com.adeptj.modules.security.jwt.JwtKeys;
import com.adeptj.modules.security.jwt.JwtVerifier;
import com.adeptj.modules.security.jwt.RsaVerificationKeyInfo;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.security.Key;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

@Designate(ocd = RsaExternalJwtConfig.class)
@ExternalJwtAlgo("Rsa")
@Component(service = ExternalJwtVerificationService.class, configurationPolicy = REQUIRE)
public class RsaExternalJwtVerificationService implements ExternalJwtVerificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final JwtVerifier jwtVerifier;

    @Activate
    public RsaExternalJwtVerificationService(@NotNull RsaExternalJwtConfig config) {
        try {
            SignatureAlgorithm algorithm = SignatureAlgorithm.forName(config.signature_algorithm());
            LOGGER.info("Selected JWT SignatureAlgorithm: [{}]", algorithm.getJcaName());
            Key verificationKey = JwtKeys.createVerificationKey(new RsaVerificationKeyInfo(algorithm, config.public_key()));
            this.jwtVerifier = new JwtVerifier(verificationKey, config.log_jwt_verification_exception_trace());
        } catch (SignatureException | JwtKeyInitializationException | IllegalArgumentException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public JwtClaims verifyJwt(String jwt) {
        return this.jwtVerifier.verify(jwt);
    }
}
