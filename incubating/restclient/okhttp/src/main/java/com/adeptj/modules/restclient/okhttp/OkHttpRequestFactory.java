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

import com.adeptj.modules.restclient.core.ClientRequest;
import com.adeptj.modules.restclient.core.HttpMethod;
import com.adeptj.modules.restclient.core.plugin.AuthorizationHeaderPlugin;
import com.adeptj.modules.restclient.core.util.Assert;
import com.adeptj.modules.restclient.core.util.ObjectMappers;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.adeptj.modules.restclient.core.HttpMethod.DELETE;
import static com.adeptj.modules.restclient.core.HttpMethod.GET;
import static com.adeptj.modules.restclient.core.HttpMethod.HEAD;
import static com.adeptj.modules.restclient.core.HttpMethod.OPTIONS;
import static com.adeptj.modules.restclient.core.HttpMethod.POST;

class OkHttpRequestFactory {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    static <T, R> Request newRequest(@NotNull ClientRequest<T, R> request, AuthorizationHeaderPlugin plugin) {
        HttpMethod method = request.getMethod();
        Assert.notNull(method, "HttpMethod can't be null");
        T body = request.getBody();
        Map<String, String> formParams = request.getFormParams();
        Request.Builder builder = new Request.Builder();
        if (body == null && (method == HEAD || method == GET || method == OPTIONS || method == DELETE)) {
            builder.method(method.toString(), null);
        } else if (method == POST && formParams != null && !formParams.isEmpty()) {
            // Handle Form Post - application/x-www-form-urlencoded
            FormBody.Builder formBuilder = new FormBody.Builder();
            for (Map.Entry<String, String> entry : formParams.entrySet()) {
                formBuilder.addEncoded(entry.getKey(), entry.getValue());
            }
            builder.post(formBuilder.build());
        } else if (body != null) {
            String data = ObjectMappers.serialize(body);
            builder.method(method.toString(), RequestBody.create(JSON, data));
        }
        if (plugin != null) {
            builder.addHeader("Authorization", (plugin.getType() + " " + plugin.getValue()));
        }
        Map<String, String> headers = request.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Map<String, String> queryParams = request.getQueryParams();
        if (queryParams == null || queryParams.isEmpty()) {
            builder.url(HttpUrl.get(request.getUri().toString()));
        } else {
            HttpUrl.Builder urlBuilder = HttpUrl.get(request.getUri().toString()).newBuilder();
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
            builder.url(urlBuilder.build());
        }
        return builder.build();
    }
}
