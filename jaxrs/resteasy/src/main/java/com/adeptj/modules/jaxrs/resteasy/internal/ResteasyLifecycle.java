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
import com.adeptj.modules.jaxrs.resteasy.ApplicationExceptionMapper;
import com.adeptj.modules.jaxrs.resteasy.ResteasyBootstrapException;
import com.adeptj.modules.jaxrs.resteasy.ResteasyConfig;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.interceptors.CorsFilter;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.lang.invoke.MethodHandles;
import java.util.Optional;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

/**
 * ResteasyLifecycle: Bootstraps RESTEasy Framework, open/close ServiceTracker for JAX-RS providers and resources.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = ResteasyConfig.class)
@Component(service = ResteasyLifecycle.class, configurationPolicy = REQUIRE)
public class ResteasyLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String JAXRS_RT_BOOTSTRAP_MSG = "JAX-RS Runtime bootstrapped in [{}] ms!!";

    private boolean sendExceptionTrace;

    private ServiceTrackers serviceTrackers;

    private BundleContext bundleContext;

    private CorsFilter corsFilter;

    private ResteasyServletDispatcher resteasyDispatcher;

    /**
     * Statically injected ValidatorService, this component will not become active until one is provided.
     */
    @Reference(policyOption = GREEDY)
    private ValidatorService validatorService;

    /**
     * Bootstraps the RESTEasy Framework using this Bundle's ClassLoader as the context ClassLoader because
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
                this.resteasyDispatcher = new ResteasyServletDispatcher();
                this.resteasyDispatcher.init(servletConfig);
                // Now we have access to fully initialized Dispatcher which provides the ResteasyProviderFactory
                // and Registry instances.
                Dispatcher dispatcher = this.resteasyDispatcher.getDispatcher();
                dispatcher.getProviderFactory()
                        .register(new PriorityValidatorContextResolver(this.validatorService.getValidatorFactory()))
                        .register(new ApplicationExceptionMapper(this.sendExceptionTrace))
                        .register(this.corsFilter);
                this.serviceTrackers = new ServiceTrackers(this.bundleContext, dispatcher);
                this.serviceTrackers.openAll();
                LOGGER.info(JAXRS_RT_BOOTSTRAP_MSG, TimeUtil.elapsedMillis(startTime));
            } catch (Throwable ex) { // NOSONAR
                LOGGER.error("Exception while bootstrapping JAX-RS Runtime!!", ex);
                throw new ResteasyBootstrapException(ex.getMessage(), ex);
            }
        });
    }

    /**
     * The ResteasyLifecycle will first closeAll the {@link org.osgi.util.tracker.ServiceTracker} instances so that
     * the OSGi service instances can be released.
     * <p>
     * Finally call {@link ResteasyServletDispatcher#destroy} so that RESTEasy can be shutdown gracefully.
     *
     * @param servletConfig the {@link ServletConfig} provided by OSGi HttpService.
     */
    void stop(ServletConfig servletConfig) {
        Optional.ofNullable(this.serviceTrackers).ifPresent(ServiceTrackers::closeAll);
        ServletContext servletContext = servletConfig.getServletContext();
        Object deployment = servletContext.getAttribute(ResteasyDeployment.class.getName());
        if (deployment instanceof ResteasyDeployment) {
            try {
                ((ResteasyDeployment) deployment).stop();
                LOGGER.info("ResteasyDeployment stopped!!");
            } catch (Exception ex) { // NOSONAR
                LOGGER.error(ex.getMessage(), ex);
            }
        }
        this.resteasyDispatcher.destroy();
        servletContext.removeAttribute(ResteasyDeployment.class.getName());
        LOGGER.info("JAX-RS Runtime stopped!!");
    }

    /**
     * Gets the {@link ResteasyServletDispatcher}
     *
     * @return {@link ResteasyServletDispatcher}
     */
    ResteasyServletDispatcher getResteasyDispatcher() {
        return resteasyDispatcher;
    }

    // <------------------------------------------------ OSGi INTERNAL ------------------------------------------------>

    @Activate
    protected void start(BundleContext bundleContext, ResteasyConfig config) {
        this.bundleContext = bundleContext;
        this.corsFilter = ResteasyUtil.newCorsFilter(config);
        this.sendExceptionTrace = config.sendExceptionTrace();
    }
}
