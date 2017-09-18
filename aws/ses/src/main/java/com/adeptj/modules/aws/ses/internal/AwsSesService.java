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
package com.adeptj.modules.aws.ses.internal;

import com.adeptj.modules.aws.core.AwsException;
import com.adeptj.modules.aws.ses.EmailConfig;
import com.adeptj.modules.aws.ses.EmailRequest;
import com.adeptj.modules.aws.ses.EmailResponse;
import com.adeptj.modules.aws.ses.api.EmailService;
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
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * Service implementation for sending email using AWS SES.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@Designate(ocd = EmailConfig.class)
@Component(configurationPolicy = REQUIRE)
public class AwsSesService implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AwsSesService.class);

    private AmazonSimpleEmailServiceAsync asyncSES;

    private AwsSesAsyncHandler awsSesAsyncHandler;

    private EmailConfig emailConfig;

    /**
     * {@inheritDoc}
     */
    @Override
    public EmailResponse sendEmail(EmailRequest emailRequest) {
        try {
            SendEmailResult result = this.asyncSES.sendEmail(new SendEmailRequest()
                    .withSource(this.emailConfig.from())
                    .withDestination(new Destination()
                            .withToAddresses(emailRequest.getRecipientToList())
                            .withCcAddresses(emailRequest.getRecipientCcList())
                            .withBccAddresses(emailRequest.getRecipientBccList()))
                    .withMessage(new Message()
                            .withSubject(new Content().withData(emailRequest.getSubject()))
                            .withBody(new Body().withHtml(new Content().withData(emailRequest.getBody())))));
            return new EmailResponse(result.getMessageId(), result.getSdkHttpMetadata().getHttpStatusCode(),
                    result.getSdkHttpMetadata().getHttpHeaders());
        } catch (Exception ex) {
            LOGGER.error("Exception while sending email!!", ex);
            throw new AwsException(ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendEmailAsync(EmailRequest emailRequest) {
        try {
            // Shall we use the Future object returned by async call?
            this.asyncSES.sendEmailAsync(new SendEmailRequest()
                            .withSource(this.emailConfig.from())
                            .withDestination(new Destination()
                                    .withToAddresses(emailRequest.getRecipientToList())
                                    .withCcAddresses(emailRequest.getRecipientCcList())
                                    .withBccAddresses(emailRequest.getRecipientBccList()))
                            .withMessage(new Message()
                                    .withSubject(new Content().withData(emailRequest.getSubject()))
                                    .withBody(new Body().withHtml(new Content().withData(emailRequest.getBody())))),
                    this.awsSesAsyncHandler);
        } catch (Exception ex) {
            LOGGER.error("Exception while sending email asynchronously!!", ex);
        }
    }

    // Lifecycle Methods

    @Activate
    protected void activate(EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
        try {
            this.asyncSES = AmazonSimpleEmailServiceAsyncClientBuilder.standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(emailConfig.serviceEndpoint(),
                            emailConfig.signingRegion()))
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(emailConfig.accessKey(),
                            emailConfig.secretKey())))
                    .build();
            this.awsSesAsyncHandler = new AwsSesAsyncHandler();
        } catch (Exception ex) {
            LOGGER.error("Exception while starting EmailService!!", ex);
        }
    }

    @Deactivate
    protected void deactivate() {
        try {
            this.asyncSES.shutdown();
        } catch (Exception ex) {
            LOGGER.error("Exception while SES client shutdown!!", ex);
        }
    }
}
