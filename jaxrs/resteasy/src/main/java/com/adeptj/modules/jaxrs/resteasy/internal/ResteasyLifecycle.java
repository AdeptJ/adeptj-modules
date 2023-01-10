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

import com.adeptj.modules.commons.utils.ClassLoaders;
import com.adeptj.modules.commons.utils.OSGiUtil;
import com.adeptj.modules.commons.utils.TimeUtil;
import com.adeptj.modules.commons.validator.ValidatorService;
import com.adeptj.modules.jaxrs.resteasy.contextresolver.JsonReaderFactoryContextResolver;
import com.adeptj.modules.jaxrs.resteasy.contextresolver.JsonWriterFactoryContextResolver;
import com.adeptj.modules.jaxrs.resteasy.contextresolver.JsonbContextResolver;
import com.adeptj.modules.jaxrs.resteasy.contextresolver.ValidatorContextResolver;
import com.adeptj.modules.jaxrs.resteasy.exceptionmapper.GenericExceptionMapper;
import com.adeptj.modules.jaxrs.resteasy.exceptionmapper.WebApplicationExceptionMapper;
import jakarta.servlet.ServletConfig;
import jakarta.ws.rs.container.DynamicFeature;
import org.jboss.resteasy.plugins.interceptors.CorsFilter;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;

import static com.adeptj.modules.commons.utils.Constants.COMMA;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * ResteasyLifecycle: Bootstraps RESTEasy Framework, open/close ServiceTracker for JAX-RS providers and resources.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = ResteasyConfig.class)
@Component(service = ResteasyLifecycle.class, configurationPolicy = REQUIRE)
public class ResteasyLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String JAX_RS_RUNTIME_BOOTSTRAP_MSG = "JAX-RS Runtime bootstrapped in [{}] ms!!";

    private static final String SERVICE_FILTER = "(feature.name=JwtDynamicFeature)";

    // Activation objects start.

    private final ResteasyConfig config;

    private final BundleContext context;

    // Activation objects end.

    private CompositeServiceTracker<?> serviceTracker;

    private ResteasyDispatcher resteasyDispatcher;

    /**
     * Statically injected ValidatorService, this component will not become active until one is provided.
     */
    private final ValidatorService validatorService;

    private final DynamicFeature jwtDynamicFeature;

    @Activate
    public ResteasyLifecycle(@Reference ValidatorService validatorService,
                             @Reference(target = SERVICE_FILTER) DynamicFeature dynamicFeature,
                             BundleContext context,
                             ResteasyConfig config) {
        this.validatorService = validatorService;
        this.jwtDynamicFeature = dynamicFeature;
        this.context = context;
        this.config = config;
    }

    /**
     * Bootstraps the RESTEasy Framework using this Bundle's ClassLoader as the context ClassLoader because
     * we need to find the providers specified in the file [META-INF/services/javax.ws.rs.Providers] file
     * which will not be visible to the original context ClassLoader which is the application ClassLoader itself.
     *
     * @param servletConfig the {@link ServletConfig} provided by OSGi HttpService.
     */
    public void start(ServletConfig servletConfig) {
        ClassLoaders.executeUnderContextClassLoader(this.getClass().getClassLoader(), () -> this.doStart(servletConfig));
    }

    private void doStart(ServletConfig servletConfig) {
        long startTime = System.nanoTime();
        LOGGER.info("Bootstrapping JAX-RS Runtime!!");
        try {
            // Remove previous ResteasyDeployment from ServletContext attributes, just in case there is any.
            ResteasyUtil.removeResteasyDeployment(servletConfig.getServletContext());
            ResteasyProviderFactory providerFactory = this.initResteasyProviderFactory();
            this.resteasyDispatcher = new ResteasyDispatcher(servletConfig, providerFactory);
            this.resteasyDispatcher.init(servletConfig);
            Registry registry = this.resteasyDispatcher.getDispatcher().getRegistry();
            this.serviceTracker = new CompositeServiceTracker<>(this.context, providerFactory, registry);
            this.serviceTracker.open();
            LOGGER.info(JAX_RS_RUNTIME_BOOTSTRAP_MSG, TimeUtil.elapsedMillis(startTime));
        } catch (Exception ex) { // NOSONAR
            LOGGER.error("Exception while bootstrapping JAX-RS Runtime!!", ex);
            throw new ResteasyBootstrapException(ex.getMessage(), ex);
        }
    }

    private ResteasyProviderFactory initResteasyProviderFactory() {
        return new ResteasyProviderFactoryAdapter(this.config.provider_skip_list())
                .register(this.initCorsFilter(this.config))
                .register(new GenericExceptionMapper(this.config.send_exception_trace()))
                .register(new WebApplicationExceptionMapper(this.config.log_web_application_exception()))
                .register(new ValidatorContextResolver(this.validatorService))
                .register(new JsonbContextResolver())
                .register(new JsonReaderFactoryContextResolver())
                .register(new JsonWriterFactoryContextResolver())
                .register(this.jwtDynamicFeature);
    }

    private @NotNull CorsFilter initCorsFilter(@NotNull ResteasyConfig config) {
        CorsFilter corsFilter = new CorsFilter();
        corsFilter.setAllowCredentials(config.allow_credentials());
        corsFilter.setCorsMaxAge(config.cors_max_age());
        corsFilter.setExposedHeaders(String.join(COMMA, config.exposed_headers()));
        corsFilter.setAllowedMethods(String.join(COMMA, config.allowed_methods()));
        corsFilter.setAllowedHeaders(String.join(COMMA, config.allowed_headers()));
        corsFilter.getAllowedOrigins().addAll(Arrays.asList(config.allowed_origins()));
        return corsFilter;
    }

    /**
     * The ResteasyLifecycle will first close the {@link CompositeServiceTracker} instance so that the OSGi service
     * instances(JAX-RS providers and resources) can be released.
     * <p>
     * Finally, call {@link ResteasyDispatcher#destroy} so that RESTEasy can be shutdown gracefully.
     *
     * @param servletConfig the {@link ServletConfig} provided by OSGi HttpService.
     */
    public void stop(@NotNull ServletConfig servletConfig) {
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
    public ResteasyDispatcher getResteasyDispatcher() {
        return resteasyDispatcher;
    }
}
