package com.adeptj.modules.security.jwt.internal;

import com.adeptj.modules.security.jwt.ExternalJwtVerificationService;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

import static java.nio.charset.StandardCharsets.UTF_8;

@Designate(ocd = HmacExternalJwtConfig.class)
@ExternalJwtAlgo("Hmac")
@Component(service = ExternalJwtVerificationService.class)
public class HmacExternalJwtVerificationService extends AbstractExternalJwtVerificationService {

    @Activate
    public HmacExternalJwtVerificationService(@NotNull HmacExternalJwtConfig config) {
        try {
            super.setVerificationKey(Keys.hmacShaKeyFor(config.hmac_key().getBytes(UTF_8)));
        } catch (WeakKeyException ex) {
            this.logger.error(ex.getMessage(), ex);
            throw ex;
        }
    }
}
