package com.adeptj.modules.commons.aws.messaging;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

/**
 * @author Rakesh.Kumar, AdeptJ.
 */
@ObjectClassDefinition(name = "", description = "")
public @interface AWSMessagingConfig {

    // SNS Configs

    @AttributeDefinition(name = "senderId", description = "AWS SNS SenderID")
    String senderId();

    @AttributeDefinition(name = "SMSType", options = {@Option(label = "TRANSACTIONAL", value = "Transactional"),
            @Option(label = "PROMOTIONAL", value = "Promotional")})
    String smsType();

    @AttributeDefinition(name = "snsAccessKeyId", description = "AWS SNS AccessKeyId")
    String snsAccessKeyId();

    @AttributeDefinition(name = "snsSecretKey", description = "AWS SNS SecretKey")
    String snsSecretKey();

    // SES Configs

    @AttributeDefinition(name = "from", description = "AWS SES Email From")
    String from();

    @AttributeDefinition(name = "sesAccessKeyId", description = "AWS SES AccessKeyId")
    String sesAccessKeyId();

    @AttributeDefinition(name = "sesSecretKey", description = "AWS SES SecretKey")
    String sesSecretKey();
}
