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
package com.adeptj.modules.restclient.apache;

import com.adeptj.modules.restclient.core.ClientResponse;
import com.adeptj.modules.restclient.core.util.ObjectMappers;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

class ClientResponseFactory {

    private ClientResponseFactory() {
    }

    @NotNull
    static <R> ClientResponse<R> newResponse(@NotNull HttpResponse response, Class<R> responseAs) throws IOException {
        ClientResponse<R> clientResponse = new ClientResponse<>();
        StatusLine statusLine = response.getStatusLine();
        clientResponse.setStatus(statusLine.getStatusCode());
        clientResponse.setReason(statusLine.getReasonPhrase());
        // 1. Copy all the headers which may be needed by the caller.
        Header[] responseHeaders = response.getAllHeaders();
        if (responseHeaders != null && responseHeaders.length > 0) {
            clientResponse.setHeaders(Stream.of(responseHeaders)
                    .collect(Collectors.toMap(Header::getName, Header::getValue)));
        }
        // 2. if no response body is expected then return without setting the content.
        if (responseAs == void.class || responseAs == Void.class) {
            return clientResponse;
        }
        HttpEntity entity = response.getEntity();
        // 3. byte[] is expected.
        if (responseAs == byte[].class) {
            clientResponse.setContent(responseAs.cast(IOUtils.toByteArray(entity.getContent())));
        } else if (responseAs == String.class) {
            // 4. A text response is expected, create a String from the HttpEntity.
            clientResponse.setContent(responseAs.cast(EntityUtils.toString(entity, UTF_8)));
        } else {
            // 5. A custom type is expected, deserialize the HttpEntity to the expected type.
            clientResponse.setContent(ObjectMappers.deserialize(entity.getContent(), responseAs));
        }
        return clientResponse;
    }
}
