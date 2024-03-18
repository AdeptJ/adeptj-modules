package com.adeptj.modules.restclient.apache;

import com.adeptj.modules.restclient.core.ClientRequest;
import com.adeptj.modules.restclient.core.HttpMethod;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.jetbrains.annotations.NotNull;

public class ApacheRequestFactory {

    private ApacheRequestFactory() {
    }

    static <T, R> HttpUriRequest newRequest(@NotNull ClientRequest<T, R> request) {
        HttpMethod method = request.getMethod();
        HttpRequestBase apacheRequest = switch (method) {
            case HEAD, GET, OPTIONS -> new NonEntityEnclosingRequest(method.toString());
            case POST, PUT, PATCH, DELETE -> HttpClientUtils.createEntityEnclosingRequest(request);
            default -> throw new IllegalStateException("Unsupported HttpMethod!!");
        };
        HttpClientUtils.addHeaders(request, apacheRequest);
        HttpClientUtils.addQueryParams(request, apacheRequest);
        return apacheRequest;
    }
}
