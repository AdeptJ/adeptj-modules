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
import com.adeptj.modules.jaxrs.resteasy.ResteasyBootstrapException;
import com.adeptj.modules.jaxrs.resteasy.contextresolver.JsonReaderFactoryContextResolver;
import com.adeptj.modules.jaxrs.resteasy.contextresolver.JsonWriterFactoryContextResolver;
import com.adeptj.modules.jaxrs.resteasy.contextresolver.JsonbContextResolver;
import com.adeptj.modules.jaxrs.resteasy.contextresolver.ValidatorContextResolver;
import com.adeptj.modules.jaxrs.resteasy.exceptionmapper.GenericExceptionMapper;
import com.adeptj.modules.jaxrs.resteasy.exceptionmapper.WebApplicationExceptionMapper;
import org.jboss.resteasy.spi.Dispatcher;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import java.lang.invoke.MethodHandles;

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

    private static final String JAX_RS_RUNTIME_BOOTSTRAP_MSG = "JAX-RS Runtime bootstrapped in [{}] ms!!";

    private CompositeServiceTracker<?> serviceTracker;

    private ResteasyDispatcher resteasyDispatcher;

    // Activation objects start.

    private final ResteasyConfig config;

    private final BundleContext context;

    // Activation objects end.

    /**
     * Statically injected ValidatorService, this component will not become active until one is provided.
     */
    private final ValidatorService validatorService;

    @Activate
    public ResteasyLifecycle(@Reference ValidatorService vs, BundleContext bc, ResteasyConfig config) {
        this.validatorService = vs;
        this.context = bc;
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
                // Remove previous ResteasyDeployment, if any.
                ResteasyUtil.removeResteasyDeployment(servletConfig.getServletContext());
                this.resteasyDispatcher = new ResteasyDispatcher(this.config.blacklistedProviders());
                this.resteasyDispatcher.init(servletConfig);
                Dispatcher dispatcher = this.resteasyDispatcher.getDispatcher();
                ResteasyProviderFactory providerFactory = dispatcher.getProviderFactory()
                        .register(ResteasyUtil.newCorsFilter(this.config))
                        .register(new GenericExceptionMapper(this.config.sendExceptionTrace()))
                        .register(new WebApplicationExceptionMapper())
                        .register(new ValidatorContextResolver(this.validatorService.getValidatorFactory()))
                        .register(new JsonbContextResolver())
                        .register(new JsonReaderFactoryContextResolver())
                        .register(new JsonWriterFactoryContextResolver());
                this.serviceTracker = new CompositeServiceTracker<>(this.context, providerFactory, dispatcher.getRegistry());
                this.serviceTracker.open();
                LOGGER.info(JAX_RS_RUNTIME_BOOTSTRAP_MSG, TimeUtil.elapsedMillis(startTime));
            } catch (Exception ex) { // NOSONAR
                LOGGER.error("Exception while bootstrapping JAX-RS Runtime!!", ex);
                throw new ResteasyBootstrapException(ex.getMessage(), ex);
            }
        });
    }

    /**
     * The ResteasyLifecycle will first close the {@link CompositeServiceTracker} instance so that the OSGi service
     * instances(JAX-RS providers and resources) can be released.
     * <p>
     * Finally call {@link ResteasyDispatcher#destroy} so that RESTEasy can be shutdown gracefully.
     *
     * @param servletConfig the {@link ServletConfig} provided by OSGi HttpService.
     */
    void stop(@NotNull ServletConfig servletConfig) {
        OSGiUtil.closeQuietly(this.serviceTracker);
        this.resteasyDispatcher.destroy();
        ResteasyUtil.removeResteasyDeployment(servletConfig.getServletContext());
        LOGGER.info("JAX-RS Runtime stopped!!");
    }

    /**
     * Gets the {@link ResteasyDispatcher}
     *
     * @return {@link ResteasyDispatcher}
     */
    ResteasyDispatcher getResteasyDispatcher() {
        return resteasyDispatcher;
    }
}
