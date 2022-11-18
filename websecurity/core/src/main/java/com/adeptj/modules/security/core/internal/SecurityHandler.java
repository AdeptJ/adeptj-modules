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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.servlet.context.ServletContextHelper;
import org.osgi.service.servlet.whiteboard.propertytypes.HttpWhiteboardContext;

import static com.adeptj.modules.security.core.SecurityConstants.SERVLET_CONTEXT_NAME;
import static com.adeptj.modules.security.core.SecurityConstants.SERVLET_CONTEXT_PATH;
import static org.osgi.service.component.annotations.ServiceScope.BUNDLE;

/**
 * SecurityHandler.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ProviderType
@HttpWhiteboardContext(name = SERVLET_CONTEXT_NAME, path = SERVLET_CONTEXT_PATH)
@Component(service = ServletContextHelper.class, scope = BUNDLE)
public class SecurityHandler extends ServletContextHelper {

    private final Authenticator authenticator;

    @Activate
    public SecurityHandler(@Reference Authenticator authenticator, @NotNull ComponentContext context) {
        super(context.getUsingBundle());
        this.authenticator = authenticator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) {
        return this.authenticator.handleSecurity(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finishSecurity(HttpServletRequest request, HttpServletResponse response) {
        this.authenticator.finishSecurity(request);
    }
}
