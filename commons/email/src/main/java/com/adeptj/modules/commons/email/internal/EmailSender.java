package com.adeptj.modules.commons.email.internal;

import com.adeptj.modules.commons.email.EmailException;
import com.adeptj.modules.commons.email.EmailInfo;
import com.adeptj.modules.commons.email.EmailType;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.adeptj.modules.commons.email.Constants.DEFAULT_PROTOCOL;
import static com.adeptj.modules.commons.email.Constants.DELIM_COMMA;
import static com.adeptj.modules.commons.email.Constants.TEXT_HTML;
import static com.adeptj.modules.commons.email.EmailType.HTML;
import static com.adeptj.modules.commons.email.EmailType.MULTIPART;
import static com.adeptj.modules.commons.email.EmailType.SIMPLE;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class EmailSender implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EmailInfo emailInfo;

    private final Session session;

    public EmailSender(@NotNull EmailInfo emailInfo, @NotNull String defaultFromAddress, @NotNull Session session) {
        EmailType emailType = emailInfo.getEmailType();
        if (emailType == SIMPLE || emailType == HTML) {
            Validate.isTrue(StringUtils.isNotEmpty(emailInfo.getMessage()), "message can't be null!!");
        } else if (emailType == MULTIPART) {
            Validate.isTrue(emailInfo.getMultipart() != null, "MimeMultipart can't be null!");
        }
        if (StringUtils.isEmpty(emailInfo.getFromAddress())) {
            emailInfo.setFromAddress(defaultFromAddress);
        }
        this.emailInfo = emailInfo;
        this.session = session;
    }

    @Override
    public void run() {
        this.send();
    }

    public void send() {
        LOGGER.info("Sending email with Message-ID: {}", this.emailInfo.getMessageId());
        long startTime = System.nanoTime();
        try {
            MimeMessage message = this.getMessage();
            long start = System.nanoTime();
            try (Transport transport = this.session.getTransport(DEFAULT_PROTOCOL)) {
                transport.connect();
                if (LOGGER.isDebugEnabled()) {
                    long end = NANOSECONDS.toMillis(System.nanoTime() - start);
                    LOGGER.debug("Transport.connect() took: {} ms!", end);
                }
                transport.sendMessage(message, message.getAllRecipients());
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new EmailException(ex);
        }
        long endTime = NANOSECONDS.toMillis(System.nanoTime() - startTime);
        LOGGER.info("Email with Message-ID: {} sent in {} ms!", this.emailInfo.getMessageId(), endTime);
    }

    @NotNull
    private MimeMessage getMessage() throws MessagingException {
        MimeMessage message = new MimeMessage(this.session);
        message.addHeader("Message-ID", this.emailInfo.getMessageId());
        message.setSubject(this.emailInfo.getSubject());
        message.setFrom(new InternetAddress(this.emailInfo.getFromAddress()));
        String[] toAddresses = this.emailInfo.getToAddresses();
        if (toAddresses.length == 1) {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(toAddresses[0]));
        } else {
            Set<String> addressSet = new HashSet<>();
            Collections.addAll(addressSet, toAddresses);
            String recipients = String.join(DELIM_COMMA, addressSet.toArray(new String[0]));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        }
        EmailType emailType = this.emailInfo.getEmailType();
        if (emailType == SIMPLE) {
            message.setText(this.emailInfo.getMessage());
        } else if (emailType == HTML) {
            message.setContent(this.emailInfo.getMessage(), TEXT_HTML);
        } else if (emailType == MULTIPART) {
            message.setContent(this.emailInfo.getMultipart());
        }
        return message;
    }
}
