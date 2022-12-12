package com.adeptj.modules.commons.restclient.apache;

import com.adeptj.modules.commons.restclient.core.ClientRequest;
import com.adeptj.modules.commons.restclient.core.HttpMethod;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.jetbrains.annotations.NotNull;

public class ApacheRequestFactory {

    static <T, R> HttpUriRequest newRequest(@NotNull ClientRequest<T, R> request) {
        HttpRequestBase apacheRequest;
        HttpMethod method = request.getMethod();
        switch (method) {
            case HEAD:
            case GET:
            case OPTIONS:
                apacheRequest = new NonEntityEnclosingRequest(method.toString());
                break;
            case POST:
            case PUT:
            case PATCH:
            case DELETE: // Sometimes DELETE has a payload.
                apacheRequest = HttpClientUtils.createEntityEnclosingRequest(request);
                break;
            default:
                throw new IllegalStateException("Unsupported HttpMethod!!");
        }
        HttpClientUtils.addHeaders(request, apacheRequest);
        HttpClientUtils.addQueryParams(request, apacheRequest);
        return apacheRequest;
    }
}
