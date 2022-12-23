package com.adeptj.modules.commons.email.internal;

import com.adeptj.modules.commons.email.EmailException;
import com.adeptj.modules.commons.email.EmailInfo;
import com.adeptj.modules.commons.email.EmailService;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Properties;
import java.util.function.Supplier;

@Designate(ocd = EmailConfig.class)
@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class EmailServiceImpl extends Authenticator implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String TEXT_HTML = "text/html; charset=utf-8";

    private final EmailConfig config;

    @Activate
    public EmailServiceImpl(@NotNull EmailConfig config) {
        this.config = config;
    }

    @Override
    public void sendSimpleEmail(@NotNull EmailInfo emailInfo) {
        try {
            Message message = this.getMessage(emailInfo);
            message.setText(emailInfo.getMessage());
            Transport.send(message);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new EmailException(ex);
        }
    }

    @Override
    public void sendHtmlEmail(@NotNull EmailInfo emailInfo) {
        try {
            Message message = this.getMessage(emailInfo);
            message.setContent(emailInfo.getMessage(), TEXT_HTML);
            Transport.send(message);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new EmailException(ex);
        }
    }

    @Override
    public void sendMultipartEmail(@NotNull EmailInfo emailInfo, @NotNull Supplier<MimeMultipart> supplier) {
        try {
            Message message = this.getMessage(emailInfo);
            message.setContent(supplier.get());
            Transport.send(message);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new EmailException(ex);
        }
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(this.config.email_username(), this.config.email_password());
    }

    private @NotNull Message getMessage(@NotNull EmailInfo emailInfo) throws Exception {
        Message message = new MimeMessage(this.getSession());
        if (StringUtils.isEmpty(emailInfo.getFromAddress())) {
            message.setFrom(new InternetAddress(this.config.email_from_address()));
        } else {
            message.setFrom(new InternetAddress(emailInfo.getFromAddress()));
        }
        String recipients = String.join(",", emailInfo.getToAddresses());
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        message.setSubject(emailInfo.getSubject());
        return message;
    }

    private @NotNull Session getSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", this.config.smtp_host());
        props.put("mail.smtp.port", this.config.smtp_port());
        props.put("mail.debug", this.config.debug());
        return Session.getInstance(props, this);
    }
}
