package com.adeptj.modules.security.core.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "AdeptJ Authenticator Configuration",
        description = "Security configuration for AdeptJ Authenticator"
)
public @interface AuthenticatorConfig {

    @AttributeDefinition(
            name = "Disable Security",
            description = "Enable or disable security."
    )
    boolean disable_security() default true;

    @AttributeDefinition(
            name = "Security Disabled Paths",
            description = "Paths on which security is disabled"
    )
    String[] security_disabled_paths();
}
