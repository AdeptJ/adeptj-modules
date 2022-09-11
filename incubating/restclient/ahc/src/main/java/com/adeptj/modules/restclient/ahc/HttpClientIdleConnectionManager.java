package com.adeptj.modules.restclient.ahc;

import org.apache.http.conn.HttpClientConnectionManager;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * HttpClient idle connection evictor.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
class HttpClientIdleConnectionManager implements Runnable {

    private final int idleTimeout;

    private final HttpClientConnectionManager connectionManager;

    HttpClientIdleConnectionManager(int idleTimeout, HttpClientConnectionManager connectionManager) {
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
