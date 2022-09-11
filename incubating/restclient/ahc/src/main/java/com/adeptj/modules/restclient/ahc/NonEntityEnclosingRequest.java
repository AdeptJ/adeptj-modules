package com.adeptj.modules.restclient.ahc;

import com.adeptj.modules.restclient.core.ClientRequest;
import org.apache.http.client.methods.HttpRequestBase;

public class NonEntityEnclosingRequest extends HttpRequestBase {

    private final String method;

    <T, R> NonEntityEnclosingRequest(ClientRequest<T, R> request) {
        this.method = request.getMethod().toString();
    }

    @Override
    public String getMethod() {
        return this.method;
    }
}
