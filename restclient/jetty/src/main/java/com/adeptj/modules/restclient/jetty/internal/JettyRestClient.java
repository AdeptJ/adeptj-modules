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
package com.adeptj.modules.restclient.jetty.internal;

import com.adeptj.modules.restclient.core.AbstractRestClient;
import com.adeptj.modules.restclient.core.ClientRequest;
import com.adeptj.modules.restclient.core.ClientResponse;
import com.adeptj.modules.restclient.core.RestClient;
import com.adeptj.modules.restclient.core.RestClientException;
import com.adeptj.modules.restclient.core.RestClientInitializationException;
import com.adeptj.modules.restclient.jetty.util.ClientResponseFactory;
import com.adeptj.modules.restclient.jetty.util.JettyRequestFactory;
import com.adeptj.modules.restclient.jetty.util.JettyRestClientLogger;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.client.ContentResponse;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.client.transport.HttpClientTransportOverHTTP;
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

import java.lang.invoke.MethodHandles;
import java.util.concurrent.atomic.AtomicLong;

import static org.eclipse.jetty.http.HttpHeader.AUTHORIZATION;

/**
 * The RestClient implementation based on Jetty {@link HttpClient}.
 *
 * @author Rakesh Kumar, AdeptJ
 */
@Designate(ocd = JettyRestClient.JettyHttpClientConfig.class)
@Component(service = RestClient.class)
public class JettyRestClient extends AbstractRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final HttpClient httpClient;

    @Activate
    public JettyRestClient(@NotNull JettyHttpClientConfig config) {
        super(config.debug_request(), config.mdc_req_id_attribute_name());
        try {
            this.httpClient = new HttpClient();
            this.httpClient.setFollowRedirects(config.follow_redirects());
            this.httpClient.setName(config.name());
            this.httpClient.setConnectTimeout(config.connect_timeout());
            this.httpClient.setIdleTimeout(config.idle_timeout());
            this.httpClient.setMaxConnectionsPerDestination(config.max_connections_per_destination());
            this.httpClient.setMaxRequestsQueuedPerDestination(config.max_requests_queued_per_destination());
            this.httpClient.setAddressResolutionTimeout(config.address_resolution_timeout());
            this.httpClient.setMaxRedirects(config.max_redirects());
            this.httpClient.setRequestBufferSize(config.request_buffer_size());
            this.httpClient.setResponseBufferSize(config.response_buffer_size());
            HttpClientTransportOverHTTP transport = (HttpClientTransportOverHTTP) this.httpClient.getTransport();
            transport.getClientConnector().setTCPNoDelay(config.tcp_no_delay());
            this.httpClient.start();
            LOGGER.info("Jetty HttpClient Started!");
        } catch (Exception ex) { // NOSONAR
            throw new RestClientInitializationException(ex);
        }
    }

    @Override
    protected <T, R> @NotNull ClientResponse<R> doExecuteRequest(@NotNull ClientRequest<T, R> request) {
        try {
            ContentResponse response;
            Request jettyRequest = JettyRequestFactory.newRequest(this.httpClient, request);
            this.addAuthorizationHeader(jettyRequest);
            if (this.debugRequest) {
                response = this.executeRequestDebug(request, jettyRequest);
            } else {
                response = jettyRequest.send();
            }
            return ClientResponseFactory.newResponse(response, request.getResponseAs());
        } catch (Exception ex) { // NOSONAR
            throw new RestClientException(ex);
        }
    }

    private void addAuthorizationHeader(@NotNull Request request) {
        String authorizationHeaderValue = this.getAuthorizationHeaderValue(request.getPath());
        if (StringUtils.isNotEmpty(authorizationHeaderValue)) {
            request.headers(f -> f.add(AUTHORIZATION, authorizationHeaderValue));
        }
    }

    private <T, R> ContentResponse executeRequestDebug(ClientRequest<T, R> cr, Request jettyRequest) throws Exception {
        try {
            String requestId = this.getRequestId();
            JettyRestClientLogger.logRequest(requestId, cr, jettyRequest);
            AtomicLong startTime = new AtomicLong(System.nanoTime());
            ContentResponse response = jettyRequest.send();
            long executionTime = startTime.updateAndGet(time -> (System.nanoTime() - time));
            JettyRestClientLogger.logResponse(requestId, response, executionTime);
            return response;
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

    // <<----------------------------------------- OSGi Internal  ------------------------------------------>>

    @Deactivate
    protected void stop() {
        LOGGER.info("Stopping Jetty HttpClient!");
        try {
            this.httpClient.stop();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * AdeptJ Jetty HttpClient configurations.
     *
     * @author Rakesh Kumar, AdeptJ.
     */
    @ObjectClassDefinition(
            name = "AdeptJ Jetty HttpClient Configuration",
            description = "AdeptJ Jetty HttpClient Configuration"
    )
    public static @interface JettyHttpClientConfig {

        @AttributeDefinition(
                name = "Jetty HttpClient Name",
                description = "Name to be used by Jetty QueuedThreadPool(QTP) for its name."
        )
        String name() default "AdeptJ Jetty HttpClient";

        @AttributeDefinition(
                name = "Jetty HttpClient Connect Timeout",
                description = "The max time, in milliseconds, a connection can take to connect to destinations. " +
                        "Zero value means infinite timeout."
        )
        long connect_timeout() default 60000L;

        @AttributeDefinition(
                name = "Jetty HttpClient Idle Timeout",
                description = "The max time, in milliseconds, " +
                        "a connection can be idle (that is, without traffic of bytes in either direction)"
        )
        long idle_timeout() default 60000L;

        @AttributeDefinition(
                name = "Jetty HttpClient Max Connections Per Destination",
                description = "Sets the max number of connections to open to each destinations. " +
                        "RFC 2616 suggests that 2 connections should be opened per each destination, " +
                        "but browsers commonly open 6."
        )
        int max_connections_per_destination() default 64;

        @AttributeDefinition(
                name = "Jetty HttpClient Max Requests Queued Per Destination",
                description = "Sets the max number of requests that may be queued to a destination. " +
                        "If this HttpClient performs a high rate of requests to a destination, and all the connections" +
                        "managed by that destination are busy with other requests, then new requests will be queued up in the destination. " +
                        "This parameter controls how many requests can be queued before starting to reject them."
        )
        int max_requests_queued_per_destination() default 1024;

        @AttributeDefinition(
                name = "Jetty HttpClient Request Buffer Size",
                description = "The size of the buffer used to write requests."
        )
        int request_buffer_size() default 4096; // 4k

        @AttributeDefinition(
                name = "Jetty HttpClient Response Buffer Size",
                description = "The size of the buffer used to read responses."
        )
        int response_buffer_size() default 16384; // 16k

        @AttributeDefinition(
                name = "Jetty HttpClient Max Redirects",
                description = "The max number of HTTP redirects that are followed in a conversation, or -1 for unlimited redirects."
        )
        int max_redirects() default 8;

        @AttributeDefinition(
                name = "Jetty HttpClient Address Resolution Timeout",
                description = "Sets the socket address resolution timeout(in milliseconds) used by the default " +
                        "SocketAddressResolver created by this HttpClient at startup."
        )
        long address_resolution_timeout() default 15000L;

        @AttributeDefinition(
                name = "Jetty HttpClient TCP No Delay",
                description = "Whether TCP_NODELAY is enabled."
        )
        boolean tcp_no_delay() default true;

        @AttributeDefinition(
                name = "Jetty HttpClient Follow Redirects",
                description = "Whether Jetty HttpClient to follow redirects."
        )
        boolean follow_redirects() default true;

        @AttributeDefinition(
                name = "Debug Request",
                description = "Debug for detecting any issues with the request execution. Please keep it disabled on production systems."
        )
        boolean debug_request();

        @AttributeDefinition(
                name = "SLF4J MDC Request Attribute Name",
                description = "The attribute might already been setup by application during initial request processing."
        )
        String mdc_req_id_attribute_name() default "JETTY_HTTP_CLIENT_REQ_ID";
    }
}
