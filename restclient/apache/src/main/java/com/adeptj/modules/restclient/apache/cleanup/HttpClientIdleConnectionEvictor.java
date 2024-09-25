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
package com.adeptj.modules.restclient.apache.cleanup;

import org.apache.http.conn.HttpClientConnectionManager;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * HttpClient idle connection evictor.
 *
 * @author Rakesh Kumar, AdeptJ
 */
public class HttpClientIdleConnectionEvictor implements Runnable {

    private final int idleTimeout;

    private final HttpClientConnectionManager connectionManager;

    public HttpClientIdleConnectionEvictor(int idleTimeout, HttpClientConnectionManager connectionManager) {
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
