package com.adeptj.modules.restclient.apache;

import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.protocol.HttpContext;

/**
 * Simple implementation of Apache {@link ConnectionKeepAliveStrategy}
 * <p>
 * If the keep-alive timeout directive in the response is shorter than our configured max, honor that.
 * Otherwise, go with the configured maximum.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
class HttpClientConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy {

    private final long maxIdleTime;

    /**
     * @param maxIdleTime the maximum time a connection may be idle
     */
    HttpClientConnectionKeepAliveStrategy(long maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    @Override
    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
        long duration = DefaultConnectionKeepAliveStrategy.INSTANCE.getKeepAliveDuration(response, context);
        return (duration > 0 && duration < this.maxIdleTime) ? duration : this.maxIdleTime;
    }
}
