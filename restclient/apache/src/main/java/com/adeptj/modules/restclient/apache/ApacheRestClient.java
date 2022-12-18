package com.adeptj.modules.restclient.apache;

import com.adeptj.modules.restclient.core.AbstractRestClient;
import com.adeptj.modules.restclient.core.ClientRequest;
import com.adeptj.modules.restclient.core.ClientResponse;
import com.adeptj.modules.restclient.core.RestClient;
import com.adeptj.modules.restclient.core.RestClientException;
import com.adeptj.modules.restclient.core.plugin.AuthorizationHeaderPlugin;
import org.apache.commons.lang3.StringUtils;
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
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

@Designate(ocd = ApacheHttpClientConfig.class)
@Component(service = RestClient.class)
public class ApacheRestClient extends AbstractRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String SCHEME_HTTPS = "https";

    private static final String SCHEME_HTTP = "http";

    private final CloseableHttpClient httpClient;

    private final ScheduledExecutorService executorService;

    @Activate
    public ApacheRestClient(@NotNull ApacheHttpClientConfig config) {
        super(config.debug_request(), config.mdc_req_id_attribute_name());
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        PoolingHttpClientConnectionManager connectionManager = this.getConnectionManager(config);
        this.httpClient = this.initHttpClient(connectionManager, config);
        int idleTimeout = config.idle_timeout();
        int initialDelay = idleTimeout * 2;
        HttpClientIdleConnectionEvictor evictor = new HttpClientIdleConnectionEvictor(idleTimeout, connectionManager);
        this.executorService.scheduleAtFixedRate(evictor, initialDelay, idleTimeout, SECONDS);
        LOGGER.info("Apache HttpClient Started!");
    }

    @Override
    protected <T, R> @NotNull ClientResponse<R> doExecuteRequest(ClientRequest<T, R> request) {
        HttpUriRequest apacheRequest = ApacheRequestFactory.newRequest(request);
        this.addAuthorizationHeader(apacheRequest);
        ClientResponse<R> response;
        try {
            if (this.debugRequest) {
                response = this.executeRequestDebug(request, apacheRequest);
            } else {
                response = this.httpClient.execute(apacheRequest, new HttpResponseHandler<>(request.getResponseAs()));
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new RestClientException(ex);
        }
        return response;
    }

    private void addAuthorizationHeader(@NotNull HttpUriRequest request) {
        String authorizationHeaderValue = this.getAuthorizationHeaderValue(request.getURI().getPath());
        if (StringUtils.isNotEmpty(authorizationHeaderValue)) {
            request.addHeader(AUTHORIZATION, authorizationHeaderValue);
        }
    }

    private <T, R> @NotNull ClientResponse<R> executeRequestDebug(@NotNull ClientRequest<T, R> cr,
                                                                  HttpUriRequest ar) throws IOException {
        String reqId = this.getReqId();
        ApacheRestClientLogger.logRequest(reqId, ar);
        AtomicLong startTime = new AtomicLong(System.nanoTime());
        try (CloseableHttpResponse response = this.httpClient.execute(ar)) {
            long executionTime = startTime.updateAndGet(time -> (System.nanoTime() - time));
            ClientResponse<R> clientResponse = ClientResponseFactory.newClientResponse(response, cr.getResponseAs());
            ApacheRestClientLogger.logResponse(reqId, clientResponse, executionTime);
            EntityUtils.consumeQuietly(response.getEntity());
            return clientResponse;
        }
    }

    @Override
    public <T> T unwrap(@NotNull Class<T> type) {
        if (type.isInstance(this.httpClient)) {
            return type.cast(this.httpClient);
        }
        return null;
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    /**
     * Initialize the Apache {@link CloseableHttpClient} with a pooling connection manager.
     *
     * @param cm     the {@link PoolingHttpClientConnectionManager}
     * @param config the {@link ApacheHttpClientConfig}.
     */
    private CloseableHttpClient initHttpClient(PoolingHttpClientConnectionManager cm, @NotNull ApacheHttpClientConfig config) {
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setConnectTimeout(config.connect_timeout())
                .setConnectionRequestTimeout(config.connection_request_timeout())
                .setSocketTimeout(config.socket_timeout())
                .build();
        HttpClientBuilder clientBuilder = HttpClients.custom()
                .setConnectionManager(cm)
                .setKeepAliveStrategy(new HttpClientConnectionKeepAliveStrategy(config.max_idle_time()))
                .setDefaultRequestConfig(defaultRequestConfig);
        if (config.disable_cookie_management()) {
            clientBuilder.disableCookieManagement();
        }
        return clientBuilder.build();
    }

    private @NotNull PoolingHttpClientConnectionManager getConnectionManager(@NotNull ApacheHttpClientConfig config) {
        PoolingHttpClientConnectionManager connectionManager;
        if (config.skip_hostname_verification()) {
            try {
                SSLContext sslContext = SSLContexts.custom()
                        .loadTrustMaterial(null, TrustSelfSignedStrategy.INSTANCE)
                        .build();
                Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                        .register(SCHEME_HTTPS, new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE))
                        .register(SCHEME_HTTP, new PlainConnectionSocketFactory())
                        .build();
                connectionManager = new PoolingHttpClientConnectionManager(registry);
            } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException ex) {
                LOGGER.error(ex.getMessage(), ex);
                throw new IllegalStateException(ex);
            }
        } else {
            connectionManager = new PoolingHttpClientConnectionManager();
        }
        connectionManager.setDefaultMaxPerRoute(config.max_per_route());
        connectionManager.setMaxTotal(config.max_total());
        connectionManager.setValidateAfterInactivity(config.validate_after_inactivity());
        return connectionManager;
    }

    // <<----------------------------------------- OSGi Internal  ------------------------------------------>>

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
        this.doBindAuthorizationHeaderPlugin(plugin);
    }

    protected void unbindAuthorizationHeaderPlugin(AuthorizationHeaderPlugin plugin) {
        this.doUnbindAuthorizationHeaderPlugin(plugin);
    }
}
