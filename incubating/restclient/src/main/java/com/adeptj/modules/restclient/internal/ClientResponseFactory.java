package com.adeptj.modules.restclient.internal;

import com.adeptj.modules.restclient.ClientResponse;
import com.adeptj.modules.restclient.ObjectMappers;
import org.eclipse.jetty.client.api.ContentResponse;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

class ClientResponseFactory {

    static <R> ClientResponse<R> newClientResponse(ContentResponse cr, Class<R> responseType) {
        ClientResponse<R> response = new ClientResponse<>();
        response.setStatus(cr.getStatus());
        response.setReason(cr.getReason());
        // 1. Copy all the headers which may be needed by the caller.
        if (cr.getHeaders().size() > 0) {
            Map<String, String> headers = new HashMap<>();
            cr.getHeaders().forEach(f -> headers.put(f.getName(), f.getValue()));
            response.setHeaders(headers);
        }
        // 2. if no response body is expected then return without setting the content.
        if (responseType.equals(void.class)) {
            return response;
        }
        // 3. byte[] is expected - the Jetty client response is already byte[]
        if (responseType.equals(byte[].class)) {
            response.setContent(responseType.cast(cr.getContent()));
        } else if (responseType.equals(String.class)) {
            // 4. A text response is expected, create a String from the Jetty client response byte[].
            response.setContent(responseType.cast(new String(cr.getContent(), UTF_8)));
        } else {
            // 5. A custom type is expected, deserialize the Jetty client response byte[] to the expected type.
            response.setContent(ObjectMappers.deserialize(cr.getContent(), responseType));
        }
        return response;
    }
}
