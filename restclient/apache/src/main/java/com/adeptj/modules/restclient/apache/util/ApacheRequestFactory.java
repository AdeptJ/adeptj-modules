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

import com.adeptj.modules.restclient.apache.request.NonEntityEnclosingRequest;
import com.adeptj.modules.restclient.core.ClientRequest;
import com.adeptj.modules.restclient.core.HttpMethod;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.jetbrains.annotations.NotNull;

/**
 * @author Rakesh Kumar, AdeptJ
 */
public class ApacheRequestFactory {

    public static <T, R> HttpUriRequest newRequest(@NotNull ClientRequest<T, R> request) {
        HttpMethod method = request.getMethod();
        HttpRequestBase apacheRequest = switch (method) {
            case GET, HEAD, OPTIONS -> new NonEntityEnclosingRequest(request.getURI(), method);
            case POST, PUT, PATCH, DELETE -> HttpClientUtils.getEntityEnclosingRequest(request);
        };
        HttpClientUtils.addHeaders(request, apacheRequest);
        HttpClientUtils.addQueryParams(request, apacheRequest);
        return apacheRequest;
    }

    private ApacheRequestFactory() {
    }
}
