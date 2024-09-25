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
package com.adeptj.modules.restclient.apache.handler;

import com.adeptj.modules.restclient.apache.util.ClientResponseFactory;
import com.adeptj.modules.restclient.core.ClientResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;

import java.io.IOException;

/**
 * @author Rakesh Kumar, AdeptJ
 */
public class HttpResponseHandler<T> implements ResponseHandler<ClientResponse<T>> {

    private final Class<T> responseAs;

    public HttpResponseHandler(Class<T> responseAs) {
        this.responseAs = responseAs;
    }

    @Override
    public ClientResponse<T> handleResponse(HttpResponse response) throws IOException {
        return ClientResponseFactory.newResponse(response, this.responseAs);
    }
}
