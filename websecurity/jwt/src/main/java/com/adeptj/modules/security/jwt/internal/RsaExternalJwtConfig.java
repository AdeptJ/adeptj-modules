package com.adeptj.modules.security.jwt.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

@ObjectClassDefinition(
        name = "AdeptJ Rsa External JWT Service Configuration",
        description = "Configs for AdeptJ Rsa External JWT Service Configuration"
)
public @interface RsaExternalJwtConfig {

    @AttributeDefinition(
            name = "JWT Signature Verification Algorithm",
            description = "RSA Signature algorithm for external JWT verification.",
            options = {
                    @Option(label = "RSA 256", value = "RS256"),
                    @Option(label = "RSA 384", value = "RS384"),
                    @Option(label = "RSA 512", value = "RS512"),
            }
    )
    String signature_algorithm();

    @AttributeDefinition(
            name = "Jwt PublicKey(Verification Key)",
            description = "PublicKey data (PEM-encoded) for JWT verification."
    )
    String public_key();

    @AttributeDefinition(
            name = "Log Jwt Verification Exception Trace",
            description = "Whether to log the Jwt verification exception trace in server logs."
    )
    boolean log_jwt_verification_exception_trace();
}
