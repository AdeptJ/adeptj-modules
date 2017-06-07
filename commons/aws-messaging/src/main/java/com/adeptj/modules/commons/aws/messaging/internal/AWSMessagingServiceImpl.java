package com.adeptj.modules.commons.aws.messaging.internal;

import com.adeptj.modules.commons.aws.messaging.AWSMessagingConfig;
import com.adeptj.modules.commons.aws.messaging.AWSMessagingService;
import com.adeptj.modules.commons.aws.messaging.MessageType;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsync;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsyncClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * AWSMessagingService for sending Email or SMS.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@Designate(ocd = AWSMessagingConfig.class)
@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class AWSMessagingServiceImpl implements AWSMessagingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AWSMessagingServiceImpl.class);

    private AmazonSNSAsync asyncSNS;

    private AmazonSimpleEmailServiceAsync asyncSES;

    private Map<String, MessageAttributeValue> smsAttributes;

    private AWSMessagingConfig config;

    /**
     * Send the given message(either EMAIL or SMS)
     *
     * @param type {@link MessageType} either EMAIL or SMS
     * @param data Message data Map required by the system
     * @return a status if the Message has been sent or not.
     */
    @Override
    public void sendMessage(MessageType type, Map<String, String> data) {
        switch (type) {
            case SMS:
                this.sendSMS(data);
                break;
            case EMAIL:
                this.sendEmail(data);
                break;
            default:
                LOGGER.warn("Unknown MessageType: [{}]", type);
        }
    }

    private void sendSMS(Map<String, String> data) {
        this.asyncSNS.publishAsync(new PublishRequest().withMessage(data.get("message")).
                withPhoneNumber(data.get("mobNo")).withMessageAttributes(this.smsAttributes));
    }

    private void sendEmail(Map<String, String> data) {
        Destination destination = new Destination().withToAddresses(data.get("recipient"));
        Content subject = new Content().withData(data.get("subject"));
        Content textBody = new Content().withData(data.get("message"));
        Message message = new Message().withSubject(subject).withBody(new Body().withText(textBody));
        this.asyncSES.sendEmailAsync(new
                SendEmailRequest().withSource(this.config.from()).withDestination(destination).withMessage(message));
    }

    @Activate
    protected void activate(AWSMessagingConfig config) {
        this.config = config;
        this.smsAttributes = new HashMap<>();
        this.smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue().withStringValue(config.senderId())
                .withDataType("String"));
        this.smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue().withStringValue(config.smsType())
                .withDataType("String"));
        this.asyncSNS = AmazonSNSAsyncClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(config.snsAccessKeyId(), config.snsSecretKey()))).build();
        this.asyncSES = AmazonSimpleEmailServiceAsyncClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(config.sesAccessKeyId(), config.sesSecretKey()))).build();
    }

    @Deactivate
    protected void deactivate() {
        this.asyncSNS.shutdown();
        this.asyncSES.shutdown();
    }
}
