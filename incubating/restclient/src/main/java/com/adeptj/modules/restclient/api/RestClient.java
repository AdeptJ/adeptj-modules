package com.adeptj.modules.restclient.api;

import org.eclipse.jetty.client.HttpClient;
import org.osgi.annotation.versioning.ProviderType;

import java.util.function.Consumer;
import java.util.function.Function;

@ProviderType
public interface RestClient {

    <T, R> ClientResponse<R> GET(ClientRequest<T, R> request);

    <T, R> ClientResponse<R> POST(ClientRequest<T, R> request);

    <T, R> ClientResponse<R> PUT(ClientRequest<T, R> request);

    <T, R> ClientResponse<R> DELETE(ClientRequest<T, R> request);

    <T, R> ClientResponse<R> executeRequest(ClientRequest<T, R> request);

    <R> R doWithHttpClient(Function<HttpClient, R> function);

    void doWithHttpClient(Consumer<HttpClient> consumer);
}
