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
import com.adeptj.modules.commons.utils.OSGiUtil;
import com.adeptj.modules.commons.utils.TimeUtil;
import com.adeptj.modules.commons.validator.service.ValidatorService;
import com.adeptj.modules.jaxrs.resteasy.GenericExceptionHandler;
import com.adeptj.modules.jaxrs.resteasy.ResteasyBootstrapException;
import org.jboss.resteasy.spi.Dispatcher;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import java.lang.invoke.MethodHandles;

import static com.adeptj.modules.jaxrs.resteasy.internal.ResteasyConstants.RESTEASY_DEPLOYMENT;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

/**
 * ResteasyLifecycle: Bootstraps RESTEasy Framework, open/close ServiceTracker for JAX-RS providers and resources.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = ResteasyConfig.class)
@Component(service = ResteasyLifecycle.class, scope = PROTOTYPE, configurationPolicy = REQUIRE)
public class ResteasyLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String JAXRS_RT_BOOTSTRAP_MSG = "JAX-RS Runtime bootstrapped in [{}] ms!!";

    private ProviderTracker providerTracker;

    private ResourceTracker resourceTracker;

    private ResteasyServletDispatcher resteasyDispatcher;

    // Activation objects start.

    private final ResteasyConfig config;

    private final BundleContext bundleContext;

    // Activation objects end.

    /**
     * Statically injected ValidatorService, this component will not become active until one is provided.
     */
    private final ValidatorService validatorService;

    @Activate
    public ResteasyLifecycle(@Reference ValidatorService vs, BundleContext bc, ResteasyConfig config) {
        this.validatorService = vs;
        this.bundleContext = bc;
        this.config = config;
    }

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
                this.resteasyDispatcher = new ResteasyServletDispatcher(this.config.blacklistedProviders());
                this.resteasyDispatcher.init(servletConfig);
                Dispatcher dispatcher = this.resteasyDispatcher.getDispatcher();
                ResteasyProviderFactory providerFactory = dispatcher.getProviderFactory()
                        .register(ResteasyUtil.newCorsFilter(this.config))
                        .register(new GenericExceptionHandler(this.config.sendExceptionTrace()))
                        .register(new ValidatorContextResolver(this.validatorService.getValidatorFactory()))
                        .register(new ObjectMapperContextResolver());
                this.providerTracker = new ProviderTracker(this.bundleContext, providerFactory);
                this.providerTracker.open();
                this.resourceTracker = new ResourceTracker(this.bundleContext, dispatcher.getRegistry());
                this.resourceTracker.open();
                LOGGER.info(JAXRS_RT_BOOTSTRAP_MSG, TimeUtil.elapsedMillis(startTime));
            } catch (Exception ex) { // NOSONAR
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
        this.resteasyDispatcher.destroy();
        ResteasyUtil.clearPreviousResteasyDeployment(servletConfig.getServletContext());
        LOGGER.info("ServletContext attribute [{}] removed!!", RESTEASY_DEPLOYMENT);
        OSGiUtil.closeQuietly(this.providerTracker);
        OSGiUtil.closeQuietly(this.resourceTracker);
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
}
