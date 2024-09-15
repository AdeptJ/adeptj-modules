package com.adeptj.modules.commons.email.internal;

import com.adeptj.modules.commons.email.EmailException;
import com.adeptj.modules.commons.email.EmailInfo;
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
import static com.adeptj.modules.commons.email.Constants.HEADER_MESSAGE_ID;
import static com.adeptj.modules.commons.email.Constants.TEXT_HTML;
import static com.adeptj.modules.commons.email.EmailInfo.EmailType.HTML;
import static com.adeptj.modules.commons.email.EmailInfo.EmailType.MULTIPART;
import static com.adeptj.modules.commons.email.EmailInfo.EmailType.SIMPLE;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

class EmailSender implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EmailInfo emailInfo;

    private final Session session;

    private final String host;

    private final int port;

    private final String username;

    private final String password;

    EmailSender(@NotNull EmailInfo emailInfo, @NotNull EmailConfig config, @NotNull Session session) {
        EmailInfo.EmailType emailType = emailInfo.getEmailType();
        if (emailType == SIMPLE || emailType == HTML) {
            Validate.isTrue(StringUtils.isNotEmpty(emailInfo.getMessage()), "message can't be null!!");
        } else if (emailType == MULTIPART) {
            Validate.isTrue((emailInfo.getMultipart() != null), "MimeMultipart can't be null!");
        }
        if (StringUtils.isEmpty(emailInfo.getFromAddress())) {
            emailInfo.setFromAddress(config.default_from_address());
        }
        this.emailInfo = emailInfo;
        this.session = session;
        this.host = config.smtp_host();
        this.port = config.smtp_port();
        this.username = config.smtp_username();
        this.password = config.smtp_password();
    }

    @Override
    public void run() {
        this.send();
    }

    private void send() {
        String messageId = this.emailInfo.getMessageId();
        LOGGER.info("Sending email with Message-ID: {}", messageId);
        long startTime = System.nanoTime();
        try {
            this.doSend();
        } catch (Exception ex) {
            String msg = String.format("Exception while sending email with Message-ID: %s", messageId);
            LOGGER.error(msg, ex);
            throw new EmailException(ex);
        }
        long endTime = NANOSECONDS.toMillis(System.nanoTime() - startTime);
        LOGGER.info("Email with Message-ID: {} sent in {} ms!", messageId, endTime);
    }

    private void doSend() throws MessagingException {
        long start = System.nanoTime();
        try (Transport transport = this.session.getTransport(DEFAULT_PROTOCOL)) {
            transport.connect(this.host, this.port, this.username, this.password);
            if (LOGGER.isDebugEnabled()) {
                long end = NANOSECONDS.toMillis(System.nanoTime() - start);
                LOGGER.debug("Transport.connect() took: {} ms!", end);
            }
            MimeMessage message = this.getMessage();
            transport.sendMessage(message, message.getAllRecipients());
        }
    }

    @NotNull
    private MimeMessage getMessage() throws MessagingException {
        MimeMessage message = new MimeMessage(this.session);
        message.addHeader(HEADER_MESSAGE_ID, this.emailInfo.getMessageId());
        message.setSubject(this.emailInfo.getSubject());
        message.setFrom(new InternetAddress(this.emailInfo.getFromAddress()));
        this.handleAddresses(message, Message.RecipientType.TO, this.emailInfo.getToAddresses());
        this.handleAddresses(message, Message.RecipientType.CC, this.emailInfo.getCcAddresses());
        this.handleAddresses(message, Message.RecipientType.BCC, this.emailInfo.getBccAddresses());
        this.setEmailContent(message);
        return message;
    }

    private void handleAddresses(MimeMessage message,
                                 Message.RecipientType type, String[] addresses) throws MessagingException {
        if (addresses == null || addresses.length == 0) {
            LOGGER.error("No address(es) provided in [{}] address field.", type);
            return;
        }
        if (addresses.length == 1) {
            message.setRecipient(type, new InternetAddress(addresses[0]));
        } else {
            // Deduplicate the addresses.
            Set<String> addressSet = new HashSet<>();
            Collections.addAll(addressSet, addresses);
            String recipients = String.join(DELIM_COMMA, addressSet.toArray(new String[0]));
            message.setRecipients(type, recipients);
        }
    }

    private void setEmailContent(MimeMessage message) throws MessagingException {
        switch (this.emailInfo.getEmailType()) {
            case SIMPLE -> message.setText(this.emailInfo.getMessage());
            case HTML -> message.setContent(this.emailInfo.getMessage(), TEXT_HTML);
            case MULTIPART -> message.setContent(this.emailInfo.getMultipart());
        }
    }
}
