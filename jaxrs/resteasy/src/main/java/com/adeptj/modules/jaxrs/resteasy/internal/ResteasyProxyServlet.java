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

package com.adeptj.modules.jaxrs.resteasy.internal;

import com.adeptj.modules.jaxrs.resteasy.ResteasyBootstrapException;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletAsyncSupported;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletName;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletPattern;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.adeptj.modules.jaxrs.resteasy.internal.ResteasyConstants.RESTEASY_DISPATCHER_SERVLET_PATH;
import static com.adeptj.modules.jaxrs.resteasy.internal.ResteasyConstants.RESTEASY_PROXY_SERVLET_NAME;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;


/**
 * ResteasyProxyServlet delegates request processing to {@link ResteasyServletDispatcher}.
 * <p>
 * RESTEasy's bootstrapping is delegated to {@link ResteasyLifecycle} which also registers the ServiceTracker for
 * JAX-RS resources and providers.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ResteasyServletInitParameters
@HttpWhiteboardServletAsyncSupported
@HttpWhiteboardServletName(RESTEASY_PROXY_SERVLET_NAME)
@HttpWhiteboardServletPattern(RESTEASY_DISPATCHER_SERVLET_PATH)
@Component(service = Servlet.class, scope = PROTOTYPE)
public class ResteasyProxyServlet extends HttpServlet {

    private static final long serialVersionUID = -4415966373465265279L;

    /**
     * Service is statically injected so that this servlet doesn't get initialized until the reference
     * to {@link ResteasyLifecycle} becomes available which also manages RESTEasy's lifecycle.
     */
    @Reference
    private ResteasyLifecycle resteasyLifecycle; // NOSONAR

    /**
     * Delegates the RESTEasy's bootstrap process in {@link ResteasyLifecycle#start}
     */
    @Override
    public void init() {
        try {
            this.resteasyLifecycle.start(this.getServletConfig());
        } catch (ResteasyBootstrapException ex) {
            this.getServletContext().removeAttribute(ResteasyDeployment.class.getName());
            throw ex;
        }
    }

    /**
     * Dispatches the request to {@link ResteasyServletDispatcher}.
     *
     * @param req  current request to a resource method
     * @param resp required to set status code, content etc.
     * @throws IOException exception thrown by {@link ResteasyServletDispatcher}
     */
    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.resteasyLifecycle.getResteasyDispatcher().service(req, resp);
    }

    /**
     * Shutdown RESTEasy framework and close all the resource and provider trackers.
     */
    @Override
    public void destroy() {
        this.resteasyLifecycle.stop();
        this.getServletContext().removeAttribute(ResteasyDeployment.class.getName());
    }
}
