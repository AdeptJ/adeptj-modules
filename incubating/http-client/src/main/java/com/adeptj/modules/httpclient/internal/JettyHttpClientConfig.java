package com.adeptj.modules.httpclient.internal;

import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * AdeptJ Jetty HttpClient configurations.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@ObjectClassDefinition(
        name = "AdeptJ Jetty HttpClient Configuration",
        description = "AdeptJ Jetty HttpClient Configuration"
)
public @interface JettyHttpClientConfig {

    String name() default "AdeptJ Jetty HttpClient";

    long connectTimeout() default 60000L;

    long idleTimeout() default 60000L;

    int maxConnectionsPerDestination() default 64;

    int maxRequestsQueuedPerDestination() default 1024;

    int requestBufferSize() default 4096;

    int responseBufferSize() default 16384;

    int maxRedirects() default 8;

    long addressResolutionTimeout() default 15000;

    boolean tcpNoDelay() default true;
}
