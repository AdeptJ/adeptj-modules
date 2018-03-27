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
import com.adeptj.modules.jaxrs.resteasy.JaxRSBootstrapException;
import com.adeptj.modules.jaxrs.resteasy.JaxRSCoreConfig;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.validation.ValidatorFactory;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * WhiteboardManager bootstrap JAX-RS runtime, resources and providers.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
class JaxRSWhiteboardManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRSWhiteboardManager.class);

    private static final String JAXRS_RT_BOOTSTRAP_MSG = "JAX-RS Runtime bootstrapped in [{}] ms!!";

    private ServiceTracker<Object, Object> resourceTracker;

    private ServiceTracker<Object, Object> providerTracker;

    private final BundleContext bundleContext;

    private final JaxRSCoreConfig config;

    private HttpServlet30Dispatcher resteasyDispatcher;

    JaxRSWhiteboardManager(BundleContext bundleContext, JaxRSCoreConfig config) {
        this.bundleContext = bundleContext;
        this.config = config;
    }

    HttpServlet30Dispatcher getResteasyDispatcher() {
        return resteasyDispatcher;
    }

    /**
     * Bootstraps the RESTEasy Framework using Bundle's ClassLoader as the context ClassLoader because
     * we need to find the providers specified in the file [META-INF/services/javax.ws.rs.Providers] file
     * which will not be visible to the original context ClassLoader which is the application ClassLoader itself.
     *
     * @param servletConfig the {@link ServletConfig} provided by OSGi HttpService.
     */
    void start(ServletConfig servletConfig, ValidatorFactory validatorFactory) {
        ClassLoaders.executeWith(this.getClass().getClassLoader(), () -> {
            try {
                final long startTime = System.nanoTime();
                LOGGER.info("Bootstrapping JAX-RS Runtime!!");
                this.resteasyDispatcher = new HttpServlet30Dispatcher();
                this.resteasyDispatcher.init(servletConfig);
                Dispatcher dispatcher = this.resteasyDispatcher.getDispatcher();
                ResteasyProviderFactory providerFactory = dispatcher.getProviderFactory();
                ResteasyUtil.removeInternalValidators(providerFactory);
                ResteasyUtil.registerInternalProviders(providerFactory, this.config, validatorFactory);
                this.providerTracker = ResteasyUtil.openProviderServiceTracker(this.bundleContext, providerFactory);
                this.resourceTracker = ResteasyUtil.openResourceServiceTracker(this.bundleContext, dispatcher.getRegistry());
                LOGGER.info(JAXRS_RT_BOOTSTRAP_MSG, NANOSECONDS.toMillis(System.nanoTime() - startTime));
            } catch (Exception ex) { // NOSONAR
                LOGGER.error("Exception while bootstrapping JAX-RS Runtime!!", ex);
                throw new JaxRSBootstrapException(ex.getMessage(), ex);
            }
        });
    }

    /**
     * The WhiteboardManager will first close the resource and provider {@link ServiceTracker} so that RESTEasy
     * can clean up them from its registry.
     * <p>
     * Then close the {@link ServiceTracker} so that the OSGi service instances can be released.
     * Finally call the destroy of super so that RESTEasy can cleanup remaining resources.
     */
    void stop() {
        Stream.of(this.providerTracker, this.resourceTracker)
                .filter(Objects::nonNull)
                .forEach(serviceTracker -> {
                    // Don't want the ServiceTracker#close call to prevent RESTEasy to do proper cleanup.
                    try {
                        serviceTracker.close();
                    } catch (Exception ex) { // NOSONAR
                        LOGGER.error("Exception while closing ServiceTracker instances!!", ex);
                    }
                });
        this.resteasyDispatcher.destroy();
        LOGGER.info("JAX-RS Runtime stopped!!");
    }
}
