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
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.util.Map;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.apache.commons.lang3.reflect.FieldUtils.getDeclaredField;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN;


/**
 * RESTEasyServlet extends RESTEasy HttpServlet30Dispatcher so that Servlet 3.0 Async behaviour can be leveraged.
 * It also registers the JAX-RS resource ServiceTracker and GeneralValidatorContextResolver.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = RESTEasyConfig.class)
@Component(immediate = true, service = Servlet.class, configurationPolicy = REQUIRE,
        property = {
                HTTP_WHITEBOARD_SERVLET_NAME + "=RESTEasy HttpServlet30Dispatcher",
                HTTP_WHITEBOARD_SERVLET_PATTERN + "=/*",
                HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED + "=true",
                HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX + "resteasy.servlet.mapping.prefix=/"
        })
public class RESTEasyServlet extends HttpServlet30Dispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(RESTEasyServlet.class);

    private static final long serialVersionUID = 8759503561853047365L;

    private static final String FIELD_CTX_RESOLVERS = "contextResolvers";

    private static final String JAXRS_SERVICE_FILTER = "(&(objectClass=*)(osgi.jaxrs.resource.base=*))";

    private static final String INIT_MSG = "RESTEasyServlet initialized in [{}] ms!!";

    private ServiceTracker<Object, Object> resourceTracker;

    private BundleContext context;

    private RESTEasyConfig config;

    @Reference
    private JwtService jwtService;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        long startTime = System.nanoTime();
        LOGGER.info("Initializing RESTEasyServlet!!");
        // Set Bundle ClassLoader as the context ClassLoader because we need to find the providers specified
        // in the file META-INF/services/javax.ws.rs.Providers which will not be visible to the original
        // context ClassLoader which is the application class loader in actual.
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        try {
            // First bootstrap RESTEasy in super.init()
            super.init(servletConfig);
            Dispatcher dispatcher = this.getDispatcher();
            ResteasyProviderFactory providerFactory = dispatcher.getProviderFactory();
            providerFactory.register(new JwtCheckFilter(this.jwtService));
            providerFactory.register(new CORSFeature(this.config));
            this.removeDefaultGeneralValidator(providerFactory);
            providerFactory.registerProvider(GeneralValidatorContextResolver.class);
            this.resourceTracker = new ServiceTracker<>(this.context, this.context.createFilter(JAXRS_SERVICE_FILTER),
                    new JaxRSResources(this.context, dispatcher.getRegistry()));
            this.resourceTracker.open();
            LOGGER.info(INIT_MSG, NANOSECONDS.toMillis(System.nanoTime() - startTime));
        } catch (Exception ex) { // NOSONAR
            LOGGER.error("Exception while initializing RESTEasy HttpServletDispatcher!!", ex);
            throw new JaxRSInitializationException(ex.getMessage(), ex);
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    private void removeDefaultGeneralValidator(ResteasyProviderFactory factory) {
        try {
            // First remove the default RESTEasy GeneralValidator so that we can register ours.
            Object validator = Map.class.cast(getDeclaredField(ResteasyProviderFactory.class, FIELD_CTX_RESOLVERS, true)
                    .get(factory))
                    .remove(GeneralValidator.class);
            LOGGER.info("Removed RESTEasy GeneralValidator: [{}]", validator);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            LOGGER.error("Exception while removing GeneralValidator", ex);
        }
    }

    // LifeCycle Methods

    @Activate
    protected void activate(RESTEasyConfig config, BundleContext context) {
        this.config = config;
        this.context = context;
    }

    @Deactivate
    protected void deactivate() {
        this.resourceTracker.close();
    }
}
