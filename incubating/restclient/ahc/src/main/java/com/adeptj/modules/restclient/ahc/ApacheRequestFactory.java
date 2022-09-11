package com.adeptj.modules.restclient.ahc;

import com.adeptj.modules.restclient.core.ClientRequest;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.jetbrains.annotations.NotNull;

public class ApacheRequestFactory {

    static <T, R> HttpUriRequest newRequest(@NotNull ClientRequest<T, R> request) {
        HttpRequestBase apacheRequest;
        switch (request.getMethod()) {
            case HEAD:
            case GET:
            case OPTIONS:
                apacheRequest = new NonEntityEnclosingRequest(request);
                break;
            case POST:
            case PUT:
            case PATCH:
            case DELETE:
                // Sometimes DELETE has a payload.
                apacheRequest = new EntityEnclosingRequest(request);
                break;
            default:
                throw new IllegalStateException("Unsupported Verb!!");
        }
        HttpClientUtils.handleUri(request, apacheRequest);
        HttpClientUtils.addHeaders(request, apacheRequest);
        return apacheRequest;
    }
}
