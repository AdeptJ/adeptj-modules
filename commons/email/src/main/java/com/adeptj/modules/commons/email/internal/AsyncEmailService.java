package com.adeptj.modules.commons.email.internal;

import com.adeptj.modules.commons.email.EmailInfo;
import com.adeptj.modules.commons.email.EmailService;
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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Designate(ocd = EmailConfig.class)
@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class AsyncEmailService implements EmailService {

    private final EmailConfig config;

    private final ExecutorService emailExecutorService;

    private Session session;

    private final Lock lock;

    @Activate
    public AsyncEmailService(@NotNull EmailConfig config) {
        this.config = config;
        this.emailExecutorService = Executors.newFixedThreadPool(config.thread_pool_size());
        this.lock = new ReentrantLock();
    }

    @Override
    public void sendEmail(@NotNull EmailInfo emailInfo) {
        this.emailExecutorService.execute(new EmailSender(emailInfo, this.config, this.getSession()));
    }

    private Session getSession() {
        this.lock.lock();
        try {
            if (this.session == null) {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.debug", Boolean.toString(this.config.debug()));
                this.session = Session.getInstance(props);
            }
        } finally {
            this.lock.unlock();
        }
        return this.session;
    }

    // <<------------------------------------------ OSGi Internal  ------------------------------------------->>

    @Deactivate
    protected void stop() {
        this.emailExecutorService.shutdown();
    }
}
