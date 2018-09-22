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

import com.adeptj.modules.commons.utils.Functions;
import com.adeptj.modules.commons.utils.TimeUtil;
import com.adeptj.modules.commons.validator.service.ValidatorService;
import com.adeptj.modules.jaxrs.resteasy.ResteasyBootstrapException;
import com.adeptj.modules.jaxrs.resteasy.ResteasyConfig;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * ResteasyLifecycle: Bootstraps RESTEasy Framework, open/close ServiceTracker for JAX-RS providers and resources
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = ResteasyConfig.class)
@Component(service = ResteasyLifecycle.class, configurationPolicy = REQUIRE)
public class ResteasyLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String JAXRS_RT_BOOTSTRAP_MSG = "JAX-RS Runtime bootstrapped in [{}] ms!!";

    private List<ServiceTracker<Object, Object>> serviceTrackers;

    private ResteasyConfig config;

    private BundleContext bundleContext;

    private HttpServlet30Dispatcher resteasyServletDispatcher;

    /**
     * Statically injected ValidatorService, this component will not resolve until one is provided.
     */
    @Reference
    private ValidatorService validatorService;

    /**
     * Bootstraps the RESTEasy Framework using Bundle's ClassLoader as the context ClassLoader because
     * we need to find the providers specified in the file [META-INF/services/javax.ws.rs.Providers] file
     * which will not be visible to the original context ClassLoader which is the application ClassLoader itself.
     *
     * @param servletConfig the {@link ServletConfig} provided by OSGi HttpService.
     */
    void start(ServletConfig servletConfig) {
        Functions.execute(this.getClass().getClassLoader(), () -> {
            try {
                long startTime = System.nanoTime();
                LOGGER.info("Bootstrapping JAX-RS Runtime!!");
                this.resteasyServletDispatcher = new HttpServlet30Dispatcher();
                this.resteasyServletDispatcher.init(servletConfig);
                Dispatcher dispatcher = this.resteasyServletDispatcher.getDispatcher();
                ResteasyProviderFactory rpf = dispatcher.getProviderFactory();
                ResteasyUtil.removeDefaultValidatorContextResolvers(rpf);
                ResteasyUtil.registerProviders(rpf, this.config, this.validatorService.getValidatorFactory());
                this.serviceTrackers.add(new ProviderTracker(this.bundleContext, rpf));
                this.serviceTrackers.add(new ResourceTracker(this.bundleContext, dispatcher.getRegistry()));
                LOGGER.info(JAXRS_RT_BOOTSTRAP_MSG, TimeUtil.elapsedMillis(startTime));
            } catch (Exception ex) { // NOSONAR
                LOGGER.error("Exception while bootstrapping JAX-RS Runtime!!", ex);
                throw new ResteasyBootstrapException(ex.getMessage(), ex);
            }
        });
    }

    /**
     * The ResteasyLifecycle will first close the {@link org.osgi.util.tracker.ServiceTracker} instances so that
     * the OSGi service instances can be released.
     * <p>
     * Finally call HttpServlet30Dispatcher#destroy so that RESTEasy can be shutdown gracefully.
     */
    void stop() {
        this.serviceTrackers
                .stream()
                .filter(Objects::nonNull)
                .forEach(serviceTracker -> {
                    // Don't want the ServiceTracker#close call to prevent RESTEasy to do proper cleanup.
                    try {
                        serviceTracker.close();
                    } catch (Exception ex) { // NOSONAR
                        LOGGER.error("Exception while closing ServiceTracker instances!!", ex);
                    }
                });
        this.resteasyServletDispatcher.destroy();
        LOGGER.info("JAX-RS Runtime stopped!!");
    }

    /**
     * Gets the RESTEasy's {@link HttpServlet30Dispatcher}
     *
     * @return RESTEasy's {@link HttpServlet30Dispatcher}
     */
    HttpServlet30Dispatcher getResteasyServletDispatcher() {
        return resteasyServletDispatcher;
    }

    // ------------------------------------------------- OSGi INTERNAL -------------------------------------------------

    @Activate
    protected void start(BundleContext bundleContext, ResteasyConfig config) {
        this.bundleContext = bundleContext;
        this.config = config;
        this.serviceTrackers = new ArrayList<>();
    }
}
