/*
###############################################################################
#                                                                             #
#    Copyright 2016-2022, AdeptJ (http://www.adeptj.com)                      #
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
package com.adeptj.modules.restclient.jetty;

import org.eclipse.jetty.client.HttpClient;

import java.util.concurrent.atomic.AtomicBoolean;

class HttpClientWrapper extends HttpClient {

    private final AtomicBoolean stopInternal;

    HttpClientWrapper() {
        this.stopInternal = new AtomicBoolean(false);
    }

    final void setStopInternal() {
        this.stopInternal.set(true);
    }

    @Override
    protected void doStop() throws Exception {
        if (this.stopInternal.get()) {
            super.doStop();
        } else {
            throw new UnsupportedOperationException("Managed Jetty HttpClient can't be closed by consumer!!");
        }
    }
}
