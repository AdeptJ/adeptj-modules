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
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.context.ServletContextHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

import static com.adeptj.modules.security.core.internal.DefaultServletContextHelper.SERVLET_CONTEXT_NAME;
import static com.adeptj.modules.security.core.internal.DefaultServletContextHelper.EQ;
import static com.adeptj.modules.security.core.internal.DefaultServletContextHelper.ROOT_PATH;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH;

/**
 * DefaultServletContextHelper.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ProviderType
@Component(
        service = ServletContextHelper.class,
        scope = ServiceScope.BUNDLE,
        property = {
                HTTP_WHITEBOARD_CONTEXT_NAME + EQ + SERVLET_CONTEXT_NAME,
                HTTP_WHITEBOARD_CONTEXT_PATH + EQ + ROOT_PATH
        }
)
public class DefaultServletContextHelper extends ServletContextHelper {

    static final String SERVLET_CONTEXT_NAME = "AdeptJ DefaultServletContext";

    static final String EQ = "=";

    static final String ROOT_PATH = "/";

    private static final String BIND_AUTHENTICATOR = "bindAuthenticator";

    private static final String UNBIND_AUTHENTICATOR = "unbindAuthenticator";

    private SecurityHandler securityHandler;

    @Reference(
            cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC,
            bind = BIND_AUTHENTICATOR,
            unbind = UNBIND_AUTHENTICATOR
    )
    private volatile Authenticator authenticator;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return this.securityHandler.handleSecurity(request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getResource(String name) {
        return this.securityHandler.getResource(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMimeType(String name) {
        return this.securityHandler.getMimeType(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getResourcePaths(String path) {
        return this.securityHandler.getResourcePaths(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRealPath(String path) {
        return this.securityHandler.getRealPath(path);
    }

    // --------------------- INTERNAL ---------------------

    // Lifecycle methods

    protected void bindAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    protected void unbindAuthenticator(Authenticator authenticator) {
        this.authenticator = null;
    }

    @Activate
    protected void start(ComponentContext componentContext) {
        this.securityHandler = new SecurityHandler(componentContext.getUsingBundle(), this.authenticator);
    }
}
