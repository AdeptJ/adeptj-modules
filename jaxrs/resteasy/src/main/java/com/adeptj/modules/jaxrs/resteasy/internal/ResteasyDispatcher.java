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

import io.smallrye.config.SmallRyeConfigProviderResolver;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ServletBootstrap;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.invoke.MethodHandles;

/**
 * This class extends RESTEasy's {@link HttpServlet30Dispatcher} and does following.
 * <p>
 * 1. In {@link ResteasyDispatcher#init} create {@link ResteasyDeployment} and start it.
 * 2. Set the {@link ResteasyDeployment} as a servlet context attribute to be used by further bootstrapping process.
 * 3. Call {@link HttpServlet30Dispatcher#init} to do further bootstrapping.
 * 4. Once RESTEasy's {@link HttpServlet30Dispatcher} fully initialized, {@link #service} method becomes ready for
 * request processing.
 * 5. {@link #destroy()} method stops the {@link ResteasyDeployment}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class ResteasyDispatcher extends HttpServlet30Dispatcher {

    private static final long serialVersionUID = 983150981041495057L;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String PROCESSING_REQUEST_MSG = "Processing [{}] request for [{}]";

    private final transient ResteasyDeployment deployment;

    ResteasyDispatcher(@NotNull ServletConfig servletConfig, String[] providerDenyList) {
        // This is needed for resolving the ResteasyProxyServlet's init parameters by RESTEasy's ServletConfigSource.
        ResteasyContext.pushContext(ServletConfig.class, servletConfig);
        // This is needed by RESTEasy's ResteasyConfigProvider class in ConfigurationBootstrap.createDeployment().
        ConfigProviderResolver.setInstance(new SmallRyeConfigProviderResolver());
        this.deployment = new ServletBootstrap(servletConfig).createDeployment();
        this.deployment.setProviderFactory(new ExtendedResteasyProviderFactory(providerDenyList));
        this.deployment.start();
        LOGGER.info("ResteasyDeployment started!!");
        ResteasyUtil.addResteasyDeployment(servletConfig.getServletContext(), this.deployment);
    }

    @Override
    public void init(@NotNull ServletConfig servletConfig) throws ServletException {
        LOGGER.info("Initializing RESTEasy ServletContainerDispatcher!!");
        super.init(servletConfig);
        LOGGER.info("RESTEasy ServletContainerDispatcher Initialized!!");
        // Resteasy is bootstrapped now so ResteasyDeployment can be removed from ServletContext attributes.
        ResteasyUtil.removeResteasyDeployment(servletConfig.getServletContext());
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(PROCESSING_REQUEST_MSG, req.getMethod(), req.getRequestURI());
        }
        super.service(req.getMethod(), req, resp);
    }

    @Override
    public void destroy() {
        if (this.deployment != null) {
            this.deployment.stop();
            LOGGER.info("ResteasyDeployment stopped!!");
        }
    }
}
