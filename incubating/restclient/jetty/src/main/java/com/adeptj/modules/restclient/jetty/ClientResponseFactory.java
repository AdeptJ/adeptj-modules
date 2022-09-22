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

import com.adeptj.modules.restclient.core.ClientResponse;
import com.adeptj.modules.restclient.core.util.ObjectMappers;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpField;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

class ClientResponseFactory {

    static <R> @NotNull ClientResponse<R> newClientResponse(@NotNull ContentResponse jettyResponse, Class<R> responseAs) {
        ClientResponse<R> response = new ClientResponse<>();
        response.setStatus(jettyResponse.getStatus());
        response.setReason(jettyResponse.getReason());
        // 1. Copy all the headers which may be needed by the caller.
        if (jettyResponse.getHeaders().size() > 0) {
            response.setHeaders(jettyResponse.getHeaders()
                    .stream()
                    .collect(Collectors.toMap(HttpField::getName, HttpField::getValue)));
        }
        // 2. if no response body is expected then return without setting the content.
        if (responseAs == void.class || responseAs == Void.class) {
            return response;
        }
        // 3. byte[] is expected - the Jetty client response is already byte[]
        if (responseAs == byte[].class) {
            response.setContent(responseAs.cast(jettyResponse.getContent()));
        } else if (responseAs == String.class) {
            // 4. A text response is expected, create a String from the Jetty client response byte[].
            response.setContent(responseAs.cast(jettyResponse.getContentAsString()));
        } else {
            // 5. A custom type is expected, deserialize the Jetty client response byte[] to the expected type.
            response.setContent(ObjectMappers.deserialize(jettyResponse.getContent(), responseAs));
        }
        return response;
    }
}
