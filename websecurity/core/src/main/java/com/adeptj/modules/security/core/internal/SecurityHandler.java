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
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.util.Set;

import static com.adeptj.modules.security.core.SecurityConstants.SERVLET_CONTEXT_NAME;
import static com.adeptj.modules.security.core.internal.SecurityHandler.ROOT_PATH;
import static org.osgi.service.component.annotations.ServiceScope.BUNDLE;

/**
 * SecurityHandler.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ProviderType
@HttpWhiteboardContext(name = SERVLET_CONTEXT_NAME, path = ROOT_PATH)
@Component(service = ServletContextHelper.class, scope = BUNDLE)
public class SecurityHandler extends ServletContextHelper {

    static final String ROOT_PATH = "/";

    private ServletContextHelperProxy contextHelperProxy;

    /**
     * The {@link Authenticator} service is statically referenced.
     */
    @Reference
    private Authenticator authenticator;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) {
        return this.contextHelperProxy.handleSecurity(request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finishSecurity(HttpServletRequest request, HttpServletResponse response) {
        this.contextHelperProxy.finishSecurity(request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getResource(String name) {
        return this.contextHelperProxy.getResource(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMimeType(String name) {
        return this.contextHelperProxy.getMimeType(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getResourcePaths(String path) {
        return this.contextHelperProxy.getResourcePaths(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRealPath(String path) {
        return this.contextHelperProxy.getRealPath(path);
    }

    // <<------------------------------------------ OSGi INTERNAL ------------------------------------------>>

    @Activate
    protected void start(ComponentContext context) {
        this.contextHelperProxy = new ServletContextHelperProxy(context.getUsingBundle(), this.authenticator);
    }
}
