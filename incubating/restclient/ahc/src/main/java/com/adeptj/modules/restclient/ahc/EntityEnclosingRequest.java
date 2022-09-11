package com.adeptj.modules.restclient.ahc;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

public class EntityEnclosingRequest extends HttpEntityEnclosingRequestBase {

    private final String method;

    EntityEnclosingRequest(String method) {
        this.method = method;
    }

    @Override
    public String getMethod() {
        return this.method;
    }
}
