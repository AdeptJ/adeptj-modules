package com.adeptj.modules.restclient.internal;

import com.adeptj.modules.restclient.AntPathMatcher;
import com.adeptj.modules.restclient.RestClientException;
import com.adeptj.modules.restclient.api.ClientRequest;
import com.adeptj.modules.restclient.api.ClientResponse;
import com.adeptj.modules.restclient.api.RestClient;
import com.adeptj.modules.restclient.plugin.AuthorizationHeaderPlugin;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.adeptj.modules.restclient.api.HttpMethod.DELETE;
import static com.adeptj.modules.restclient.api.HttpMethod.GET;
import static com.adeptj.modules.restclient.api.HttpMethod.POST;
import static com.adeptj.modules.restclient.api.HttpMethod.PUT;
import static org.eclipse.jetty.http.HttpHeader.AUTHORIZATION;
import static org.osgi.service.component.annotations.ReferenceCardinality.OPTIONAL;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

@Designate(ocd = JettyHttpClientConfig.class)
@Component
public class JettyRestClient implements RestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final HttpClient jettyClient;

    private final boolean debugRequest;

    private final String mdcReqIdAttrName;

    private AuthorizationHeaderPlugin plugin;

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
        this.debugRequest = config.debug_request();
        this.mdcReqIdAttrName = config.mdc_req_id_attribute_name();
    }

    @Override
    public <T, R> ClientResponse<R> GET(ClientRequest<T, R> request) {
        return this.executeRequest(request.withMethod(GET));
    }

    @Override
    public <T, R> ClientResponse<R> POST(ClientRequest<T, R> request) {
        return this.executeRequest(request.withMethod(POST));
    }

    @Override
    public <T, R> ClientResponse<R> PUT(ClientRequest<T, R> request) {
        return this.executeRequest(request.withMethod(PUT));
    }

    @Override
    public <T, R> ClientResponse<R> DELETE(ClientRequest<T, R> request) {
        return this.executeRequest(request.withMethod(DELETE));
    }

    @Override
    public <T, R> ClientResponse<R> executeRequest(ClientRequest<T, R> request) {
        if (this.debugRequest) {
            return this.doExecuteRequestDebug(request);
        }
        return this.doExecuteRequest(request);
    }

    @Override
    public <T> T doWithHttpClient(Function<HttpClient, T> function) {
        return function.apply(this.jettyClient);
    }

    @Override
    public void doWithHttpClient(Consumer<HttpClient> consumer) {
        consumer.accept(this.jettyClient);
    }

    private <T, R> ClientResponse<R> doExecuteRequestDebug(ClientRequest<T, R> request) {
        try {
            Request jettyRequest = JettyRequestFactory.newRequest(this.jettyClient, request);
            this.addAuthorizationHeader(jettyRequest);
            String reqId = RestClientLogger.logRequest(request, jettyRequest, this.mdcReqIdAttrName);
            long startTime = System.nanoTime();
            ContentResponse response = jettyRequest.send();
            long endTime = System.nanoTime();
            long executionTime = endTime - startTime;
            RestClientLogger.logResponse(reqId, response, executionTime);
            return ClientResponseFactory.newClientResponse(response, request.getResponseAs());
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new RestClientException(ex);
        }
    }

    private <T, R> ClientResponse<R> doExecuteRequest(ClientRequest<T, R> request) {
        try {
            Request jettyRequest = JettyRequestFactory.newRequest(this.jettyClient, request);
            this.addAuthorizationHeader(jettyRequest);
            ContentResponse jettyResponse = jettyRequest.send();
            return ClientResponseFactory.newClientResponse(jettyResponse, request.getResponseAs());
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new RestClientException(ex);
        }
    }

    private void addAuthorizationHeader(Request request) {
        // Create a temp var because the service is dynamic.
        AuthorizationHeaderPlugin ahp = this.plugin;
        if (ahp == null || ahp.getPathPatterns().isEmpty()) {
            return;
        }
        AntPathMatcher matcher = AntPathMatcher.builder().build();
        for (String pattern : ahp.getPathPatterns()) {
            if (matcher.isMatch(pattern, request.getPath())) {
                request.headers(f -> f.add(AUTHORIZATION, ahp.getType() + " " + ahp.getValue()));
                break;
            }
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

    @Reference(service = AuthorizationHeaderPlugin.class, cardinality = OPTIONAL, policy = DYNAMIC)
    protected void bindAuthorizationHeaderPlugin(AuthorizationHeaderPlugin plugin) {
        LOGGER.info("Binding AuthorizationHeaderPlugin: {}", plugin);
        this.plugin = plugin;
    }

    protected void unbindAuthorizationHeaderPlugin(AuthorizationHeaderPlugin plugin) {
        LOGGER.info("Unbinding AuthorizationHeaderPlugin: {}", plugin);
        if (this.plugin == plugin) {
            this.plugin = null;
        }
    }
}
