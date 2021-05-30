package com.adeptj.modules.httpclient.internal;

import com.adeptj.modules.httpclient.HttpClientProvider;
import org.eclipse.jetty.client.HttpClient;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

@Designate(ocd = JettyHttpClientConfig.class)
@Component
public class JettyHttpClientProvider implements HttpClientProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final HttpClient jettyClient;

    @Activate
    public JettyHttpClientProvider(JettyHttpClientConfig config) {
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
    public HttpClient getHttpClient() {
        return this.jettyClient;
    }

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
