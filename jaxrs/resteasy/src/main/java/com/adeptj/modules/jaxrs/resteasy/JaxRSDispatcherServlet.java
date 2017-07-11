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

package com.adeptj.modules.jaxrs.resteasy;

import com.adeptj.modules.commons.utils.ClassLoaders;
import com.adeptj.modules.jaxrs.base.JwtCheckFilter;
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
                HTTP_WHITEBOARD_SERVLET_NAME + "=AdeptJ JAX-RS DispatcherServlet",
                HTTP_WHITEBOARD_SERVLET_PATTERN + "=/*",
                HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED + "=true",
                HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX + RESTEASY_SERVLET_MAPPING_PREFIX + "=/"
        })
public class JaxRSDispatcherServlet extends HttpServlet30Dispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRSDispatcherServlet.class);

    private static final long serialVersionUID = -4415966373465265279L;

    private static final String FIELD_CTX_RESOLVERS = "contextResolvers";

    private static final String SERVICE_FILTER = "(&(objectClass=*)(osgi.jaxrs.resource.base=*))";

    private static final String INIT_MSG = "JaxRSDispatcherServlet initialized in [{}] ms!!";

    private ServiceTracker<Object, Object> resourceTracker;

    private BundleContext context;

    private ResteasyProviderFactory providerFactory;

    private JaxRSCoreConfig config;

    /**
     * The {@link JwtService} is optionally referenced, if available then it is bind and {@link JwtCheckFilter}
     * is registered with the {@link ResteasyProviderFactory}
     */
    @Reference(
            bind = "bindJwtService",
            unbind = "unbindJwtService",
            cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC
    )
    private JwtService jwtService;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        long startTime = System.nanoTime();
        LOGGER.info("Initializing JaxRSDispatcherServlet!!");
        /*
        Use Bundle ClassLoader as the context ClassLoader because we need to find the providers specified
        in the file META-INF/services/javax.ws.rs.Providers file which will not be visible to the original
        context ClassLoader which is the application class loader in actual.
        */
        ClassLoaders.executeWith(JaxRSDispatcherServlet.class.getClassLoader(), () -> {
            try {
                // First bootstrap RESTEasy in super.init()
                super.init(servletConfig);
                Dispatcher dispatcher = this.getDispatcher();
                this.providerFactory = dispatcher.getProviderFactory();
                this.providerFactory.register(new JaxRSCorsFeature(this.config));
                this.removeDefaultGeneralValidator(providerFactory);
                this.providerFactory.registerProvider(GeneralValidatorContextResolver.class);
                this.openServiceTracker(dispatcher);
                LOGGER.info(INIT_MSG, NANOSECONDS.toMillis(System.nanoTime() - startTime));
            } catch (Exception ex) { // NOSONAR
                LOGGER.error("Exception while initializing RESTEasy HttpServletDispatcher!!", ex);
                throw new JaxRSInitializationException(ex.getMessage(), ex);
            }
        });
    }

    private void openServiceTracker(Dispatcher dispatcher) {
        this.resourceTracker = new ServiceTracker<>(this.context, anyServiceFilter(this.context, SERVICE_FILTER),
                new JaxRSResources(this.context, dispatcher.getRegistry()));
        this.resourceTracker.open();
    }

    private void removeDefaultGeneralValidator(ResteasyProviderFactory providerFactory) {
        try {
            // First remove the default RESTEasy GeneralValidator so that we can register ours.
            Object validator = Map.class
                    .cast(getDeclaredField(ResteasyProviderFactory.class, FIELD_CTX_RESOLVERS, true)
                            .get(providerFactory))
                    .remove(GeneralValidator.class);
            LOGGER.info("Removed RESTEasy GeneralValidator: [{}]", validator);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            LOGGER.error("Exception while removing GeneralValidator", ex);
        }
    }

    // LifeCycle Methods

    protected void bindJwtService(JwtService jwtService) {
        LOGGER.info("Bind JwtService!!");
        this.jwtService = jwtService;
        this.providerFactory.register(new JwtCheckFilter(this.jwtService));
    }

    protected void unbindJwtService(JwtService jwtService) {
        LOGGER.info("Unbind JwtService!!");
        this.jwtService = null;
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
