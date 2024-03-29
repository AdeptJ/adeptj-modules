/*
###############################################################################
#                                                                             #
#    Copyright 2016-2022, AdeptJ (http://www.adeptj.com)                      #
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
package com.adeptj.modules.restclient.jetty;

import com.adeptj.modules.restclient.core.AbstractRestClient;
import com.adeptj.modules.restclient.core.ClientRequest;
import com.adeptj.modules.restclient.core.ClientResponse;
import com.adeptj.modules.restclient.core.RestClient;
import com.adeptj.modules.restclient.core.RestClientException;
import com.adeptj.modules.restclient.core.RestClientInitializationException;
import com.adeptj.modules.restclient.core.plugin.AuthorizationHeaderPlugin;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.client.ContentResponse;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.client.transport.HttpClientTransportOverHTTP;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.atomic.AtomicLong;

import static org.eclipse.jetty.http.HttpHeader.AUTHORIZATION;
import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

@Designate(ocd = JettyHttpClientConfig.class)
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
        } catch (Exception ex) {
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
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
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

    @Reference(service = AuthorizationHeaderPlugin.class, cardinality = MULTIPLE, policy = DYNAMIC)
    protected void bindAuthorizationHeaderPlugin(AuthorizationHeaderPlugin plugin) {
        this.doBindAuthorizationHeaderPlugin(plugin);
    }

    protected void unbindAuthorizationHeaderPlugin(AuthorizationHeaderPlugin plugin) {
        this.doUnbindAuthorizationHeaderPlugin(plugin);
    }
}
