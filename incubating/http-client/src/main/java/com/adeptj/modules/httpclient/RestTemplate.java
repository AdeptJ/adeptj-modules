package com.adeptj.modules.httpclient;

import org.osgi.annotation.versioning.ProviderType;

import java.net.URI;
import java.util.Map;

@ProviderType
public interface RestTemplate {

    byte[] GET(URI uri, Map<String, String> headers);

    <T> T GET(URI uri, Class<T> responseType, Map<String, String> headers);

    <T> T executeCallback(HttpClientCallback<T> callback);

}
