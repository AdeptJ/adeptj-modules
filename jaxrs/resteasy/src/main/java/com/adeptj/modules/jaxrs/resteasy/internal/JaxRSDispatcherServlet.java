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

import com.adeptj.modules.commons.utils.ClassLoaders;
import com.adeptj.modules.commons.utils.Loggers;
import com.adeptj.modules.jaxrs.core.JaxRSExceptionHandler;
import com.adeptj.modules.jaxrs.resteasy.JaxRSCoreConfig;
import com.adeptj.modules.jaxrs.resteasy.JaxRSInitializationException;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import static com.adeptj.modules.jaxrs.resteasy.internal.JaxRSDispatcherServlet.ASYNC_SUPPORTED_TRUE;
import static com.adeptj.modules.jaxrs.resteasy.internal.JaxRSDispatcherServlet.EQ;
import static com.adeptj.modules.jaxrs.resteasy.internal.JaxRSDispatcherServlet.JAXRS_DISPATCHER_SERVLET_NAME;
import static com.adeptj.modules.jaxrs.resteasy.internal.JaxRSDispatcherServlet.MAPPING_PREFIX_VALUE;
import static com.adeptj.modules.jaxrs.resteasy.internal.JaxRSDispatcherServlet.SERVLET_PATTERN_VALUE;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN;


/**
 * JaxRSDispatcherServlet extends RESTEasy HttpServlet30Dispatcher so that Servlet 3.0 Async behaviour can be leveraged.
 * It also registers the JAX-RS resource/provider ServiceTracker and other providers.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = JaxRSCoreConfig.class)
@Component(
        immediate = true,
        service = Servlet.class,
        configurationPolicy = REQUIRE,
        property = {
                HTTP_WHITEBOARD_SERVLET_NAME + EQ + JAXRS_DISPATCHER_SERVLET_NAME,
                HTTP_WHITEBOARD_SERVLET_PATTERN + EQ + SERVLET_PATTERN_VALUE,
                HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED + EQ + ASYNC_SUPPORTED_TRUE,
                HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX + RESTEASY_SERVLET_MAPPING_PREFIX + EQ + MAPPING_PREFIX_VALUE
        }
)
public class JaxRSDispatcherServlet extends HttpServlet30Dispatcher {

    static final String EQ = "=";

    static final String JAXRS_DISPATCHER_SERVLET_NAME = "AdeptJ JAX-RS DispatcherServlet";

    static final String SERVLET_PATTERN_VALUE = "/*";

    static final String ASYNC_SUPPORTED_TRUE = "true";

    static final String MAPPING_PREFIX_VALUE = "/";

    private static final long serialVersionUID = -4415966373465265279L;

    private ServiceTracker<Object, Object> resourceTracker;

    private ServiceTracker<Object, Object> providerTracker;

    private BundleContext context;

    private JaxRSCoreConfig config;

    @Override
    public void init(ServletConfig servletConfig) {
        final long startTime = System.nanoTime();
        final Logger logger = Loggers.get(JaxRSDispatcherServlet.class);
        logger.info("Initializing JaxRSDispatcherServlet!!");
        // Use Bundle ClassLoader as the context ClassLoader because we need to find the providers specified
        // in the file [META-INF/services/javax.ws.rs.Providers] file which will not be visible to the original
        // context ClassLoader which is the application class loader in fact.
        ClassLoaders.executeWith(JaxRSDispatcherServlet.class.getClassLoader(), () -> {
            try {
                // First let the RESTEasy framework bootstrap in super.init()
                super.init(servletConfig);
                Dispatcher dispatcher = this.getDispatcher();
                ResteasyProviderFactory providerFactory = dispatcher.getProviderFactory();
                JaxRSUtil.removeDefaultValidators(providerFactory);
                providerFactory.register(ValidatorContextResolver.class)
                        .register(JaxRSUtil.createCorsFilter(this.config))
                        .register(new DefaultExceptionHandler(this.config.showException()))
                        .register(new JaxRSExceptionHandler(this.config.showException()));
                this.providerTracker = JaxRSUtil.getProviderServiceTracker(this.context, providerFactory);
                this.resourceTracker = JaxRSUtil.getResourceServiceTracker(this.context, dispatcher.getRegistry());
                logger.info("JaxRSDispatcherServlet initialized in [{}] ms!!",
                        NANOSECONDS.toMillis(System.nanoTime() - startTime));
            } catch (Exception ex) { // NOSONAR
                logger.error("Exception while initializing JaxRSDispatcherServlet!!", ex);
                throw new JaxRSInitializationException(ex.getMessage(), ex);
            }
        });
    }

    // --------------------------- INTERNAL ---------------------------
    // ---------------- Component lifecycle methods -------------------

    @Activate
    protected void start(JaxRSCoreConfig config, BundleContext context) {
        this.config = config;
        this.context = context;
    }

    @Deactivate
    protected void stop() {
        JaxRSUtil.closeServiceTracker(this.providerTracker);
        JaxRSUtil.closeServiceTracker(this.resourceTracker);
    }
}
