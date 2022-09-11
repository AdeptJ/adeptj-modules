package com.adeptj.modules.restclient.apache;

import com.adeptj.modules.restclient.core.ClientResponse;
import com.adeptj.modules.restclient.core.util.ObjectMappers;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ClientResponseFactory {

    static <R> @NotNull ClientResponse<R> newClientResponse(@NotNull CloseableHttpResponse response,
                                                            Class<R> responseAs) throws IOException {
        ClientResponse<R> clientResponse = new ClientResponse<>();
        StatusLine statusLine = response.getStatusLine();
        clientResponse.setStatus(statusLine.getStatusCode());
        clientResponse.setReason(statusLine.getReasonPhrase());
        // 1. Copy all the headers which may be needed by the caller.
        Header[] allHeaders = response.getAllHeaders();
        if (allHeaders != null && allHeaders.length > 0) {
            Map<String, String> headers = new HashMap<>();
            for (Header header : allHeaders) {
                headers.put(header.getName(), header.getValue());
            }
            clientResponse.setHeaders(headers);
        }
        // 2. if no clientResponse body is expected then return without setting the content.
        if (responseAs.equals(void.class) || responseAs.equals(Void.class)) {
            return clientResponse;
        }
        HttpEntity entity = response.getEntity();
        // 3. byte[] is expected.
        if (responseAs.equals(byte[].class)) {
            clientResponse.setContent(responseAs.cast(IOUtils.toByteArray(entity.getContent())));
        } else if (responseAs.equals(String.class)) {
            // 4. A text response is expected, create a String from the HttpEntity.
            clientResponse.setContent(responseAs.cast(EntityUtils.toString(entity, UTF_8)));
        } else {
            // 5. A custom type is expected, deserialize the HttpEntity to the expected type.
            clientResponse.setContent(ObjectMappers.deserialize(IOUtils.toByteArray(entity.getContent()), responseAs));
        }
        EntityUtils.consumeQuietly(entity);
        return clientResponse;
    }
}
