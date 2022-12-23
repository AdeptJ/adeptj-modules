package com.adeptj.modules.commons.email.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import static org.osgi.service.metatype.annotations.AttributeType.PASSWORD;

/**
 * AdeptJ EmailService Config.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ObjectClassDefinition(
        name = "AdeptJ EmailService Configuration",
        description = "Configuration for AdeptJ EmailService."
)
public @interface EmailConfig {

    @AttributeDefinition(name = "SMTP Host", description = "Address of the SMTP host")
    String smtp_host();

    @AttributeDefinition(name = "SMTP Port", description = "Port of the SMTP host")
    int smtp_port();

    @AttributeDefinition(name = "Email From Address", description = "Email from address")
    String email_from_address();

    @AttributeDefinition(name = "SMTP Username", description = "SMTP username")
    String email_username();

    @AttributeDefinition(name = "SMTP Password", description = "SMTP password", type = PASSWORD)
    String email_password();

    @AttributeDefinition(name = "SMTP Mail Debug", description = "Debug SMTP email")
    boolean debug();
}
