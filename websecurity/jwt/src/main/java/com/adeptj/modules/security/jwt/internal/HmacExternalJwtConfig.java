package com.adeptj.modules.security.jwt.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "AdeptJ Hmac External JWT Service Configuration",
        description = "Configs for AdeptJ Hmac External JWT Service."
)
public @interface HmacExternalJwtConfig {

    @AttributeDefinition(
            name = "Jwt Hmac Key(Verification Key)",
            description = "Hmac key for JWT verification."
    )
    String hmac_key();

    @AttributeDefinition(
            name = "Log Jwt Verification Exception Trace",
            description = "Whether to log the Jwt verification exception trace in server logs."
    )
    boolean log_jwt_verification_exception_trace();
}
