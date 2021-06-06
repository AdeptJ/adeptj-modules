package com.adeptj.modules.httpclient.internal;

import com.adeptj.modules.httpclient.ClientRequest;
import com.adeptj.modules.httpclient.ClientResponse;
import com.adeptj.modules.httpclient.ClientResponseFactory;
import com.adeptj.modules.httpclient.HttpMethod;
import com.adeptj.modules.httpclient.JettyRequestFactory;
import com.adeptj.modules.httpclient.RestClient;
import com.adeptj.modules.httpclient.RestClientException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.Validate;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.adeptj.modules.httpclient.HttpMethod.DELETE;
import static com.adeptj.modules.httpclient.HttpMethod.GET;
import static com.adeptj.modules.httpclient.HttpMethod.POST;
import static com.adeptj.modules.httpclient.HttpMethod.PUT;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

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
        request.setHttpMethod(GET);
        return this.executeRequest(request, responseType);
    }

    @Override
    public <T> ClientResponse<T> POST(ClientRequest request, Class<T> responseType) {
        request.setHttpMethod(POST);
        return this.executeRequest(request, responseType);
    }

    @Override
    public <T> ClientResponse<T> PUT(ClientRequest request, Class<T> responseType) {
        request.setHttpMethod(PUT);
        return this.executeRequest(request, responseType);
    }

    @Override
    public <T> ClientResponse<T> DELETE(ClientRequest request, Class<T> responseType) {
        request.setHttpMethod(DELETE);
        return this.executeRequest(request, responseType);
    }

    @Override
    public <T> ClientResponse<T> executeRequest(ClientRequest request, Class<T> responseType) {
        Validate.isTrue((request.getHttpMethod() != null), "HttpMethod can't be null");
        return this.doExecuteRequest(request.getHttpMethod(), request, responseType);
    }

    @Override
    public <T> T doWithHttpClient(Function<HttpClient, T> function) {
        return function.apply(this.jettyClient);
    }

    @Override
    public void doWithHttpClient(Consumer<HttpClient> consumer) {
        consumer.accept(this.jettyClient);
    }

    private <T> ClientResponse<T> doExecuteRequest(HttpMethod httpMethod, ClientRequest cr, Class<T> responseType) {
        Request request = JettyRequestFactory.newRequest(this.jettyClient, cr, httpMethod);
        try {
            ContentResponse response = request.send();
            return ClientResponseFactory.newClientResponse(response, responseType, MAPPER);
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
