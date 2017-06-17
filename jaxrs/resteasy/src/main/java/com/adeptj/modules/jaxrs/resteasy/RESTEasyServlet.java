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

import com.adeptj.modules.jaxrs.base.JaxRSAuthRepository;
import com.adeptj.modules.jaxrs.base.ValidateJWTFilter;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.validation.GeneralValidator;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang3.reflect.FieldUtils.getDeclaredField;


/**
 * RESTEasyServlet extends RESTEasy HttpServlet30Dispatcher so that Servlet 3.0 Async behaviour can be leveraged.
 * It also registers the JAX-RS resource ServiceTracker and GeneralValidatorContextResolver.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@Component(immediate = true, service = Servlet.class,
        property = {
                HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME + "=RESTEasy HttpServlet30Dispatcher",
                HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN + "=/*",
                HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED + "=true",
                HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX + "resteasy.servlet.mapping.prefix=/"
        })
public class RESTEasyServlet extends HttpServlet30Dispatcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(RESTEasyServlet.class);

	private static final long serialVersionUID = 8759503561853047365L;

    private static final String FIELD_CTX_RESOLVERS = "contextResolvers";

    private static final String FIELD_PROVIDER_CLASSES = "providerClasses";
    
    private ResourceTracker resourceTracker;

    private BundleContext context;

    @Reference
    private JaxRSAuthRepository authRepository;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
        ClassLoaders.executeWith(this.getClass().getClassLoader(), () -> {
            try {
                super.init(servletConfig);
                Dispatcher dispatcher = this.getDispatcher();
                ResteasyProviderFactory providerFactory = dispatcher.getProviderFactory();
                providerFactory.register(new ValidateJWTFilter(this.authRepository));
                this.registerContextResolver(providerFactory);
                this.resourceTracker = new ResourceTracker(this.context, dispatcher.getRegistry());
                this.resourceTracker.open();
                LOGGER.info("RESTEasyServlet initialized successfully!!");
            } catch (Exception ex) { // NOSONAR
                LOGGER.error("Exception while initializing RESTEasy HttpServletDispatcher!!", ex);
                throw new JaxRSInitializationException(ex);
            }
        });
    }

    private void registerContextResolver(ResteasyProviderFactory factory) {
        try {
            Map.class.cast(getDeclaredField(ResteasyProviderFactory.class, FIELD_CTX_RESOLVERS, true).get(factory))
                    .remove(GeneralValidator.class);
            Set.class.cast(getDeclaredField(ResteasyProviderFactory.class, FIELD_PROVIDER_CLASSES, true).get(factory))
                    .remove(GeneralValidatorContextResolver.class);
            factory.registerProvider(GeneralValidatorContextResolver.class);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            LOGGER.error("Exception while adding ContextResolver", ex);
        }
    }

    // LifeCycle Methods

    @Activate
    protected void activate(BundleContext context) {
	    this.context = context;
    }

    @Deactivate
    protected void deactivate() {
	    this.resourceTracker.close();
    }
}
