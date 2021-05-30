package com.adeptj.modules.httpclient;

import org.eclipse.jetty.client.HttpClient;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface HttpClientProvider {

    /**
     * Provides an AdeptJ Runtime managed Jetty {@link HttpClient}, caller must not close it as it will not be usable
     * after the close call.
     *
     * @return a configured {@link HttpClient}
     */
    HttpClient getHttpClient();
}
