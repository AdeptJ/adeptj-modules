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

import com.adeptj.modules.restclient.core.ClientResponse;
import com.adeptj.modules.restclient.core.util.ObjectMappers;
import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.StatusLine;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class ClientResponseFactory {

    static <R> @NotNull ClientResponse<R> newClientResponse(@NotNull Response okHttpResponse,
                                                            Class<R> responseAs) throws IOException {
        ClientResponse<R> clientResponse = new ClientResponse<>();
        StatusLine statusLine = StatusLine.get(okHttpResponse);
        clientResponse.setStatus(statusLine.code);
        clientResponse.setReason(statusLine.message);
        // 1. Copy all the headers which may be needed by the caller.
        Headers headers = okHttpResponse.headers();
        Map<String, String> clientRespHeaders = new HashMap<>();
        for (String name : headers.names()) {
            clientRespHeaders.put(name, headers.get(name));
        }
        clientResponse.setHeaders(clientRespHeaders);
        // 2. if no response body is expected then return without setting the content.
        if (responseAs == void.class || responseAs == Void.class) {
            return clientResponse;
        }
        ResponseBody body = okHttpResponse.body();
        if (body != null) {
            // 3. byte[] is expected.
            if (responseAs == byte[].class) {
                clientResponse.setContent(responseAs.cast(body.bytes()));
            } else if (responseAs == String.class) {
                // 4. A text response is expected, create a String from the byte[].
                clientResponse.setContent(responseAs.cast(body.string()));
            } else {
                // 5. A custom type is expected, deserialize the byte[] to the expected type.
                clientResponse.setContent(ObjectMappers.deserialize(body.bytes(), responseAs));
            }
        }
        return clientResponse;
    }
}
