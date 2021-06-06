package com.adeptj.modules.httpclient;

import java.net.URI;
import java.util.Map;

public class ClientRequest {

    private final URI uri;

    private HttpMethod httpMethod;

    private Map<String, String> headers;

    private Map<String, String> queryParams;

    private Map<String, String> formParams;

    private String body;

    public ClientRequest(URI uri) {
        this.uri = uri;
    }

    public URI getUri() {
        return uri;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public Map<String, String> getFormParams() {
        return formParams;
    }

    public void setFormParams(Map<String, String> formParams) {
        this.formParams = formParams;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
