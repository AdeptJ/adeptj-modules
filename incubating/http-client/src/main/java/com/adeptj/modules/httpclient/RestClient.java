package com.adeptj.modules.httpclient;

import org.eclipse.jetty.client.HttpClient;
import org.osgi.annotation.versioning.ProviderType;

import java.util.function.Consumer;
import java.util.function.Function;

@ProviderType
public interface RestClient {

    <T> ClientResponse<T> GET(ClientRequest request, Class<T> responseType);

    <T> ClientResponse<T> POST(ClientRequest request, Class<T> responseType);

    <T> ClientResponse<T> PUT(ClientRequest request, Class<T> responseType);

    <T> ClientResponse<T> DELETE(ClientRequest request, Class<T> responseType);

    <T> ClientResponse<T> executeRequest(ClientRequest request, Class<T> responseType);

    <T> T doWithHttpClient(Function<HttpClient, T> function);

    void doWithHttpClient(Consumer<HttpClient> consumer);
}
