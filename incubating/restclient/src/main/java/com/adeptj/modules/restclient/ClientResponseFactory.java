package com.adeptj.modules.restclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.client.api.ContentResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ClientResponseFactory {

    public static <R> ClientResponse<R> newClientResponse(ContentResponse cr,
                                                          Class<R> responseType,
                                                          ObjectMapper mapper) throws IOException {
        ClientResponse<R> response = new ClientResponse<>();
        response.setStatus(cr.getStatus());
        response.setReason(cr.getReason());
        if (cr.getHeaders().size() > 0) {
            Map<String, String> headers = new HashMap<>();
            cr.getHeaders().forEach(f -> headers.put(f.getName(), f.getValue()));
            response.setHeaders(headers);
        }
        if (responseType.equals(void.class)) {
            return response;
        }
        if (responseType.equals(byte[].class)) {
            response.setContent(responseType.cast(cr.getContent()));
        } else if (responseType.equals(String.class)) {
            response.setContent(responseType.cast(new String(cr.getContent(), UTF_8)));
        } else {
            response.setContent(mapper.reader().forType(responseType).readValue(cr.getContent()));
        }
        return response;
    }
}
