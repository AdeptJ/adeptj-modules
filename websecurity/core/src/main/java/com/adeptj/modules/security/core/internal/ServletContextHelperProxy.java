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

package com.adeptj.modules.security.core.internal;

import com.adeptj.modules.security.core.Authenticator;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.Bundle;
import org.osgi.service.http.context.ServletContextHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A {@link ServletContextHelper} proxy which initializes the {@link ServletContextHelper} with the bundle instance
 * of the service which consumed the {@link SecurityHandler}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ProviderType
public class ServletContextHelperProxy extends ServletContextHelper {

    private Authenticator authenticator;

    ServletContextHelperProxy(Bundle usingBundle, Authenticator authenticator) {
        super(usingBundle);
        this.authenticator = authenticator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) {
        return this.authenticator.handleSecurity(request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finishSecurity(HttpServletRequest request, HttpServletResponse response) {
        this.authenticator.finishSecurity(request, response);
    }
}
