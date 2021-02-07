package com.adeptj.modules.commons.email.service.internal;

import com.adeptj.modules.commons.email.service.EmailService;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.Designate;

//@Designate(ocd = EmailConfig.class)
//@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class EmailServiceImpl implements EmailService {

    @Activate
    public EmailServiceImpl(EmailConfig config) {
    }

    @Override
    public void sendEmail() throws EmailException {
        Email email = new SimpleEmail();
        email.setHostName("smtp.googlemail.com");
        email.setSmtpPort(465);
        email.setAuthentication("username", "password");
        email.setStartTLSEnabled(true);
        email.setFrom("user@gmail.com");
        email.setSubject("TestMail");
        email.setMsg("Test email from AdeptJ");
        email.addTo("john.reese@johnreese.com");
        email.send();
    }
}
