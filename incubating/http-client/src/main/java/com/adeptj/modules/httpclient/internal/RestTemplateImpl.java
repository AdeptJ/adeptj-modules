package com.adeptj.modules.httpclient.internal;

import com.adeptj.modules.httpclient.HttpClientCallback;
import com.adeptj.modules.httpclient.HttpClientProvider;
import com.adeptj.modules.httpclient.RestTemplate;
import com.adeptj.modules.httpclient.RestClientException;
import org.eclipse.jetty.client.api.Request;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.eclipse.jetty.http.HttpMethod.GET;

@Component
public class RestTemplateImpl implements RestTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final HttpClientProvider hcp;

    @Activate
    public RestTemplateImpl(@Reference HttpClientProvider hcp) {
        this.hcp = hcp;
    }

    @Override
    public byte[] GET(URI uri, Map<String, String> headers) {
        Request request = this.hcp.getHttpClient().newRequest(uri).method(GET);
        if (headers != null && !headers.isEmpty()) {
            request.headers(m -> headers.forEach(m::add));
        }
        try {
            return request.send().getContent();
        } catch (InterruptedException | TimeoutException | ExecutionException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new RestClientException(ex);
        }
    }

    @Override
    public <T> T GET(URI uri, Class<T> responseType, Map<String, String> headers) {
        byte[] bytes = this.GET(uri, headers);
        return responseType.cast(new String(bytes));
    }

    @Override
    public <T> T executeCallback(HttpClientCallback<T> callback) {
        return callback.doWithHttpClient(this.hcp.getHttpClient());
    }
}
