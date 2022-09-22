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
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

@Component(service = RestClient.class)
public class OkHttpRestClient extends AbstractRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final OkHttpClient httpClient;

    @Activate
    public OkHttpRestClient(@NotNull OkHttpClientConfig config) {
        super(config.debug_request(), config.mdc_req_id_attribute_name());
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        this.httpClient = builder.build();
    }

    @Override
    protected @NotNull <T, R> ClientResponse<R> doExecuteRequest(ClientRequest<T, R> request) {
        String authorizationHeaderValue = this.getAuthorizationHeaderValue(request.getUri().getPath());
        Request okHttpRequest = OkHttpRequestFactory.newRequest(request, authorizationHeaderValue);
        try (Response response = this.httpClient.newCall(okHttpRequest).execute()) {
            return ClientResponseFactory.newClientResponse(response, request.getResponseAs());
        } catch (Exception e) {
            throw new RestClientException(e);
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
        this.doBindAuthorizationHeaderPlugin(plugin);
    }

    protected void unbindAuthorizationHeaderPlugin(AuthorizationHeaderPlugin plugin) {
        this.doUnbindAuthorizationHeaderPlugin(plugin);
    }
}
