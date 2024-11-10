/*
###############################################################################
#                                                                             #
#    Copyright 2016-2024, AdeptJ (http://www.adeptj.com)                      #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/
package com.adeptj.modules.restclient.core;

import com.adeptj.modules.restclient.core.util.Assert;
import com.adeptj.modules.restclient.core.util.ClientRequestUtil;
import org.jetbrains.annotations.Nullable;

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

    private Class<R> responseAs;

    private Class<R> responseDeserializationType;

    private HttpMethod method;

    private long timeout;

    private Map<String, String> headers;

    private Map<String, String> queryParams;

    private Map<String, String> formParams;

    private Map<String, String> uriVariables;

    /**
     * The JSON body, which will be serialized to String if not already a String.
     */
    private T body;

    private ClientRequest(URI uri) {
        ClientRequestUtil.checkURI(uri);
        this.uri = uri;
    }

    public URI getURI() {
        return uri;
    }

    public @Nullable Class<R> getResponseAs() {
        return responseAs;
    }

    public void setResponseAs(Class<R> responseAs) {
        // responseAs can be null, means no response deserialization is needed.
        this.responseAs = responseAs;
    }

    public HttpMethod getMethod() {
        return method;
    }

    // For overriding HttpMethod at RestClient level, see JettyRestClient#GET.
    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    /**
     * Timeout in milliseconds.
     *
     * @return timeout in milliseconds.
     */
    public long getTimeout() {
        return timeout;
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

    /**
     * Builder for use case where no response serialization is needed.
     *
     * @param <T> The type of the body
     * @param <R> The type of the response
     * @return the Builder instance.
     */
    public static <T, R> Builder<T, R> builder() {
        return builder(null);
    }

    /**
     * Builder for use case where response serialization of given type is needed .
     *
     * @param <T> The type of the body
     * @param <R> The type of the response
     * @return the Builder instance.
     */
    public static <T, R> Builder<T, R> builder(Class<R> responseAs) {
        return new Builder<>(responseAs);
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

        private final Class<R> responseAs;

        public Builder(Class<R> responseAs) {
            this.responseAs = responseAs;
        }

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

        public Builder<T, R> headers(Map<String, String> reqHeaders) {
            if (this.headers == null) {
                this.headers = new HashMap<>();
            }
            this.headers.putAll(reqHeaders);
            return this;
        }

        public Builder<T, R> queryParam(String name, String value) {
            if (this.queryParams == null) {
                this.queryParams = new HashMap<>();
            }
            this.queryParams.put(name, value);
            return this;
        }

        public Builder<T, R> queryParams(Map<String, String> params) {
            if (this.queryParams == null) {
                this.queryParams = new HashMap<>();
            }
            this.queryParams.putAll(params);
            return this;
        }

        public Builder<T, R> formParam(String name, String value) {
            if (this.formParams == null) {
                this.formParams = new HashMap<>();
            }
            this.formParams.put(name, value);
            return this;
        }

        public Builder<T, R> formParams(Map<String, String> params) {
            if (this.formParams == null) {
                this.formParams = new HashMap<>();
            }
            this.formParams.putAll(params);
            return this;
        }

        public Builder<T, R> body(T body) {
            this.body = body;
            return this;
        }

        public ClientRequest<T, R> build() {
            ClientRequest<T, R> request = new ClientRequest<>(this.uri);
            request.responseAs = this.responseAs;
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
