package com.adeptj.modules.httpclient;

import org.eclipse.jetty.client.HttpClient;

@FunctionalInterface
public interface HttpClientCallback<T> {

    T doWithHttpClient(HttpClient client);
}
