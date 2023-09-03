package com.adeptj.modules.commons.email;

import jakarta.mail.internet.MimeMultipart;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.UUID;

public class EmailInfo {

    private final EmailType emailType;

    private final String subject;

    private final String[] toAddresses;

    private String[] ccAddresses;

    private String[] bccAddresses;

    private String message;

    private String fromAddress;

    private MimeMultipart multipart;

    private String messageId;

    public EmailInfo(EmailType emailType, String subject, String... toAddresses) {
        Validate.isTrue((emailType != null), "EmailType can't be null!!");
        Validate.isTrue(StringUtils.isNotEmpty(subject), "subject can't be null!!");
        Validate.isTrue(ArrayUtils.isNotEmpty(toAddresses), "toAddresses can't be null or empty!");
        this.emailType = emailType;
        this.subject = subject;
        this.toAddresses = toAddresses;
        this.messageId = UUID.randomUUID().toString();
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

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String[] getCcAddresses() {
        return ccAddresses;
    }

    public String[] getBccAddresses() {
        return bccAddresses;
    }

    public String getMessage() {
        return message;
    }

    public MimeMultipart getMultipart() {
        return multipart;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private EmailType emailType;

        private String subject;

        private String[] toAddresses;

        private String[] ccAddresses;

        private String[] bccAddresses;

        private String message;

        private String fromAddress;

        private MimeMultipart multipart;

        public Builder emailType(EmailType emailType) {
            this.emailType = emailType;
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder addToAddresses(String... toAddresses) {
            this.toAddresses = toAddresses;
            return this;
        }

        public Builder addCcAddresses(String... ccAddresses) {
            this.ccAddresses = ccAddresses;
            return this;
        }

        public Builder addBccAddresses(String... bccAddresses) {
            this.bccAddresses = bccAddresses;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder fromAddress(String fromAddress) {
            this.fromAddress = fromAddress;
            return this;
        }

        public Builder multipart(MimeMultipart multipart) {
            this.multipart = multipart;
            return this;
        }

        public EmailInfo build() {
            EmailInfo emailInfo = new EmailInfo(this.emailType, this.subject, this.toAddresses);
            emailInfo.ccAddresses = this.ccAddresses;
            emailInfo.bccAddresses = this.bccAddresses;
            emailInfo.message = this.message;
            emailInfo.fromAddress = this.fromAddress;
            emailInfo.multipart = this.multipart;
            return emailInfo;
        }
    }

    public enum EmailType {
        SIMPLE,HTML,MULTIPART
    }
}
