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

    @AttributeDefinition(name = "Email Default From Address", description = "Email default from address")
    String default_from_address();

    @AttributeDefinition(name = "SMTP Username", description = "SMTP username")
    String smtp_username();

    @AttributeDefinition(name = "SMTP Password", description = "SMTP password", type = PASSWORD)
    String smtp_password();

    @AttributeDefinition(name = "SMTP Mail Debug", description = "Debug SMTP email")
    boolean debug();

    @AttributeDefinition(name = "Email Thread Pool Size", description = "Thread Pool size for send email execution.")
    int thread_pool_size() default 5;
}
