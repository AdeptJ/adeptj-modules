package com.adeptj.modules.restclient.apache;

import com.adeptj.modules.restclient.core.AbstractRestClient;
import com.adeptj.modules.restclient.core.ClientRequest;
import com.adeptj.modules.restclient.core.ClientResponse;
import com.adeptj.modules.restclient.core.RestClient;
import com.adeptj.modules.restclient.core.RestClientException;
import com.adeptj.modules.restclient.core.plugin.AuthorizationHeaderPlugin;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import static com.adeptj.modules.restclient.core.HttpMethod.DELETE;
import static com.adeptj.modules.restclient.core.HttpMethod.GET;
import static com.adeptj.modules.restclient.core.HttpMethod.POST;
import static com.adeptj.modules.restclient.core.HttpMethod.PUT;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

@Designate(ocd = ApacheHttpClientConfig.class)
@Component(service = RestClient.class, configurationPolicy = REQUIRE)
public class ApacheRestClient extends AbstractRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String SCHEME_HTTPS = "https";

    private static final String SCHEME_HTTP = "http";

    private CloseableHttpClient httpClient;

    private ScheduledExecutorService executorService;

    private final boolean debugRequest;

    private final String mdcReqIdAttrName;

    private final List<AuthorizationHeaderPlugin> authorizationHeaderPlugins;

    @Activate
    public ApacheRestClient(ApacheHttpClientConfig config) {
        if (config.skip_hostname_verification()) {
            try {
                SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, TrustSelfSignedStrategy.INSTANCE)
                        .build();
                Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                        .register(SCHEME_HTTPS, new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE))
                        .register(SCHEME_HTTP, new PlainConnectionSocketFactory())
                        .build();
                this.initHttpClient(new PoolingHttpClientConnectionManager(socketFactoryRegistry), config);
            } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException ex) {
                LOGGER.error(ex.getMessage(), ex);
                throw new IllegalStateException(ex);
            }
        } else {
            this.initHttpClient(new PoolingHttpClientConnectionManager(), config);
        }
        this.debugRequest = config.debug_request();
        this.mdcReqIdAttrName = config.mdc_req_id_attribute_name();
        this.authorizationHeaderPlugins = new CopyOnWriteArrayList<>();
    }

    @Override
    public <T, R> ClientResponse<R> GET(ClientRequest<T, R> request) {
        this.ensureHttpMethod(request, GET);
        return this.executeRequest(request);
    }

    @Override
    public <T, R> ClientResponse<R> POST(ClientRequest<T, R> request) {
        this.ensureHttpMethod(request, POST);
        return this.executeRequest(request);
    }

    @Override
    public <T, R> ClientResponse<R> PUT(ClientRequest<T, R> request) {
        this.ensureHttpMethod(request, PUT);
        return this.executeRequest(request);
    }

    @Override
    public <T, R> ClientResponse<R> DELETE(ClientRequest<T, R> request) {
        this.ensureHttpMethod(request, DELETE);
        return this.executeRequest(request);
    }

    @Override
    public <T, R> ClientResponse<R> executeRequest(ClientRequest<T, R> request) {
        if (request.getMethod() == null) {
            throw new IllegalStateException("No HttpMethod set in the ClientRequest!!");
        }
        if (this.debugRequest) {
            return this.doExecuteRequestDebug(request);
        }
        return this.doExecuteRequest(request);
    }

    @Override
    public Object unwrap() {
        return this.httpClient;
    }

    private <T, R> @NotNull ClientResponse<R> doExecuteRequestDebug(ClientRequest<T, R> request) {
        HttpUriRequest apacheRequest = ApacheRequestFactory.newRequest(request);
        this.addAuthorizationHeader(apacheRequest);
        String reqId = ApacheRestClientLogger.logRequest(apacheRequest, this.mdcReqIdAttrName);
        AtomicLong startTime = new AtomicLong(System.nanoTime());
        try (CloseableHttpResponse response = this.httpClient.execute(apacheRequest)) {
            long executionTime = startTime.updateAndGet(time -> (System.nanoTime() - time));
            ClientResponse<R> resp = ClientResponseFactory.newClientResponse(response, request.getResponseAs());
            ApacheRestClientLogger.logResponse(reqId, resp, executionTime);
            return resp;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new RestClientException(ex);
        }
    }

    private <T, R> @NotNull ClientResponse<R> doExecuteRequest(ClientRequest<T, R> request) {
        HttpUriRequest apacheRequest = ApacheRequestFactory.newRequest(request);
        this.addAuthorizationHeader(apacheRequest);
        try (CloseableHttpResponse response = this.httpClient.execute(apacheRequest)) {
            return ClientResponseFactory.newClientResponse(response, request.getResponseAs());
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new RestClientException(ex);
        }
    }

    private void addAuthorizationHeader(HttpUriRequest request) {
        // Create a temp var because the service is dynamic.
        List<AuthorizationHeaderPlugin> plugins = this.authorizationHeaderPlugins;
        if (plugins.isEmpty()) {
            return;
        }
        AuthorizationHeaderPlugin plugin = this.resolveAuthorizationHeaderPlugin(plugins, request.getURI().getPath());
        if (plugin != null) {
            request.addHeader(AUTHORIZATION, (plugin.getType() + " " + plugin.getValue()));
            LOGGER.info("Authorization header added to request [{}] by plugin [{}]", request.getURI(), plugin);
        }
    }

    // <<----------------------------------------- OSGi Internal  ------------------------------------------>>

    /**
     * Initialize the Apache {@link CloseableHttpClient} with a pooling connection manager.
     *
     * @param connectionManager the {@link PoolingHttpClientConnectionManager}.
     */
    private void initHttpClient(PoolingHttpClientConnectionManager connectionManager, ApacheHttpClientConfig config) {
        connectionManager.setDefaultMaxPerRoute(config.max_per_route());
        connectionManager.setMaxTotal(config.max_total());
        connectionManager.setValidateAfterInactivity(config.validate_after_inactivity());
        this.httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setKeepAliveStrategy(new HttpClientConnectionKeepAliveStrategy(config.max_idle_time()))
                .disableCookieManagement()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(config.connect_timeout())
                        .setConnectionRequestTimeout(config.connection_request_timeout())
                        .setSocketTimeout(config.socket_timeout())
                        .build())
                .build();
        int idleTimeout = config.idle_timeout();
        int initialDelay = idleTimeout * 2;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.executorService.scheduleAtFixedRate(new HttpClientIdleConnectionEvictor(idleTimeout, connectionManager),
                initialDelay,
                idleTimeout,
                SECONDS);
    }

    @Deactivate
    protected void stop() {
        LOGGER.info("Stopping Apache HttpClient!");
        try {
            this.executorService.shutdown();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        try {
            this.httpClient.close();
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @Reference(service = AuthorizationHeaderPlugin.class, cardinality = MULTIPLE, policy = DYNAMIC)
    protected void bindAuthorizationHeaderPlugin(AuthorizationHeaderPlugin plugin) {
        LOGGER.info("Binding AuthorizationHeaderPlugin: {}", plugin);
        this.authorizationHeaderPlugins.add(plugin);
    }

    protected void unbindAuthorizationHeaderPlugin(AuthorizationHeaderPlugin plugin) {
        if (this.authorizationHeaderPlugins.remove(plugin)) {
            LOGGER.info("Unbounded AuthorizationHeaderPlugin: {}", plugin);
        }
    }
}
