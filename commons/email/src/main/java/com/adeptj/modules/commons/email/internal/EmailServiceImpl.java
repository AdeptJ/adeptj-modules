package com.adeptj.modules.commons.email.internal;

import com.adeptj.modules.commons.email.EmailInfo;
import com.adeptj.modules.commons.email.EmailService;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Designate(ocd = EmailConfig.class)
@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class EmailServiceImpl extends Authenticator implements EmailService {

    private final EmailConfig config;

    private final ExecutorService emailExecutorService;

    private Session session;

    @Activate
    public EmailServiceImpl(@NotNull EmailConfig config) {
        this.config = config;
        this.emailExecutorService = Executors.newFixedThreadPool(config.thread_pool_size());
    }

    @Override
    public void sendEmail(@NotNull EmailInfo emailInfo) {
        new EmailSender(emailInfo, this.config.default_from_address(), this.getSession()).send();
    }

    @Override
    public void sendEmailAsync(@NotNull EmailInfo emailInfo) {
        this.emailExecutorService.execute(new EmailSender(emailInfo, this.config.default_from_address(), this.getSession()));
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(this.config.username(), this.config.password());
    }

    private Session getSession() {
        if (this.session == null) {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", this.config.smtp_host());
            props.put("mail.smtp.port", this.config.smtp_port());
            props.put("mail.debug", Boolean.toString(this.config.debug()));
            this.session = Session.getInstance(props, this);
        }
        return this.session;
    }

    // <<------------------------------------------ OSGi Internal  ------------------------------------------->>

    @Deactivate
    protected void stop() {
        this.emailExecutorService.shutdown();
    }
}
