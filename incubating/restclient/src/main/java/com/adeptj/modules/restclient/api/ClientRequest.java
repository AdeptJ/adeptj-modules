package com.adeptj.modules.restclient.api;

import com.adeptj.modules.restclient.util.Assert;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * The request for {@link RestClient}.
 *
 * @param <T> The type of the body
 * @param <R> the type of the response
 * @author Rakesh.Kumar, AdeptJ
 */
public class ClientRequest<T, R> {

    private final URI uri;

    private final Class<R> responseAs;

    private HttpMethod method;

    private long timeout;

    private Map<String, String> headers;

    private Map<String, String> queryParams;

    private Map<String, String> formParams;

    private T body;

    private ClientRequest(URI uri, Class<R> responseAs) {
        Assert.notNull(uri, "URI can't be null!");
        Assert.notNull(responseAs, "responseAs can't be null!");
        this.uri = uri;
        this.responseAs = responseAs;
    }

    public URI getUri() {
        return uri;
    }

    public Class<R> getResponseAs() {
        return responseAs;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public long getTimeout() {
        return timeout;
    }

    // For overriding HttpMethod at RestClient level, see JettyRestClient#GET.
    public ClientRequest<T, R> withMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public Map<String, String> getFormParams() {
        return formParams;
    }

    public T getBody() {
        return body;
    }

    public static <T, R> Builder<T, R> builder() {
        return new Builder<>();
    }

    // Builder

    public static class Builder<T, R> {

        private URI uri;

        private HttpMethod method;

        private long timeout;

        private Map<String, String> headers;

        private Map<String, String> queryParams;

        private Map<String, String> formParams;

        private T body;

        private Class<R> responseAs;

        public Builder<T, R> uri(URI uri) {
            this.uri = uri;
            return this;
        }

        public Builder<T, R> method(HttpMethod method) {
            this.method = method;
            return this;
        }

        /**
         * The request timeout in milliseconds.
         *
         * @param timeout the total timeout for the request/response conversation;
         * @return timeout
         */
        public Builder<T, R> timeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder<T, R> header(String name, String value) {
            if (this.headers == null) {
                this.headers = new HashMap<>();
            }
            this.headers.put(name, value);
            return this;
        }

        public Builder<T, R> queryParam(String name, String value) {
            if (this.queryParams == null) {
                this.queryParams = new HashMap<>();
            }
            this.queryParams.put(name, value);
            return this;
        }

        public Builder<T, R> formParam(String name, String value) {
            if (this.formParams == null) {
                this.formParams = new HashMap<>();
            }
            this.formParams.put(name, value);
            return this;
        }

        public Builder<T, R> body(T body) {
            this.body = body;
            return this;
        }

        public Builder<T, R> responseAs(Class<R> responseAs) {
            this.responseAs = responseAs;
            return this;
        }

        public ClientRequest<T, R> build() {
            ClientRequest<T, R> request = new ClientRequest<>(this.uri, this.responseAs);
            request.method = this.method;
            request.timeout = this.timeout;
            request.headers = this.headers;
            request.queryParams = this.queryParams;
            request.formParams = this.formParams;
            request.body = this.body;
            return request;
        }
    }
}
