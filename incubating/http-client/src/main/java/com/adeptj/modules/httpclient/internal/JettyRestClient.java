package com.adeptj.modules.httpclient.internal;

import com.adeptj.modules.httpclient.ClientRequest;
import com.adeptj.modules.httpclient.ClientResponse;
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
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.adeptj.modules.httpclient.RestClientConstants.CONTENT_TYPE_JSON;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static java.nio.charset.StandardCharsets.UTF_8;

@Designate(ocd = JettyHttpClientConfig.class)
@Component
public class JettyRestClient implements RestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(INDENT_OUTPUT)
            .disable(WRITE_DATES_AS_TIMESTAMPS)
            .setSerializationInclusion(NON_NULL)
            .setDefaultPropertyInclusion(NON_DEFAULT);

    private final HttpClient jettyClient;

    @Activate
    public JettyRestClient(JettyHttpClientConfig config) {
        this.jettyClient = new HttpClient();
        this.jettyClient.setName(config.name());
        this.jettyClient.setConnectTimeout(config.connectTimeout());
        this.jettyClient.setIdleTimeout(config.idleTimeout());
        this.jettyClient.setMaxConnectionsPerDestination(config.maxConnectionsPerDestination());
        this.jettyClient.setMaxRequestsQueuedPerDestination(config.maxRequestsQueuedPerDestination());
        this.jettyClient.setAddressResolutionTimeout(config.addressResolutionTimeout());
        this.jettyClient.setMaxRedirects(config.maxRedirects());
        this.jettyClient.setRequestBufferSize(config.requestBufferSize());
        this.jettyClient.setResponseBufferSize(config.responseBufferSize());
        this.jettyClient.setTCPNoDelay(config.tcpNoDelay());
        LOGGER.info("Starting Jetty HttpClient!");
        try {
            this.jettyClient.start();
        } catch (Exception ex) {
            throw new HttpClientInitializationException(ex);
        }
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
        return this.toClientResponse(cr, responseType);
    }

    @Override
    public <T> T doWithHttpClient(Function<HttpClient, T> function) {
        return function.apply(this.jettyClient);
    }

    @Override
    public void doWithHttpClient(Consumer<HttpClient> consumer) {
        consumer.accept(this.jettyClient);
    }

    private <T> ClientResponse<T> toClientResponse(ContentResponse cr, Class<T> responseType) {
        ClientResponse<T> response = new ClientResponse<>();
        response.setStatus(cr.getStatus());
        response.setReason(cr.getReason());
        if (cr.getHeaders().size() > 0) {
            Map<String, String> headers = new HashMap<>();
            cr.getHeaders().forEach(f -> headers.put(f.getName(), f.getValue()));
            response.setHeaders(headers);
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
        Request request = this.jettyClient.newRequest(cr.getUri()).method(httpMethod.toString());
        Map<String, String> headers = cr.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            request.headers(m -> headers.forEach(m::add));
        }
        Map<String, String> queryParams = cr.getQueryParams();
        if (queryParams != null && !queryParams.isEmpty()) {
            queryParams.forEach(request::param);
        }
        if (StringUtils.isNotEmpty(cr.getBody())) {
            request.body(new StringRequestContent(CONTENT_TYPE_JSON, cr.getBody()));
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

    // <<----------------------------------------- OSGi Internal  ------------------------------------------>>

    @Deactivate
    protected void stop() {
        LOGGER.info("Stopping Jetty HttpClient!");
        try {
            this.jettyClient.stop();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
