package com.adeptj.modules.httpclient.internal;

import com.adeptj.modules.httpclient.ClientRequest;
import com.adeptj.modules.httpclient.ClientResponse;
import com.adeptj.modules.httpclient.HttpClientProvider;
import com.adeptj.modules.httpclient.HttpMethod;
import com.adeptj.modules.httpclient.RestClient;
import com.adeptj.modules.httpclient.RestClientException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.FormRequestContent;
import org.eclipse.jetty.client.util.StringRequestContent;
import org.eclipse.jetty.util.Fields;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class JettyRestClient implements RestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(INDENT_OUTPUT)
            .disable(WRITE_DATES_AS_TIMESTAMPS)
            .setSerializationInclusion(NON_NULL)
            .setDefaultPropertyInclusion(NON_DEFAULT);

    private final HttpClientProvider hcp;

    @Activate
    public JettyRestClient(@Reference HttpClientProvider hcp) {
        this.hcp = hcp;
    }

    @Override
    public <T> ClientResponse<T> GET(ClientRequest request, Class<T> responseType) {
        request.setHttpMethod(HttpMethod.GET);
        return this.executeRequest(request, responseType);
    }

    @Override
    public <T> ClientResponse<T> POST(ClientRequest request, Class<T> responseType) {
        request.setHttpMethod(HttpMethod.POST);
        return this.executeRequest(request, responseType);
    }

    @Override
    public <T> ClientResponse<T> PUT(ClientRequest request, Class<T> responseType) {
        request.setHttpMethod(HttpMethod.PUT);
        return this.executeRequest(request, responseType);
    }

    @Override
    public <T> ClientResponse<T> DELETE(ClientRequest request, Class<T> responseType) {
        request.setHttpMethod(HttpMethod.DELETE);
        return this.executeRequest(request, responseType);
    }

    @Override
    public <T> ClientResponse<T> executeRequest(ClientRequest request, Class<T> responseType) {
        Validate.isTrue((request.getHttpMethod() != null), "HttpMethod can't be null");
        ContentResponse cr = this.doExecuteRequest(request, request.getHttpMethod());
        return this.doPrepareResponse(cr, responseType);
    }

    @Override
    public <T> T doWithHttpClient(Function<HttpClient, T> function) {
        return function.apply(this.hcp.getHttpClient());
    }

    @Override
    public void doWithHttpClient(Consumer<HttpClient> consumer) {
        consumer.accept(this.hcp.getHttpClient());
    }

    private <T> ClientResponse<T> doPrepareResponse(ContentResponse cr, Class<T> responseType) {
        ClientResponse<T> response = new ClientResponse<>();
        response.setStatus(cr.getStatus());
        response.setReason(cr.getReason());
        if (cr.getHeaders().size() > 0) {
            Map<String, String> h = new HashMap<>();
            cr.getHeaders().forEach(f -> h.put(f.getName(), f.getValue()));
            response.setHeaders(h);
        }
        if (responseType.equals(void.class)) {
            return response;
        }
        if (responseType.equals(byte[].class)) {
            response.setContent(responseType.cast(cr.getContent()));
        } else if (responseType.equals(String.class)) {
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

    private ContentResponse doExecuteRequest(ClientRequest cr, HttpMethod httpMethod) {
        Request request = this.hcp.getHttpClient().newRequest(cr.getUri());
        request.method(httpMethod.toString());
        Map<String, String> headers = cr.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            request.headers(m -> headers.forEach(m::add));
        }
        Map<String, String> queryParams = cr.getQueryParams();
        if (queryParams != null && !queryParams.isEmpty()) {
            queryParams.forEach(request::param);
        }
        String body = cr.getPayload();
        if (StringUtils.isNotEmpty(body)) {
            request.body(new StringRequestContent("application/json", body, UTF_8));
        } else {
            Map<String, String> formParams = cr.getFormParams();
            if (formParams != null && !formParams.isEmpty()) {
                Fields fields = new Fields();
                formParams.forEach(fields::put);
                request.body(new FormRequestContent(fields));
            }
        }
        try {
            return request.send();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new RestClientException(ex);
        }
    }
}
