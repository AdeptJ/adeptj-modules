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

import org.osgi.framework.Bundle;
import org.osgi.service.http.context.ServletContextHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_SERVICE_UNAVAILABLE;

/**
 * ServletContextHelperSupport
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class ServletContextHelperSupport extends ServletContextHelper {

    public static final String DEFAULT_SERVLET_CONTEXT_NAME = "DefaultServletContext";

    private static final String AUTH_SERVICE_MISSING_MSG = "Authenticator service missing!!";

    private final Authenticator authenticator;

    public ServletContextHelperSupport(Bundle bundle, Authenticator authenticator) {
        super(bundle);
        this.authenticator = authenticator;
    }

    @Override
    public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (this.authenticator == null) {
            response.sendError(SC_SERVICE_UNAVAILABLE, AUTH_SERVICE_MISSING_MSG);
            return false;
        }
        return this.authenticator.handleSecurity(request, response);
    }
}
