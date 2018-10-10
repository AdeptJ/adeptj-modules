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

import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ServletBootstrap;
import org.jboss.resteasy.spi.ResteasyDeployment;
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
 * 1. In {@link ResteasyServletDispatcher#init(ServletConfig)} create {@link ResteasyDeployment} and start it.
 * 2. Set the {@link ResteasyDeployment} as a servlet context attribute to be used by further bootstrapping process.
 * 3. Call {@link HttpServlet30Dispatcher#init(ServletConfig)} to do further bootstrapping.
 * 4. Once RESTEasy's {@link HttpServlet30Dispatcher} fully initialized, {@link #service(HttpServletRequest, HttpServletResponse)}
 * method becomes ready for request processing.
 * 5. {@link #destroy()} method stops the {@link ResteasyDeployment} and removes the corresponding servlet context attribute.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class ResteasyServletDispatcher extends HttpServlet30Dispatcher {

    private static final long serialVersionUID = 983150981041495057L;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        ResteasyDeployment deployment = new ServletBootstrap(servletConfig).createDeployment();
        deployment.setProviderFactory(new ResteasyProviderFactoryAdapter());
        deployment.start();
        LOGGER.info("ResteasyDeployment started!!");
        servletConfig.getServletContext().setAttribute(ResteasyDeployment.class.getName(), deployment);
        super.init(servletConfig);
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        super.service(req.getMethod(), req, resp);
    }

    @Override
    public void destroy() {
        super.destroy();
        ResteasyUtil.removeResteasyDeployment(this.getServletConfig());
        LOGGER.info("Removed [org.jboss.resteasy.spi.ResteasyDeployment] form ServletContext attributes!!");
    }
}
