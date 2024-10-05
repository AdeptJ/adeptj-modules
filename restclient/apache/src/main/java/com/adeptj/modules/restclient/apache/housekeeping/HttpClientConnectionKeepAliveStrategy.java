/*
###############################################################################
#                                                                             #
#    Copyright 2016-2024, AdeptJ (http://www.adeptj.com)                      #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/
package com.adeptj.modules.restclient.apache.housekeeping;

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
 * @author Rakesh Kumar, AdeptJ
 */
public class HttpClientConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy {

    private final long maxIdleTime;

    /**
     * @param maxIdleTime the maximum time a connection may be idle
     */
    public HttpClientConnectionKeepAliveStrategy(long maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    @Override
    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
        long duration = DefaultConnectionKeepAliveStrategy.INSTANCE.getKeepAliveDuration(response, context);
        return (duration > 0 && duration < this.maxIdleTime) ? duration : this.maxIdleTime;
    }
}
