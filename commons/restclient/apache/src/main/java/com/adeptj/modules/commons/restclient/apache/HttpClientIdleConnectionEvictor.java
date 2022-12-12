package com.adeptj.modules.commons.restclient.apache;

import org.apache.http.conn.HttpClientConnectionManager;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * HttpClient idle connection evictor.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
class HttpClientIdleConnectionEvictor implements Runnable {

    private final int idleTimeout;

    private final HttpClientConnectionManager connectionManager;

    HttpClientIdleConnectionEvictor(int idleTimeout, HttpClientConnectionManager connectionManager) {
        this.idleTimeout = idleTimeout;
        this.connectionManager = connectionManager;
    }

    /**
     * TaskScheduler executes this method by the configured delay and close idle connections.
     */
    @Override
    public void run() {
        this.connectionManager.closeIdleConnections(this.idleTimeout, SECONDS);
    }
}
