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
import com.adeptj.modules.jaxrs.core.jwt.JwtFilter;
import com.adeptj.modules.jaxrs.resteasy.JaxRSCoreConfig;
import com.adeptj.modules.jaxrs.resteasy.JaxRSInitializationException;
import com.adeptj.modules.security.jwt.JwtService;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.util.Optional;

import static com.adeptj.modules.commons.utils.OSGiUtils.anyServiceFilter;
import static com.adeptj.modules.jaxrs.resteasy.internal.JaxRSDispatcherServlet.ASYNC_SUPPORTED_TRUE;
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
@Component(immediate = true, service = Servlet.class, configurationPolicy = REQUIRE,
        property = {
                HTTP_WHITEBOARD_SERVLET_NAME + JAXRS_DISPATCHER_SERVLET_NAME,
                HTTP_WHITEBOARD_SERVLET_PATTERN + SERVLET_PATTERN_VALUE,
                HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED + ASYNC_SUPPORTED_TRUE,
                HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX + RESTEASY_SERVLET_MAPPING_PREFIX + MAPPING_PREFIX_VALUE
        })
public class JaxRSDispatcherServlet extends HttpServlet30Dispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRSDispatcherServlet.class);

    private static final long serialVersionUID = -4415966373465265279L;

    private static final String FIELD_CTX_RESOLVERS = "contextResolvers";

    private static final String RES_FILTER_EXPR = "(&(objectClass=*)(osgi.jaxrs.resource.base=*))";

    private static final String PROVIDER_FILTER_EXPR = "(&(objectClass=*)(osgi.jaxrs.provider=*))";

    private static final String INIT_MSG = "JaxRSDispatcherServlet initialized in [{}] ms!!";

    private static final String BIND_JWT_SERVICE = "bindJwtService";

    private static final String UNBIND_JWT_SERVICE = "unbindJwtService";

    static final String JAXRS_DISPATCHER_SERVLET_NAME = "=AdeptJ JAX-RS DispatcherServlet";

    static final String SERVLET_PATTERN_VALUE = "=/*";

    static final String ASYNC_SUPPORTED_TRUE = "=true";

    static final String MAPPING_PREFIX_VALUE = "=/";

    private ServiceTracker<Object, Object> resourceTracker;

    private ServiceTracker<Object, Object> providerTracker;

    private BundleContext context;

    private JwtFilter jwtFilter;

    private JaxRSCoreConfig config;

    /**
     * The {@link JwtService} is optionally referenced, if available then it is bind and {@link JwtFilter}
     * is registered with the {@link ResteasyProviderFactory}
     * <p>
     * Note: As per Felix SCR, dynamic references should be declared as volatile.
     */
    @Reference(
            bind = BIND_JWT_SERVICE,
            unbind = UNBIND_JWT_SERVICE,
            cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC
    )
    private volatile JwtService jwtService;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        long startTime = System.nanoTime();
        LOGGER.info("Initializing JaxRSDispatcherServlet!!");
        // Use Bundle ClassLoader as the context ClassLoader because we need to find the providers specified
        // in the file [META-INF/services/javax.ws.rs.Providers] file which will not be visible to the original
        // context ClassLoader which is the application class loader in fact.
        ClassLoaders.executeWith(JaxRSDispatcherServlet.class.getClassLoader(), () -> {
            try {
                // First let the RESTEasy framework bootstrap in super.init()
                super.init(servletConfig);
                Dispatcher dispatcher = this.getDispatcher();
                ResteasyProviderFactory providerFactory = dispatcher.getProviderFactory();
                this.jwtFilter = new JwtFilter(this.jwtService);
                providerFactory.register(new JaxRSCorsFeature(this.config))
                        .register(this.jwtFilter)
                        .register(new DefaultExceptionHandler(this.config.showException()))
                        .register(new JaxRSExceptionHandler(this.config.showException()));
                this.openProviderServiceTracker(this.context, providerFactory);
                this.openResourceServiceTracker(this.context, dispatcher.getRegistry());
                LOGGER.info(INIT_MSG, NANOSECONDS.toMillis(System.nanoTime() - startTime));
            } catch (Exception ex) { // NOSONAR
                LOGGER.error("Exception while initializing JaxRSDispatcherServlet!!", ex);
                throw new JaxRSInitializationException(ex.getMessage(), ex);
            }
        });
    }

    private void openResourceServiceTracker(BundleContext context, Registry registry) {
        this.resourceTracker = new ServiceTracker<>(context, anyServiceFilter(context, RES_FILTER_EXPR),
                new JaxRSResources(context, registry));
        this.resourceTracker.open();
    }

    private void openProviderServiceTracker(BundleContext context, ResteasyProviderFactory providerFactory) {
        this.providerTracker = new ServiceTracker<>(context, anyServiceFilter(context, PROVIDER_FILTER_EXPR),
                new JaxRSProviders(context, providerFactory));
        this.providerTracker.open();
    }

    // Lifecycle Methods

    protected void bindJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
        Optional.ofNullable(this.jwtFilter).ifPresent(filter -> filter.setJwtService(this.jwtService));
    }

    protected void unbindJwtService(JwtService jwtService) {
        this.jwtService = null;
        this.jwtFilter.setJwtService(this.jwtService);
    }

    @Activate
    protected void start(JaxRSCoreConfig config, BundleContext context) {
        this.config = config;
        this.context = context;
    }

    @Deactivate
    protected void stop() {
        this.providerTracker.close();
        this.resourceTracker.close();
    }
}
