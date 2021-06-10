package com.adeptj.modules.restclient;

import org.apache.commons.lang3.Validate;

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

    private final Class<R> responseType;

    private HttpMethod method;

    private Map<String, String> headers;

    private Map<String, String> queryParams;

    private Map<String, String> formParams;

    private T body;

    private ClientRequest(URI uri, Class<R> responseType) {
        Validate.isTrue(uri != null, "URI can't be null!");
        Validate.isTrue(responseType != null, "responseType can't be null!");
        this.uri = uri;
        this.responseType = responseType;
    }

    public URI getUri() {
        return uri;
    }

    public Class<R> getResponseType() {
        return responseType;
    }

    public HttpMethod getMethod() {
        return method;
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

        private Map<String, String> headers;

        private Map<String, String> queryParams;

        private Map<String, String> formParams;

        private T body;

        private Class<R> responseType;

        public Builder<T, R> uri(URI uri) {
            this.uri = uri;
            return this;
        }

        public Builder<T, R> method(HttpMethod method) {
            this.method = method;
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

        public Builder<T, R> responseType(Class<R> responseType) {
            this.responseType = responseType;
            return this;
        }

        public ClientRequest<T, R> build() {
            ClientRequest<T, R> request = new ClientRequest<>(this.uri, this.responseType);
            request.method = this.method;
            request.headers = this.headers;
            request.queryParams = this.queryParams;
            request.formParams = this.formParams;
            request.body = this.body;
            return request;
        }
    }
}
