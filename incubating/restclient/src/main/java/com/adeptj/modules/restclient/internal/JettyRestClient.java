package com.adeptj.modules.restclient.internal;

import com.adeptj.modules.restclient.ClientRequest;
import com.adeptj.modules.restclient.ClientResponse;
import com.adeptj.modules.restclient.ClientResponseFactory;
import com.adeptj.modules.restclient.JettyRequestFactory;
import com.adeptj.modules.restclient.RestClient;
import com.adeptj.modules.restclient.RestClientException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.adeptj.modules.restclient.HttpMethod.DELETE;
import static com.adeptj.modules.restclient.HttpMethod.GET;
import static com.adeptj.modules.restclient.HttpMethod.POST;
import static com.adeptj.modules.restclient.HttpMethod.PUT;
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
        this.jettyClient.setConnectTimeout(config.connect_timeout());
        this.jettyClient.setIdleTimeout(config.idle_timeout());
        this.jettyClient.setMaxConnectionsPerDestination(config.max_connections_per_destination());
        this.jettyClient.setMaxRequestsQueuedPerDestination(config.max_requests_queued_per_destination());
        this.jettyClient.setAddressResolutionTimeout(config.address_resolution_timeout());
        this.jettyClient.setMaxRedirects(config.max_redirects());
        this.jettyClient.setRequestBufferSize(config.request_buffer_size());
        this.jettyClient.setResponseBufferSize(config.response_buffer_size());
        this.jettyClient.setTCPNoDelay(config.tcp_no_delay());
        LOGGER.info("Starting Jetty HttpClient!");
        try {
            this.jettyClient.start();
        } catch (Exception ex) {
            throw new HttpClientInitializationException(ex);
        }
    }

    @Override
    public <T, R> ClientResponse<R> GET(ClientRequest<T, R> request) {
        request.setHttpMethod(GET);
        return this.executeRequest(request);
    }

    @Override
    public <T, R> ClientResponse<R> POST(ClientRequest<T, R> request) {
        request.setHttpMethod(POST);
        return this.executeRequest(request);
    }

    @Override
    public <T, R> ClientResponse<R> PUT(ClientRequest<T, R> request) {
        request.setHttpMethod(PUT);
        return this.executeRequest(request);
    }

    @Override
    public <T, R> ClientResponse<R> DELETE(ClientRequest<T, R> request) {
        request.setHttpMethod(DELETE);
        return this.executeRequest(request);
    }

    @Override
    public <T, R> ClientResponse<R> executeRequest(ClientRequest<T, R> request) {
        try {
            ContentResponse response = JettyRequestFactory.newRequest(this.jettyClient, request, MAPPER)
                    .send();
            return ClientResponseFactory.newClientResponse(response, request.getResponseType(), MAPPER);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new RestClientException(ex);
        }
    }

    @Override
    public <T> T doWithHttpClient(Function<HttpClient, T> function) {
        return function.apply(this.jettyClient);
    }

    @Override
    public void doWithHttpClient(Consumer<HttpClient> consumer) {
        consumer.accept(this.jettyClient);
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
