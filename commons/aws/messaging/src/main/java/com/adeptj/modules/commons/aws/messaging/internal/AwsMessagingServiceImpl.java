/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://adeptj.com)                               #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/
package com.adeptj.modules.commons.aws.messaging.internal;

import com.adeptj.modules.commons.aws.messaging.AwsMessagingConfig;
import com.adeptj.modules.commons.aws.messaging.AwsMessagingService;
import com.adeptj.modules.commons.aws.messaging.MessageType;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
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
 * AwsMessagingService for sending Email/SMS asynchronously.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@Designate(ocd = AwsMessagingConfig.class)
@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class AwsMessagingServiceImpl implements AwsMessagingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AwsMessagingServiceImpl.class);

    private AmazonSNSAsync asyncSNS;

    private AmazonSimpleEmailServiceAsync asyncSES;

    private AwsSnsAsyncHandler awsSnsAsyncHandler;

    private AwsSesAsyncHandler awsSesAsyncHandler;

    private Map<String, MessageAttributeValue> smsAttributes;

    private AwsMessagingConfig config;

    /**
     * Send the given message(either EMAIL or SMS)
     *
     * @param type {@link MessageType} either EMAIL or SMS
     * @param data Message data Map required by the system
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
        try {
            LOGGER.info("Sending SMS to: [{}]", data.get("mobNo"));
            this.asyncSNS.publishAsync(new PublishRequest()
                    .withMessage(data.get("message"))
                    .withPhoneNumber(data.get("mobNo"))
                    .withMessageAttributes(this.smsAttributes), this.awsSnsAsyncHandler);
        } catch (Exception ex) {
            LOGGER.error("Exception while sending sms!!", ex);
        }

    }

    private void sendEmail(Map<String, String> data) {
        try {
            LOGGER.info("Sending Email to: [{}]", data.get("recipient"));
            this.asyncSES.sendEmailAsync(new SendEmailRequest()
                    .withSource(this.config.from())
                    .withDestination(new Destination().withToAddresses(data.get("recipient")))
                    .withMessage(new Message()
                            .withSubject(new Content().withData(data.get("subject")))
                            .withBody(new Body().withHtml(new Content().withData(data.get("message"))))),
                    this.awsSesAsyncHandler);
        } catch (Exception ex) {
            LOGGER.error("Exception while sending email!!", ex);
        }
    }

    // Lifecycle Methods

    @Activate
    protected void start(AwsMessagingConfig config) {
        this.config = config;
        this.smsAttributes = new HashMap<>();
        this.smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
                .withStringValue(config.senderId())
                .withDataType("String"));
        this.smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
                .withStringValue(config.smsType())
                .withDataType("String"));
        AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(new BasicAWSCredentials(
                config.accessKeyId(), config.secretKey()));
        try {
            this.asyncSNS = AmazonSNSAsyncClientBuilder.standard()
                    .withEndpointConfiguration(getEndpointConfiguration(config.snsServiceEndpoint(), config.snsSigningRegion()))
                    .withCredentials(credentialsProvider)
                    .build();
            this.asyncSES = AmazonSimpleEmailServiceAsyncClientBuilder.standard()
                    .withEndpointConfiguration(getEndpointConfiguration(config.sesServiceEndpoint(), config.sesSigningRegion()))
                    .withCredentials(credentialsProvider)
                    .build();
            this.awsSnsAsyncHandler = new AwsSnsAsyncHandler();
            this.awsSesAsyncHandler = new AwsSesAsyncHandler();
        } catch (Exception ex) {
            LOGGER.error("Exception while starting AwsMessagingService!!", ex);
        }
    }

    private AwsClientBuilder.EndpointConfiguration getEndpointConfiguration(String serviceEndpoint, String signingRegion) {
        return new AwsClientBuilder.EndpointConfiguration(serviceEndpoint, signingRegion);
    }

    @Deactivate
    protected void stop() {
        this.shutdownSNS();
        this.shutdownSES();
    }

    private void shutdownSNS() {
        try {
            this.asyncSNS.shutdown();
        } catch (Exception ex) {
            LOGGER.error("Exception while SNS client shutdown!!", ex);
        }
    }

    private void shutdownSES() {
        try {
            this.asyncSES.shutdown();
        } catch (Exception ex) {
            LOGGER.error("Exception while SES client shutdown!!", ex);
        }
    }
}
