package com.adeptj.modules.restclient.ahc;

import com.adeptj.modules.restclient.core.ClientRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtils {

    public static @NotNull List<NameValuePair> createNameValuePairs(@NotNull Map<String, String> params) {
        List<NameValuePair> pairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return pairs;
    }

    public static <T, R> void addHeaders(ClientRequest<T, R> request, HttpRequestBase apacheRequest) {
        Map<String, String> headers = request.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                apacheRequest.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    public static <T, R> void handleUri(@NotNull ClientRequest<T, R> request, HttpRequestBase apacheRequest) {
        Map<String, String> queryParams = request.getQueryParams();
        if (queryParams == null || queryParams.isEmpty()) {
            apacheRequest.setURI(request.getUri());
            return;
        }
        try {
            URI uri = new URIBuilder(request.getUri())
                    .addParameters(HttpClientUtils.createNameValuePairs(queryParams))
                    .build();
            apacheRequest.setURI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
