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

import com.adeptj.modules.jaxrs.resteasy.JaxRSCoreConfig;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * WhiteboardManager for JAX-RS resources and providers.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
class JaxRSWhiteboardManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRSWhiteboardManager.class);

    private ServiceTracker<Object, Object> resourceTracker;

    private ServiceTracker<Object, Object> providerTracker;

    private final BundleContext bundleContext;

    private final JaxRSCoreConfig config;

    JaxRSWhiteboardManager(BundleContext bundleContext, JaxRSCoreConfig config) {
        this.bundleContext = bundleContext;
        this.config = config;
    }

    void start(Dispatcher dispatcher) {
        ResteasyProviderFactory providerFactory = dispatcher.getProviderFactory();
        JaxRSUtil.removeDefaultValidators(providerFactory);
        JaxRSUtil.registerDefaultJaxRSProviders(providerFactory, this.config);
        this.providerTracker = JaxRSUtil.openProviderServiceTracker(this.bundleContext, providerFactory);
        this.resourceTracker = JaxRSUtil.openResourceServiceTracker(this.bundleContext, dispatcher.getRegistry());
    }

    void closeTrackers() {
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
    }
}
