package com.adeptj.modules.restclient.ahc;

import org.apache.http.client.methods.HttpRequestBase;

public class NonEntityEnclosingRequest extends HttpRequestBase {

    private final String method;

    NonEntityEnclosingRequest(String method) {
        this.method = method;
    }

    @Override
    public String getMethod() {
        return this.method;
    }
}
