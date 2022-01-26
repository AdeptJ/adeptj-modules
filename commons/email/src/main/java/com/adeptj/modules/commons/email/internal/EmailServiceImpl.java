package com.adeptj.modules.commons.email.internal;

import com.adeptj.modules.commons.email.EmailService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.Designate;

@Designate(ocd = EmailConfig.class)
@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class EmailServiceImpl implements EmailService {

    private final EmailConfig config;

    @Activate
    public EmailServiceImpl(@NotNull EmailConfig config) {
        this.config = config;
    }

    @Override
    public void sendSimpleEmail(String subject, String message, String fromAddress, String... toAddresses) {
        try {
            Email email = new SimpleEmail();
            email.setHostName(this.config.smtp_host());
            email.setSmtpPort(this.config.smtp_port());
            email.setAuthentication(this.config.email_username(), this.config.email_password());
            email.setStartTLSEnabled(true);
            if (StringUtils.isEmpty(fromAddress)) {
                email.setFrom(this.config.email_from_address());
            } else {
                email.setFrom(fromAddress);
            }
            email.setSubject(subject);
            email.setMsg(message);
            email.addTo(toAddresses);
            email.send();
        } catch (EmailException ex) {
            ex.printStackTrace();
        }
    }
}
