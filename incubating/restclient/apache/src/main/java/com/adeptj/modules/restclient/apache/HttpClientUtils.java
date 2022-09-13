package com.adeptj.modules.restclient.apache;

import com.adeptj.modules.restclient.core.ClientRequest;
import com.adeptj.modules.restclient.core.util.ObjectMappers;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.adeptj.modules.restclient.core.HttpMethod.POST;
import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpClientUtils {

    static <T, R> EntityEnclosingRequest createEntityEnclosingRequest(ClientRequest<T, R> request) {
        EntityEnclosingRequest entityEnclosingRequest = new EntityEnclosingRequest(request.getMethod().toString());
        Map<String, String> formParams = request.getFormParams();
        T body = request.getBody();
        if (body != null) {
            String data = ObjectMappers.serialize(body);
            StringEntity entity = new StringEntity(data, UTF_8);
            entity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            entityEnclosingRequest.setEntity(entity);
        } else if (request.getMethod() == POST && formParams != null && !formParams.isEmpty()) {
            // Handle Form Post - application/x-www-form-urlencoded
            List<NameValuePair> params = HttpClientUtils.createNameValuePairs(formParams);
            entityEnclosingRequest.setEntity(new UrlEncodedFormEntity(params, UTF_8));
        }
        return entityEnclosingRequest;
    }

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
