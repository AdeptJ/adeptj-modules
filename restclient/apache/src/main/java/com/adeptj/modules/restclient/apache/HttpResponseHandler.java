package com.adeptj.modules.restclient.apache;

import com.adeptj.modules.restclient.core.ClientResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;

import java.io.IOException;

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
