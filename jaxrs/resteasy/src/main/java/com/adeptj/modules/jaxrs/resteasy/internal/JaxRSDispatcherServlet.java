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

import com.adeptj.modules.commons.validator.service.ValidatorService;
import com.adeptj.modules.jaxrs.resteasy.JaxRSCoreConfig;
import com.adeptj.modules.jaxrs.resteasy.ValidatorFactoryProvider;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import java.io.IOException;

import static com.adeptj.modules.jaxrs.resteasy.internal.JaxRSDispatcherServlet.ASYNC_SUPPORTED_TRUE;
import static com.adeptj.modules.jaxrs.resteasy.internal.JaxRSDispatcherServlet.EQ;
import static com.adeptj.modules.jaxrs.resteasy.internal.JaxRSDispatcherServlet.JAXRS_DISPATCHER_SERVLET_NAME;
import static com.adeptj.modules.jaxrs.resteasy.internal.JaxRSDispatcherServlet.MAPPING_PREFIX_VALUE;
import static com.adeptj.modules.jaxrs.resteasy.internal.JaxRSDispatcherServlet.SERVLET_PATTERN_VALUE;
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
public class JaxRSDispatcherServlet extends HttpServlet {

    static final String EQ = "=";

    static final String JAXRS_DISPATCHER_SERVLET_NAME = "AdeptJ JAX-RS DispatcherServlet";

    static final String SERVLET_PATTERN_VALUE = "/*";

    static final String ASYNC_SUPPORTED_TRUE = "true";

    static final String MAPPING_PREFIX_VALUE = "/";

    private static final String BIND_VALIDATOR_SERVICE = "bindValidatorService";

    private static final String UNBIND_VALIDATOR_SERVICE = "unbindValidatorService";

    private static final long serialVersionUID = -4415966373465265279L;

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRSDispatcherServlet.class);

    private JaxRSWhiteboardManager whiteboardManager;

    @Reference(
            bind = BIND_VALIDATOR_SERVICE,
            unbind = UNBIND_VALIDATOR_SERVICE,
            cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC
    )
    private ValidatorService validatorService;

    @Override
    public void init() {
        this.whiteboardManager.start(this.getServletConfig());
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        this.whiteboardManager.getResteasyDispatcher().service(req, res);
    }

    @Override
    public void destroy() {
        this.whiteboardManager.stop();
    }

    // --------------------------- INTERNAL ---------------------------
    // ---------------- Component lifecycle methods -------------------

    protected void bindValidatorService(ValidatorService validatorService) {
        LOGGER.info("Bind ValidatorService: [{}]", validatorService);
        this.validatorService = validatorService;
        ValidatorFactoryProvider.INSTANCE.setServiceValidatorFactory(this.validatorService.getValidatorFactory());
    }

    protected void unbindValidatorService(ValidatorService validatorService) {
        this.validatorService = null;
        ValidatorFactoryProvider.INSTANCE.setServiceValidatorFactory(null);
    }

    @Activate
    protected void start(BundleContext context, JaxRSCoreConfig config) {
        if (this.validatorService == null) {
            LOGGER.warn("ValidatorService is unavailable, will load the default ValidatorFactory!!");
        } else {
            ValidatorFactoryProvider.INSTANCE.setServiceValidatorFactory(this.validatorService.getValidatorFactory());
        }
        this.whiteboardManager = new JaxRSWhiteboardManager(context, config);
    }
}
