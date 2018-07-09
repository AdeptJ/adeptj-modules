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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardFilterAsyncSupported;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletName;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.invoke.MethodHandles;

import static com.adeptj.modules.commons.utils.Constants.EQ;
import static com.adeptj.modules.jaxrs.resteasy.internal.ResteasyConstants.RESTEASY_PROXY_SERVLET_NAME;
import static com.adeptj.modules.jaxrs.resteasy.internal.ResteasyConstants.SERVLET_URL_PATTERN;
import static org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX;


/**
 * ResteasyProxyServlet wraps RESTEasy's HttpServlet30Dispatcher so that Servlet 3.0 Async behaviour can be leveraged.
 * <p>
 * RESTEasy's bootstrapping is delegated to {@link ResteasyLifecycle} which also registers the ServiceTracker for
 * JAX-RS resources and providers.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@HttpWhiteboardServletName(RESTEASY_PROXY_SERVLET_NAME)
@HttpWhiteboardServletPattern(SERVLET_URL_PATTERN)
@HttpWhiteboardFilterAsyncSupported
@Component(
        service = Servlet.class,
        scope = PROTOTYPE,
        property = {
                HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX + RESTEASY_SERVLET_MAPPING_PREFIX + EQ + SERVLET_URL_PATTERN
        }
)
public class ResteasyProxyServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final long serialVersionUID = -4415966373465265279L;

    private static final String PROCESSING_REQUEST_MSG = "Processing [{}] request for [{}]";

    /**
     * Manages RESTEasy's lifecycle.
     */
    @Reference
    private ResteasyLifecycle resteasyLifecycle;

    /**
     * Delegates the RESTEasy's bootstrap process in {@link ResteasyLifecycle#start(ServletConfig)}
     */
    @Override
    public void init() {
        this.resteasyLifecycle.start(this.getServletConfig());
    }

    /**
     * Dispatches the request to RESTEasy's HttpServlet30Dispatcher.
     *
     * @param req  current request to a resource method
     * @param resp required to set status code, content etc.
     * @throws IOException exception thrown by RESTEasy's HttpServlet30Dispatcher
     */
    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(PROCESSING_REQUEST_MSG, req.getMethod(), req.getRequestURI());
        }
        this.resteasyLifecycle.getResteasyDispatcher().service(req.getMethod(), req, resp);
    }

    /**
     * Shutdown RESTEasy framework and close all the resource and provider trackers.
     */
    @Override
    public void destroy() {
        this.resteasyLifecycle.stop();
    }
}
