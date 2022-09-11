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
import com.adeptj.modules.restclient.core.plugin.AuthorizationHeaderPlugin;
import org.eclipse.jetty.client.AbstractHttpClientTransport;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.io.ClientConnector;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.adeptj.modules.restclient.core.HttpMethod.DELETE;
import static com.adeptj.modules.restclient.core.HttpMethod.GET;
import static com.adeptj.modules.restclient.core.HttpMethod.POST;
import static com.adeptj.modules.restclient.core.HttpMethod.PUT;
import static org.eclipse.jetty.http.HttpHeader.AUTHORIZATION;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

@Designate(ocd = JettyHttpClientConfig.class)
@Component(service = RestClient.class, configurationPolicy = REQUIRE)
public class JettyRestClient extends AbstractRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final HttpClient jettyClient;

    private final boolean debugRequest;

    private final String mdcReqIdAttrName;

    private final List<AuthorizationHeaderPlugin> authorizationHeaderPlugins;

    @Activate
    public JettyRestClient(@NotNull JettyHttpClientConfig config) {
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
        ((AbstractHttpClientTransport) this.jettyClient.getTransport())
                .getContainedBeans(ClientConnector.class)
                .stream()
                .findFirst()
                .ifPresent(connector -> connector.setTCPNoDelay(config.tcp_no_delay()));
        LOGGER.info("Starting Jetty HttpClient!");
        try {
            this.jettyClient.start();
        } catch (Exception ex) {
            throw new JettyHttpClientInitializationException(ex);
        }
        this.debugRequest = config.debug_request();
        this.mdcReqIdAttrName = config.mdc_req_id_attribute_name();
        this.authorizationHeaderPlugins = new CopyOnWriteArrayList<>();
    }

    @Override
    public <T, R> ClientResponse<R> GET(@NotNull ClientRequest<T, R> request) {
        this.ensureHttpMethod(request, GET);
        return this.executeRequest(request);
    }

    @Override
    public <T, R> ClientResponse<R> POST(@NotNull ClientRequest<T, R> request) {
        this.ensureHttpMethod(request, POST);
        return this.executeRequest(request);
    }

    @Override
    public <T, R> ClientResponse<R> PUT(@NotNull ClientRequest<T, R> request) {
        this.ensureHttpMethod(request, PUT);
        return this.executeRequest(request);
    }

    @Override
    public <T, R> ClientResponse<R> DELETE(@NotNull ClientRequest<T, R> request) {
        this.ensureHttpMethod(request, DELETE);
        return this.executeRequest(request);
    }

    @Override
    public <T, R> ClientResponse<R> executeRequest(ClientRequest<T, R> request) {
        if (this.debugRequest) {
            return this.doExecuteRequestDebug(request);
        }
        return this.doExecuteRequest(request);
    }

    @Override
    public Object unwrap() {
        return this.jettyClient;
    }

    private <T, R> @NotNull ClientResponse<R> doExecuteRequestDebug(ClientRequest<T, R> request) {
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

    private <T, R> @NotNull ClientResponse<R> doExecuteRequest(ClientRequest<T, R> request) {
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
        List<AuthorizationHeaderPlugin> plugins = this.authorizationHeaderPlugins;
        if (plugins.isEmpty()) {
            return;
        }
        AuthorizationHeaderPlugin plugin = this.resolveAuthorizationHeaderPlugin(plugins, request.getPath());
        if (plugin != null) {
            request.headers(f -> f.add(AUTHORIZATION, (plugin.getType() + " " + plugin.getValue())));
            LOGGER.info("Authorization header added to request [{}] by plugin [{}]", request.getURI(), plugin);
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
