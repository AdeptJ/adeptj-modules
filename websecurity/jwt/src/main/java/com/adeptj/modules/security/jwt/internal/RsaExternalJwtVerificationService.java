package com.adeptj.modules.security.jwt.internal;

import com.adeptj.modules.security.jwt.ExternalJwtVerificationService;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

@Designate(ocd = RsaExternalJwtConfig.class)
@ExternalJwtAlgo("Rsa")
@Component(service = ExternalJwtVerificationService.class)
public class RsaExternalJwtVerificationService extends AbstractExternalJwtVerificationService {

    @Activate
    public RsaExternalJwtVerificationService(@NotNull RsaExternalJwtConfig config) {
        try {
            SignatureAlgorithm algorithm = SignatureAlgorithm.forName(config.signature_algorithm());
            this.logger.info("Selected JWT SignatureAlgorithm: [{}]", algorithm.getJcaName());
            super.setVerificationKey(JwtKeys.createVerificationKey(config.public_key(), algorithm.getFamilyName()));
        } catch (SignatureException | JwtKeyInitializationException | IllegalArgumentException ex) {
            this.logger.error(ex.getMessage(), ex);
            throw ex;
        }
    }
}
