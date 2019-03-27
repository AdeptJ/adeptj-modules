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
import org.jboss.resteasy.plugins.interceptors.CorsFilter;
import org.jboss.resteasy.spi.Dispatcher;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.adeptj.modules.jaxrs.resteasy.internal.ResteasyConstants.RESTEASY_DEPLOYMENT;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;
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

    private boolean sendExceptionTrace;

    private ProviderTracker providerTracker;

    private ResourceTracker resourceTracker;

    private CorsFilter corsFilter;

    private ResteasyServletDispatcher resteasyDispatcher;

    private List<String> blacklistedProviders;

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
                this.resteasyDispatcher.bootstrap(servletConfig, this.blacklistedProviders);
                Dispatcher dispatcher = this.resteasyDispatcher.getDispatcher();
                this.providerTracker
                        .setResteasyProviderFactory(dispatcher.getProviderFactory()
                                .register(this.corsFilter)
                                .register(new ApplicationExceptionMapper(this.sendExceptionTrace))
                                .register(new ValidatorContextResolver(this.validatorService.getValidatorFactory())))
                        .open();
                this.resourceTracker
                        .setRegistry(dispatcher.getRegistry())
                        .open();
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
        servletConfig.getServletContext().removeAttribute(RESTEASY_DEPLOYMENT);
        LOGGER.info("ServletContext attribute [{}] removed!!", RESTEASY_DEPLOYMENT);
        Stream.of(this.providerTracker, this.resourceTracker)
                .filter(tracker -> !tracker.isEmpty())
                .forEach(tracker -> {
                    try {
                        tracker.close();
                        LOGGER.info("ServiceTracker [{}] closed!!", tracker);
                    } catch (Exception ex) { // NOSONAR
                        LOGGER.error(ex.getMessage(), ex);
                    }
                });
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

    // <--------------------------------------------- OSGi INTERNAL --------------------------------------------->

    @Activate
    protected void start(BundleContext bundleContext, ResteasyConfig config) {
        this.providerTracker = new ProviderTracker(bundleContext);
        this.resourceTracker = new ResourceTracker(bundleContext);
        this.corsFilter = ResteasyUtil.newCorsFilter(config);
        this.sendExceptionTrace = config.sendExceptionTrace();
        this.blacklistedProviders = Arrays.asList(config.blacklistedProviders());
    }
}
