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
 * ResteasyServletDispatcherWrapper.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class ResteasyServletDispatcherWrapper extends HttpServlet30Dispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        ServletBootstrap bootstrap = new ServletBootstrap(servletConfig);
        ResteasyDeployment deployment = bootstrap.createDeployment();
        deployment.setProviderFactory(new ResteasyProviderFactoryWrapper());
        deployment.start();
        servletConfig.getServletContext().setAttribute(ResteasyDeployment.class.getName(), deployment);
        super.init(servletConfig);
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.service(req, resp);
    }

    @Override
    public void destroy() {
        super.destroy();
        this.getServletConfig().getServletContext().removeAttribute(ResteasyDeployment.class.getName());
        LOGGER.info("Removed [org.jboss.resteasy.spi.ResteasyDeployment] form ServletContext attributes!!");
    }
}
