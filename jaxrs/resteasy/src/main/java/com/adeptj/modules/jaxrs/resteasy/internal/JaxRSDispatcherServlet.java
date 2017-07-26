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
import com.adeptj.modules.jaxrs.core.JwtFilter;
import com.adeptj.modules.jaxrs.resteasy.JaxRSCoreConfig;
import com.adeptj.modules.jaxrs.resteasy.JaxRSInitializationException;
import com.adeptj.modules.jaxrs.resteasy.error.DefaultExceptionHandler;
import com.adeptj.modules.security.jwt.JwtService;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.validation.GeneralValidator;
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
import java.util.Map;

import static com.adeptj.modules.commons.utils.OSGiUtils.anyServiceFilter;
import static com.adeptj.modules.jaxrs.resteasy.internal.JaxRSDispatcherServlet.ASYNC_SUPPORTED_TRUE;
import static com.adeptj.modules.jaxrs.resteasy.internal.JaxRSDispatcherServlet.JAXRS_DISPATCHER_SERVLET_NAME;
import static com.adeptj.modules.jaxrs.resteasy.internal.JaxRSDispatcherServlet.MAPPING_PREFIX_VALUE;
import static com.adeptj.modules.jaxrs.resteasy.internal.JaxRSDispatcherServlet.SERVLET_PATTERN_VALUE;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.apache.commons.lang3.reflect.FieldUtils.getDeclaredField;
import static org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN;


/**
 * JaxRSDispatcherServlet extends RESTEasy HttpServlet30Dispatcher so that Servlet 3.0 Async behaviour can be leveraged.
 * It also registers the JAX-RS resource ServiceTracker, GeneralValidatorContextResolver and other providers.
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

    private static final String INIT_MSG = "JaxRSDispatcherServlet initialized in [{}] ms!!";

    private static final String BIND_JWT_SERVICE = "bindJwtService";

    private static final String UNBIND_JWT_SERVICE = "unbindJwtService";

    static final String JAXRS_DISPATCHER_SERVLET_NAME = "=AdeptJ JAX-RS DispatcherServlet";

    static final String SERVLET_PATTERN_VALUE = "=/*";

    static final String ASYNC_SUPPORTED_TRUE = "=true";

    static final String MAPPING_PREFIX_VALUE = "=/";

    private ServiceTracker<Object, Object> resourceTracker;

    private BundleContext context;

    private ResteasyProviderFactory providerFactory;

    private JwtFilter jwtFilter;

    private JaxRSCoreConfig config;

    /**
     * The {@link JwtService} is optionally referenced, if available then it is bind and {@link JwtFilter}
     * is registered with the {@link ResteasyProviderFactory}
     *
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
                // First bootstrap RESTEasy in super.init()
                super.init(servletConfig);
                Dispatcher dispatcher = this.getDispatcher();
                this.providerFactory = dispatcher.getProviderFactory();
                this.providerFactory.register(new JaxRSCorsFeature(this.config));
                this.jwtFilter = new JwtFilter();
                this.jwtFilter.setJwtService(this.jwtService);
                LOGGER.info("Initialized JwtFilter with JwtService: [{}]", this.jwtService);
                this.providerFactory.register(this.jwtFilter);
                this.removeDefaultGeneralValidator(this.providerFactory);
                this.providerFactory.register(new GeneralValidatorContextResolver());
                this.providerFactory.register(new DefaultExceptionHandler(this.config.showException()));
                this.openServiceTracker(dispatcher);
                LOGGER.info(INIT_MSG, NANOSECONDS.toMillis(System.nanoTime() - startTime));
            } catch (Exception ex) { // NOSONAR
                LOGGER.error("Exception while initializing JaxRSDispatcherServlet!!", ex);
                throw new JaxRSInitializationException(ex.getMessage(), ex);
            }
        });
    }

    private void openServiceTracker(Dispatcher dispatcher) {
        this.resourceTracker = new ServiceTracker<>(this.context, anyServiceFilter(this.context, RES_FILTER_EXPR),
                new JaxRSResources(this.context, dispatcher.getRegistry()));
        this.resourceTracker.open();
    }

    private void removeDefaultGeneralValidator(ResteasyProviderFactory providerFactory) {
        try {
            // First remove the default RESTEasy GeneralValidator so that we can register ours.
            Map.class.cast(getDeclaredField(ResteasyProviderFactory.class, FIELD_CTX_RESOLVERS, true)
                            .get(providerFactory))
                    .remove(GeneralValidator.class);
            LOGGER.info("Removed RESTEasy GeneralValidator!!");
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            LOGGER.error("Exception while removing RESTEasy GeneralValidator", ex);
        }
    }

    // LifeCycle Methods

    protected void bindJwtService(JwtService jwtService) {
        LOGGER.info("Bind: [{}]", jwtService);
        this.jwtService = jwtService;
        if (this.jwtFilter == null) {
            LOGGER.warn("Can't inject JwtService as JwtFilter not yet initialized!!");
        } else {
            this.jwtFilter.setJwtService(this.jwtService);
        }
    }

    protected void unbindJwtService(JwtService jwtService) {
        LOGGER.info("Unbind: [{}]", jwtService);
        this.jwtService = null;
        this.jwtFilter.setJwtService(null);
    }

    @Activate
    protected void start(JaxRSCoreConfig config, BundleContext context) {
        this.config = config;
        this.context = context;
    }

    @Deactivate
    protected void stop() {
        this.resourceTracker.close();
    }
}
