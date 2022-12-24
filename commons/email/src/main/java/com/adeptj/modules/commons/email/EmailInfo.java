package com.adeptj.modules.commons.email;

import jakarta.mail.internet.MimeMultipart;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class EmailInfo {

    private final EmailType emailType;

    private final String subject;

    private final String[] toAddresses;

    private String message;

    private String fromAddress;

    private MimeMultipart multipart;

    public EmailInfo(EmailType emailType, String subject, String... toAddresses) {
        Validate.isTrue(emailType != null, "EmailType can't be null!!");
        Validate.isTrue(StringUtils.isNotEmpty(subject), "subject can't be null!!");
        Validate.isTrue(ArrayUtils.isNotEmpty(toAddresses), "toAddresses can't be null or empty!");
        this.emailType = emailType;
        this.subject = subject;
        // remove duplicates.
        Set<String> addresses = new HashSet<>();
        Collections.addAll(addresses, toAddresses);
        this.toAddresses = addresses.toArray(new String[0]);
    }

    public EmailType getEmailType() {
        return emailType;
    }

    public String getSubject() {
        return subject;
    }

    public String[] getToAddresses() {
        return toAddresses;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public MimeMultipart getMultipart() {
        return multipart;
    }

    public void setMultipart(MimeMultipart multipart) {
        this.multipart = multipart;
    }
}
