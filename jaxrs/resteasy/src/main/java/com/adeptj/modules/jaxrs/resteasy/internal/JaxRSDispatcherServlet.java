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

import com.adeptj.modules.commons.utils.Functions;
import com.adeptj.modules.commons.validator.service.ValidatorService;
import com.adeptj.modules.jaxrs.resteasy.JaxRSCoreConfig;
import com.adeptj.modules.jaxrs.resteasy.internal.whiteboard.JaxRSWhiteboardManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import java.io.IOException;

import static com.adeptj.modules.commons.utils.Constants.ASYNC_SUPPORTED_TRUE;
import static com.adeptj.modules.commons.utils.Constants.EQ;
import static com.adeptj.modules.jaxrs.resteasy.internal.ResteasyConstants.JAXRS_DISPATCHER_SERVLET_NAME;
import static com.adeptj.modules.jaxrs.resteasy.internal.ResteasyConstants.SERVLET_URL_PATTERN;
import static org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN;


/**
 * JaxRSDispatcherServlet extends RESTEasy's HttpServlet30Dispatcher so that Servlet 3.0 Async behaviour can be leveraged.
 * <p>
 * RESTEasy's bootstrapping is delegated to {@link JaxRSWhiteboardManager} which also registers the ServiceTracker for
 * JAX-RS resources and providers.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = JaxRSCoreConfig.class)
@Component(
        service = Servlet.class,
        scope = PROTOTYPE,
        configurationPolicy = REQUIRE,
        property = {
                HTTP_WHITEBOARD_SERVLET_NAME + EQ + JAXRS_DISPATCHER_SERVLET_NAME,
                HTTP_WHITEBOARD_SERVLET_PATTERN + EQ + SERVLET_URL_PATTERN,
                HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED + EQ + ASYNC_SUPPORTED_TRUE,
                HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX + RESTEASY_SERVLET_MAPPING_PREFIX + EQ + SERVLET_URL_PATTERN
        }
)
public class JaxRSDispatcherServlet extends HttpServlet {

    private static final long serialVersionUID = -4415966373465265279L;

    /**
     * Manages RESTEasy's lifecycle.
     */
    private JaxRSWhiteboardManager whiteboardManager;

    /**
     * Statically injected ValidatorService, this component will not resolve until one is provided.
     */
    @Reference
    private ValidatorService validatorService;

    /**
     * Bootstraps the RESTEasy Framework using Bundle's ClassLoader as the context ClassLoader because
     * we need to find the providers specified in the file [META-INF/services/javax.ws.rs.Providers] file
     * which will not be visible to the original context ClassLoader which is the application ClassLoader itself.
     */
    @Override
    public void init() {
        Functions.execute(this.getClass().getClassLoader(),
                () -> this.whiteboardManager.start(this.getServletConfig(), this.validatorService.getValidatorFactory()));
    }

    /**
     * Dispatches the request to RESTEasy's HttpServlet30Dispatcher.
     *
     * @param req current request to a resource method
     * @param res required to set status code, content etc.
     * @throws ServletException exception thrown by RESTEasy's HttpServlet30Dispatcher
     * @throws IOException      exception thrown by RESTEasy's HttpServlet30Dispatcher
     */
    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        this.whiteboardManager.getResteasyDispatcher().service(req, res);
    }

    /**
     * Shutdown RESTEasy framework and close all the resource and provider trackers.
     */
    @Override
    public void destroy() {
        this.whiteboardManager.stop();
    }

    // --------------------------- INTERNAL ---------------------------
    // ---------------- Component lifecycle methods -------------------

    @Activate
    protected void start(BundleContext context, JaxRSCoreConfig config) {
        this.whiteboardManager = new JaxRSWhiteboardManager(context, config);
    }
}
