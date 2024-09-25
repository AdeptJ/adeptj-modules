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
package com.adeptj.modules.restclient.apache.util;

import com.adeptj.modules.restclient.apache.request.EntityEnclosingRequest;
import com.adeptj.modules.restclient.core.ClientRequest;
import com.adeptj.modules.restclient.core.util.ObjectMappers;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Rakesh Kumar, AdeptJ
 */
public class HttpClientUtils {

    public static <T, R> @NotNull EntityEnclosingRequest createEntityEnclosingRequest(@NotNull ClientRequest<T, R> request) {
        EntityEnclosingRequest enclosingRequest = new EntityEnclosingRequest(request.getMethod());
        // Handle Form Post - application/x-www-form-urlencoded
        Map<String, String> formParams = request.getFormParams();
        if (formParams != null && !formParams.isEmpty()) {
            List<NameValuePair> params = HttpClientUtils.createNameValuePairs(formParams);
            enclosingRequest.setEntity(new UrlEncodedFormEntity(params, UTF_8));
            return enclosingRequest;
        }
        T body = request.getBody();
        if (body != null) {
            String data = ObjectMappers.serialize(body);
            StringEntity entity = new StringEntity(data, UTF_8);
            entity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            enclosingRequest.setEntity(entity);
        }
        return enclosingRequest;
    }

    static @NotNull List<NameValuePair> createNameValuePairs(@NotNull Map<String, String> params) {
        List<NameValuePair> pairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return pairs;
    }

    public static <T, R> void addHeaders(@NotNull ClientRequest<T, R> request, HttpRequestBase apacheRequest) {
        Map<String, String> headers = request.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                apacheRequest.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    public static <T, R> void addQueryParams(@NotNull ClientRequest<T, R> request, HttpRequestBase apacheRequest) {
        Map<String, String> queryParams = request.getQueryParams();
        if (queryParams == null || queryParams.isEmpty()) {
            apacheRequest.setURI(request.getURI());
            return;
        }
        try {
            URI uri = new URIBuilder(request.getURI())
                    .addParameters(HttpClientUtils.createNameValuePairs(queryParams))
                    .build();
            apacheRequest.setURI(uri);
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex); // NOSONAR
        }
    }

    private HttpClientUtils() {
    }
}
