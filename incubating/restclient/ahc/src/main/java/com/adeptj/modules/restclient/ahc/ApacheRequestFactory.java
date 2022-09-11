package com.adeptj.modules.restclient.ahc;

import com.adeptj.modules.restclient.core.ClientRequest;
import com.adeptj.modules.restclient.core.HttpMethod;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.jetbrains.annotations.NotNull;

import java.net.URISyntaxException;
import java.util.Map;

public class ApacheRequestFactory {

    static <T, R> HttpUriRequest newRequest(@NotNull ClientRequest<T, R> request) {
        HttpRequestBase req;
        HttpMethod method = request.getMethod();
        switch (method) {
            case HEAD:
            case GET:
            case OPTIONS:
                req = new NonEntityEnclosingRequest(request);
                break;
            case POST:
            case PUT:
            case PATCH:
            case DELETE:
                // Sometimes DELETE has a payload.
                req = new EntityEnclosingRequest(request);
                break;
            default:
                throw new IllegalStateException("Unsupported Verb!!");
        }
        Map<String, String> headers = request.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                req.addHeader(entry.getKey(), entry.getValue());
            }
        }
        handleUri(request, req);
        return req;
    }

    private static <T, R> void handleUri(@NotNull ClientRequest<T, R> request, HttpRequestBase req) {
        Map<String, String> queryParams = request.getQueryParams();
        if (queryParams == null || queryParams.isEmpty()) {
            req.setURI(request.getUri());
            return;
        }
        URIBuilder uriBuilder = new URIBuilder(request.getUri());
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            uriBuilder.addParameter(entry.getKey(), entry.getValue());
        }
        try {
            req.setURI(uriBuilder.build());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
