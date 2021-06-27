package com.adeptj.modules.restclient.api;

import java.util.Map;

/**
 * The response from {@link RestClient}.
 *
 * @param <T> The type of the response
 * @author Rakesh.Kumar, AdeptJ
 */
public class ClientResponse<T> {

    private int status;

    private String reason;

    private Map<String, String> headers;

    private T content;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
