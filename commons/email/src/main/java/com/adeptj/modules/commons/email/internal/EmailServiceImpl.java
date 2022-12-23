package com.adeptj.modules.commons.email.internal;

import com.adeptj.modules.commons.email.EmailException;
import com.adeptj.modules.commons.email.EmailInfo;
import com.adeptj.modules.commons.email.EmailService;
import com.adeptj.modules.commons.email.EmailType;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.adeptj.modules.commons.email.EmailType.HTML;
import static com.adeptj.modules.commons.email.EmailType.MULTIPART;
import static com.adeptj.modules.commons.email.EmailType.SIMPLE;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

@Designate(ocd = EmailConfig.class)
@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class EmailServiceImpl extends Authenticator implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String TEXT_HTML = "text/html; charset=utf-8";

    private final EmailConfig config;

    private final ExecutorService emailExecutorService;

    @Activate
    public EmailServiceImpl(@NotNull EmailConfig config) {
        this.config = config;
        this.emailExecutorService = Executors.newFixedThreadPool(config.thread_pool_size());
    }

    @Override
    public void sendSimpleEmail(@NotNull EmailInfo emailInfo) {
        this.emailExecutorService.execute(() -> this.doSendEmail(emailInfo, SIMPLE));
    }

    @Override
    public void sendHtmlEmail(@NotNull EmailInfo emailInfo) {
        this.emailExecutorService.execute(() -> this.doSendEmail(emailInfo, HTML));
    }

    @Override
    public void sendMultipartEmail(@NotNull EmailInfo emailInfo) {
        this.emailExecutorService.execute(() -> this.doSendEmail(emailInfo, MULTIPART));
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(this.config.email_username(), this.config.email_password());
    }

    private void doSendEmail(@NotNull EmailInfo emailInfo, EmailType emailType) {
        long startTime = System.nanoTime();
        try {
            Message message = new MimeMessage(this.getSession());
            if (StringUtils.isEmpty(emailInfo.getFromAddress())) {
                message.setFrom(new InternetAddress(this.config.email_from_address()));
            } else {
                message.setFrom(new InternetAddress(emailInfo.getFromAddress()));
            }
            String recipients = String.join(",", emailInfo.getToAddresses());
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            message.setSubject(emailInfo.getSubject());
            if (emailType == SIMPLE) {
                message.setText(emailInfo.getMessage());
            } else if (emailType == HTML) {
                message.setContent(emailInfo.getMessage(), TEXT_HTML);
            } else if (emailType == MULTIPART) {
                message.setContent(emailInfo.getMultipart());
            }
            Transport.send(message);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new EmailException(ex);
        }
        long endTime = NANOSECONDS.toMillis(System.nanoTime() - startTime);
        LOGGER.info("Send email took: {} ms!", endTime);
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

    // <<------------------------------------------ OSGi Internal  ------------------------------------------->>

    @Deactivate
    protected void stop() {
        this.emailExecutorService.shutdown();
    }
}
