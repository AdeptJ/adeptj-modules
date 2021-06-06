package com.adeptj.modules.httpclient.internal;

import com.adeptj.modules.httpclient.HttpClientCallback;
import com.adeptj.modules.httpclient.HttpClientProvider;
import com.adeptj.modules.httpclient.Response;
import com.adeptj.modules.httpclient.RestClientException;
import com.adeptj.modules.httpclient.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.eclipse.jetty.http.HttpMethod.GET;

@Component
public class RestTemplateImpl implements RestTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(INDENT_OUTPUT)
            .disable(WRITE_DATES_AS_TIMESTAMPS)
            .setSerializationInclusion(NON_NULL)
            .setDefaultPropertyInclusion(NON_DEFAULT);

    private final HttpClientProvider hcp;

    @Activate
    public RestTemplateImpl(@Reference HttpClientProvider hcp) {
        this.hcp = hcp;
    }

    private ContentResponse GET(URI uri, Map<String, String> headers) {
        Request request = this.hcp.getHttpClient().newRequest(uri).method(GET);
        if (headers != null && !headers.isEmpty()) {
            request.headers(m -> headers.forEach(m::add));
        }
        try {
            return request.send();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new RestClientException(ex);
        }
    }

    @Override
    public <T> Response<T> GET(URI uri, Class<T> responseType, Map<String, String> headers) {
        ContentResponse cr = this.GET(uri, headers);
        Response<T> response = new Response<>();
        response.setStatus(cr.getStatus());
        response.setReason(cr.getReason());
        if (cr.getHeaders().size() > 0) {
            Map<String, String> h = new HashMap<>();
            cr.getHeaders().forEach(f -> h.put(f.getName(), f.getValue()));
            response.setHeaders(h);
        }
        if (responseType.equals(String.class)) {
            response.setContent(responseType.cast(new String(cr.getContent(), UTF_8)));
        } else {
            try {
                response.setContent(MAPPER.reader().forType(responseType).readValue(cr.getContent()));
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage(), ex);
                throw new RestClientException(ex);
            }
        }
        return response;
    }

    @Override
    public <T> T executeCallback(HttpClientCallback<T> callback) {
        return callback.doWithHttpClient(this.hcp.getHttpClient());
    }
}
