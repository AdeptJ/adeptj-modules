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
package com.adeptj.modules.restclient.jetty.util;

import com.adeptj.modules.restclient.core.ClientRequest;
import com.adeptj.modules.restclient.core.HttpMethod;
import com.adeptj.modules.restclient.core.util.Assert;
import com.adeptj.modules.restclient.core.util.ObjectMappers;
import org.eclipse.jetty.client.FormRequestContent;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.client.StringRequestContent;
import org.eclipse.jetty.util.Fields;
import org.eclipse.jetty.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.adeptj.modules.restclient.core.HttpMethod.GET;
import static com.adeptj.modules.restclient.core.HttpMethod.HEAD;
import static com.adeptj.modules.restclient.core.HttpMethod.OPTIONS;
import static com.adeptj.modules.restclient.core.RestClientConstants.CONTENT_TYPE_JSON;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author Rakesh Kumar, AdeptJ
 */
public class JettyRequestFactory {

    private JettyRequestFactory() {
    }

    public static <T, R> Request newRequest(@NotNull HttpClient httpClient, @NotNull ClientRequest<T, R> request) {
        HttpMethod method = request.getMethod();
        Assert.notNull(method, "HttpMethod can't be null");
        Request jettyRequest = httpClient.newRequest(request.getURI()).method(method.toString());
        // 1. Handle timeout
        if (request.getTimeout() > 0) {
            jettyRequest.timeout(request.getTimeout(), MILLISECONDS);
        }
        // 2. Handle headers
        Map<String, String> headers = request.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            jettyRequest.headers(m -> headers.forEach(m::add));
        }
        // 3. Handle query params
        Map<String, String> queryParams = request.getQueryParams();
        if (queryParams != null && !queryParams.isEmpty()) {
            queryParams.forEach(jettyRequest::param);
        }
        // 4. No-body for http GET,HEAD methods, return right away.
        if (method == GET || method == HEAD || method == OPTIONS) {
            return jettyRequest;
        }
        // 5. Handle Form Post - application/x-www-form-urlencoded
        Map<String, String> formParams = request.getFormParams();
        if (formParams != null && !formParams.isEmpty()) {
            Fields fields = new Fields();
            formParams.forEach(fields::put);
            jettyRequest.body(new FormRequestContent(fields));
            return jettyRequest;
        }
        // POST, PUT, PATCH methods must have body, DELETE can also have a body.
        // 6. Handle Body, a JSON string or an Object which will be serialized to JSON.
        String body = ObjectMappers.serialize(request.getBody());
        if (StringUtil.isNotBlank(body)) {
            jettyRequest.body(new StringRequestContent(CONTENT_TYPE_JSON, body));
        }
        return jettyRequest;
    }
}
