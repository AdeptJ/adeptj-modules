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
package com.adeptj.modules.restclient.okhttp;

import com.adeptj.modules.restclient.core.AbstractRestClient;
import com.adeptj.modules.restclient.core.ClientRequest;
import com.adeptj.modules.restclient.core.ClientResponse;
import com.adeptj.modules.restclient.core.RestClient;
import com.adeptj.modules.restclient.core.RestClientException;
import com.adeptj.modules.restclient.core.plugin.AuthorizationHeaderPlugin;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.adeptj.modules.restclient.core.HttpMethod.DELETE;
import static com.adeptj.modules.restclient.core.HttpMethod.GET;
import static com.adeptj.modules.restclient.core.HttpMethod.POST;
import static com.adeptj.modules.restclient.core.HttpMethod.PUT;
import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

@Component(service = RestClient.class)
public class OkHttpRestClient extends AbstractRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final OkHttpClient httpClient;

    private final boolean debugRequest;

    private final String mdcReqIdAttrName;

    private final List<AuthorizationHeaderPlugin> authorizationHeaderPlugins;

    @Activate
    public OkHttpRestClient(OkHttpClientConfig config) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        this.httpClient = builder.build();
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
        AuthorizationHeaderPlugin plugin = this.getAuthorizationHeaderPlugin(request);
        Request okHttpRequest = OkHttpRequestFactory.newRequest(request, plugin);
        try (Response response = this.httpClient.newCall(okHttpRequest).execute()) {
            return ClientResponseFactory.newClientResponse(response, request.getResponseAs());
        } catch (Exception e) {
            throw new RestClientException(e);
        }
    }

    @Override
    public Object unwrap() {
        return this.httpClient;
    }

    private <T, R> AuthorizationHeaderPlugin getAuthorizationHeaderPlugin(ClientRequest<T, R> request) {
        // Create a temp var because the service is dynamic.
        List<AuthorizationHeaderPlugin> plugins = this.authorizationHeaderPlugins;
        if (plugins.isEmpty()) {
            return null;
        }
        AuthorizationHeaderPlugin plugin = this.resolveAuthorizationHeaderPlugin(plugins, request.getUri().getPath());
        if (plugin != null) {
            LOGGER.info("Authorization header added to request [{}] by plugin [{}]", request.getUri(), plugin);
        }
        return plugin;
    }

    // <<----------------------------------------- OSGi Internal  ------------------------------------------>>

    @Deactivate
    protected void stop() {
        LOGGER.info("Stopping Jetty HttpClient!");
        try {
            this.httpClient.dispatcher().executorService().shutdown();
            this.httpClient.connectionPool().evictAll();
            Cache cache = this.httpClient.cache();
            if (cache != null) {
                cache.close();
            }
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
