package com.adeptj.modules.commons.email.internal;

import com.adeptj.modules.commons.email.EmailInfo;
import com.adeptj.modules.commons.email.EmailService;
import com.adeptj.modules.commons.email.EmailType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

@Designate(ocd = EmailConfig.class)
@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class EmailServiceImpl implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EmailConfig config;

    @Activate
    public EmailServiceImpl(@NotNull EmailConfig config) {
        this.config = config;
    }

    @Override
    public void sendEmail(@NotNull EmailType emailType, @NotNull EmailInfo emailInfo) {
        switch (emailType) {
            case SIMPLE:
                this.doSendSimpleEmail(emailInfo);
                break;
            case HTML:
                this.doSendHtmlEmail(emailInfo);
                break;
            case MULTIPART:
                this.doSendMultipartEmail(emailInfo);
        }
    }

    private void doSendSimpleEmail(@NotNull EmailInfo emailInfo) {
        try {
            SimpleEmail email = new SimpleEmail();
            this.setCommonAttributes(email, emailInfo);
            email.send();
        } catch (EmailException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private void doSendHtmlEmail(@NotNull EmailInfo emailInfo) {
        try {
            HtmlEmail email = new HtmlEmail();
            this.setCommonAttributes(email, emailInfo);
            email.send();
        } catch (EmailException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private void doSendMultipartEmail(@NotNull EmailInfo emailInfo) {
        try {
            MultiPartEmail email = new MultiPartEmail();
            this.setCommonAttributes(email, emailInfo);
            email.send();
        } catch (EmailException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private void setCommonAttributes(@NotNull Email email, @NotNull EmailInfo emailInfo) throws EmailException {
        email.setHostName(this.config.smtp_host());
        email.setSmtpPort(this.config.smtp_port());
        email.setAuthentication(this.config.email_username(), this.config.email_password());
        email.setStartTLSEnabled(true);
        if (StringUtils.isEmpty(emailInfo.getFromAddress())) {
            email.setFrom(this.config.email_from_address());
        } else {
            email.setFrom(emailInfo.getFromAddress());
        }
        email.setSubject(emailInfo.getSubject());
        email.setMsg(emailInfo.getMessage());
        email.addTo(emailInfo.getToAddresses().toArray(new String[0]));
    }
}
