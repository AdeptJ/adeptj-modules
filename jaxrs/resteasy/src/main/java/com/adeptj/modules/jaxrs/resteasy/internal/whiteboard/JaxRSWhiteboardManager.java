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

package com.adeptj.modules.jaxrs.resteasy.internal.whiteboard;

import com.adeptj.modules.jaxrs.resteasy.JaxRSBootstrapException;
import com.adeptj.modules.jaxrs.resteasy.JaxRSCoreConfig;
import com.adeptj.modules.jaxrs.resteasy.internal.JaxRSProviderTracker;
import com.adeptj.modules.jaxrs.resteasy.internal.JaxRSResourceTracker;
import com.adeptj.modules.jaxrs.resteasy.internal.ResteasyUtil;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.validation.ValidatorFactory;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * WhiteboardManager bootstrap JAX-RS runtime, resources and providers.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class JaxRSWhiteboardManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRSWhiteboardManager.class);

    private static final String JAXRS_RT_BOOTSTRAP_MSG = "JAX-RS Runtime bootstrapped in [{}] ms!!";

    private List<ServiceTracker<Object, Object>> serviceTrackers;

    private WeakReference<ValidatorFactory> vfWeakRef;

    private final WeakReference<JaxRSCoreConfig> configWeakRef;

    private final BundleContext context;

    private HttpServlet30Dispatcher resteasyDispatcher;

    public JaxRSWhiteboardManager(BundleContext context, JaxRSCoreConfig config, ValidatorFactory vf) {
        this.context = context;
        this.configWeakRef = new WeakReference<>(config);
        this.serviceTrackers = new ArrayList<>();
        this.vfWeakRef = new WeakReference<>(vf);
    }

    public HttpServlet30Dispatcher getResteasyDispatcher() {
        return resteasyDispatcher;
    }

    /**
     * Bootstrap the RESTEasy Framework, open ServiceTracker for JAX-RS providers and resources.
     *
     * @param servletConfig the {@link ServletConfig} provided by OSGi HttpService.
     */
    public void start(ServletConfig servletConfig) {
        try {
            final long startTime = System.nanoTime();
            LOGGER.info("Bootstrapping JAX-RS Runtime!!");
            this.resteasyDispatcher = new HttpServlet30Dispatcher();
            this.resteasyDispatcher.init(servletConfig);
            Dispatcher dispatcher = this.resteasyDispatcher.getDispatcher();
            ResteasyProviderFactory providerFactory = dispatcher.getProviderFactory();
            ResteasyUtil.removeInternalValidators(providerFactory);
            ResteasyUtil.registerInternalProviders(providerFactory, this.configWeakRef.get(), this.vfWeakRef.get());
            this.serviceTrackers.add(new JaxRSProviderTracker(this.context, providerFactory));
            this.serviceTrackers.add(new JaxRSResourceTracker(this.context, dispatcher.getRegistry()));
            LOGGER.info(JAXRS_RT_BOOTSTRAP_MSG, NANOSECONDS.toMillis(System.nanoTime() - startTime));
        } catch (Exception ex) { // NOSONAR
            LOGGER.error("Exception while bootstrapping JAX-RS Runtime!!", ex);
            throw new JaxRSBootstrapException(ex.getMessage(), ex);
        }
    }

    /**
     * The WhiteboardManager will first close the {@link ServiceTracker} instances so that the OSGi service
     * instances can be released.
     * Finally call HttpServlet30Dispatcher#destroy so that RESTEasy can shutdown gracefully.
     */
    public void stop() {
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
        this.resteasyDispatcher.destroy();
        LOGGER.info("JAX-RS Runtime stopped!!");
    }
}
