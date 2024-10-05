/*
###############################################################################
#                                                                             #
#    Copyright 2016-2024, AdeptJ (http://www.adeptj.com)                      #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/
package com.adeptj.modules.restclient.apache.internal;

import com.adeptj.modules.restclient.apache.handler.HttpResponseHandler;
import com.adeptj.modules.restclient.apache.housekeeping.HttpClientConnectionKeepAliveStrategy;
import com.adeptj.modules.restclient.apache.housekeeping.HttpClientIdleConnectionEvictor;
import com.adeptj.modules.restclient.apache.util.ApacheRequestFactory;
import com.adeptj.modules.restclient.apache.util.ApacheRestClientLogger;
import com.adeptj.modules.restclient.apache.util.ClientResponseFactory;
import com.adeptj.modules.restclient.core.AbstractRestClient;
import com.adeptj.modules.restclient.core.ClientRequest;
import com.adeptj.modules.restclient.core.ClientResponse;
import com.adeptj.modules.restclient.core.RestClient;
import com.adeptj.modules.restclient.core.RestClientException;
import com.adeptj.modules.restclient.core.RestClientInitializationException;
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
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

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

/**
 * The RestClient implementation based on Apache HttpComponent's {@link CloseableHttpClient}.
 *
 * @author Rakesh Kumar, AdeptJ
 */
@Designate(ocd = ApacheRestClient.ApacheHttpClientConfig.class)
@Component(service = RestClient.class)
public class ApacheRestClient extends AbstractRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String SCHEME_HTTPS = "https";

    private static final String SCHEME_HTTP = "http";

    private final CloseableHttpClient httpClient;

    private final ScheduledExecutorService executorService;

    private final HttpClientIdleConnectionEvictor evictor;

    @Activate
    public ApacheRestClient(@NotNull ApacheHttpClientConfig config) {
        super(config.debug_request(), config.mdc_req_id_attribute_name());
        try {
            PoolingHttpClientConnectionManager connectionManager = this.getConnectionManager(config);
            this.httpClient = this.initHttpClient(connectionManager, config);
            int idleTimeout = config.idle_timeout();
            int initialDelay = idleTimeout * 2; // 2 minutes
            this.evictor = new HttpClientIdleConnectionEvictor(idleTimeout, connectionManager);
            this.executorService = Executors.newSingleThreadScheduledExecutor();
            this.executorService.scheduleAtFixedRate(this.evictor, initialDelay, idleTimeout, SECONDS);
            LOGGER.info("Apache HttpClient Started!");
        } catch (Exception ex) { // NOSONAR
            throw new RestClientInitializationException(ex);
        }
    }

    @Override
    protected <T, R> @NotNull ClientResponse<R> doExecuteRequest(@NotNull ClientRequest<T, R> request) {
        HttpUriRequest apacheRequest = ApacheRequestFactory.newRequest(request);
        this.addAuthorizationHeader(apacheRequest);
        ClientResponse<R> response;
        try {
            if (this.debugRequest) {
                response = this.executeRequestDebug(request, apacheRequest);
            } else {
                response = this.httpClient.execute(apacheRequest, new HttpResponseHandler<>(request.getResponseAs()));
            }
        } catch (Exception ex) { // NOSONAR
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

    private <T, R> @NotNull ClientResponse<R> executeRequestDebug(@NotNull ClientRequest<T, R> clientRequest,
                                                                  HttpUriRequest request) throws IOException {
        String requestId = this.getRequestId();
        ApacheRestClientLogger.logRequest(requestId, request);
        AtomicLong startTime = new AtomicLong(System.nanoTime());
        try (CloseableHttpResponse response = this.httpClient.execute(request)) {
            long executionTime = startTime.updateAndGet(time -> (System.nanoTime() - time));
            ClientResponse<R> clientResponse =
                    ClientResponseFactory.newResponse(response, clientRequest.getResponseAs());
            ApacheRestClientLogger.logResponse(requestId, clientResponse, executionTime);
            EntityUtils.consumeQuietly(response.getEntity());
            return clientResponse;
        } finally {
            MDC.remove(this.mdcReqIdAttrName);
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
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
        }
        this.evictor.unsetConnectionManager();
        try {
            this.httpClient.close();
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * Apache HttpClient configurations.
     *
     * @author Rakesh Kumar, AdeptJ
     */
    @ObjectClassDefinition(
            name = "AdeptJ Apache HttpClient Configuration",
            description = "AdeptJ Apache HttpClient Configuration"
    )
    public @interface ApacheHttpClientConfig {

        @AttributeDefinition(
                name = "Skip HostName Verification",
                description = "Whether to skip HostName verification"
        )
        boolean skip_hostname_verification() default true;

        @AttributeDefinition(
                name = "Disable Cookie Management",
                description = "Whether to disable cookie management"
        )
        boolean disable_cookie_management();

        @AttributeDefinition(
                name = "Connect Timeout",
                description = "HttpClient request connect timeout (milliseconds)."
        )
        int connect_timeout() default 300000; // Caller has option to override this using RequestConfig

        @AttributeDefinition(
                name = "Connection Request Timeout",
                description = "HttpClient connection request timeout (milliseconds)."
        )
        int connection_request_timeout() default 60000;

        @AttributeDefinition(
                name = "Socket Timeout",
                description = "HttpClient request socket timeout (milliseconds)."
        )
        int socket_timeout() default 300000; // Caller has option to override this using RequestConfig

        // <----------------------------- HttpClient ConnectionPool Settings ----------------------------->

        @AttributeDefinition(
                name = "HttpClient ConnectionPool Max Idle Time",
                description = "Maximum time a connection is kept alive in HttpClient ConnectionPool (milliseconds)."
        )
        long max_idle_time() default 60000;

        @AttributeDefinition(
                name = "HttpClient ConnectionPool Max Connections",
                description = "Maximum number of connections in HttpClient ConnectionPool."
        )
        int max_total() default 100;

        @AttributeDefinition(
                name = "HttpClient ConnectionPool Idle Timeout",
                description = "Maximum time a connection remains idle in HttpClient ConnectionPool (seconds)."
        )
        int idle_timeout() default 60;

        @AttributeDefinition(
                name = "HttpClient ConnectionPool Max Connections Per Route",
                description = "Maximum number of default connections per host."
        )
        int max_per_route() default 10;

        @AttributeDefinition(
                name = "HttpClient ConnectionPool Inactive Connection Validation Time",
                description = "Time interval for validating the connection after inactivity (milliseconds)."
        )
        int validate_after_inactivity() default 5000;

        // <-------------------------------------------- RestClient Settings -------------------------------------------->

        @AttributeDefinition(
                name = "Debug Request",
                description = "Debug for detecting any issues with the request execution. Please keep it disabled on production systems."
        )
        boolean debug_request();

        @AttributeDefinition(
                name = "SLF4J MDC Request Attribute Name",
                description = "The attribute might already been setup by application during initial request processing."
        )
        String mdc_req_id_attribute_name() default "APACHE_HTTP_CLIENT_REQ_ID";
    }
}
