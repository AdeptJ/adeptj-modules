package com.adeptj.modules.commons.email;

import jakarta.mail.internet.MimeMultipart;

import java.util.Set;

public class EmailInfo {

    private String subject;

    private String message;

    private String fromAddress;

    private Set<String> toAddresses;

    private MimeMultipart multipart;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public Set<String> getToAddresses() {
        return toAddresses;
    }

    public void setToAddresses(Set<String> toAddresses) {
        this.toAddresses = toAddresses;
    }

    public MimeMultipart getMultipart() {
        return multipart;
    }

    public void setMultipart(MimeMultipart multipart) {
        this.multipart = multipart;
    }
}
