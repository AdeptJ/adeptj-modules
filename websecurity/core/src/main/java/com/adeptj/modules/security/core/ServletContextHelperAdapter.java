/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://www.adeptj.com)                           #
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

package com.adeptj.modules.security.core;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.Bundle;
import org.osgi.service.http.context.ServletContextHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_SERVICE_UNAVAILABLE;

/**
 * ServletContextHelper implementation which initializes the {@link ServletContextHelper} with the bundle instance
 * of the service which consumed the {@link com.adeptj.modules.security.core.internal.DefaultServletContextHelper}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ProviderType
public final class ServletContextHelperAdapter extends ServletContextHelper {

    private static final String AUTH_SERVICE_MISSING_MSG = "Authenticator service missing!!";

    private volatile Authenticator authenticator;

    public ServletContextHelperAdapter(Bundle usingBundle) {
        super(usingBundle);
    }

    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public void unsetAuthenticator() {
        this.authenticator = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (this.authenticator == null) {
            response.sendError(SC_SERVICE_UNAVAILABLE, AUTH_SERVICE_MISSING_MSG);
            return false;
        }
        return this.authenticator.handleSecurity(request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finishSecurity(HttpServletRequest request, HttpServletResponse response) {
        super.finishSecurity(request, response);
    }
}
