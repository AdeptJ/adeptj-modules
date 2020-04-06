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

import com.adeptj.modules.commons.utils.OSGiUtil;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

import static com.adeptj.modules.jaxrs.resteasy.internal.ResteasyConstants.PROVIDER_TRACKER_FILTER;

/**
 * ProviderTracker is an OSGi ServiceTracker which registers the services annotated with JAX-RS &#064;Provider
 * annotation with RESTEasy provider registry.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class ProviderTracker extends ServiceTracker<Object, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ResteasyProviderFactory providerFactory;

    ProviderTracker(BundleContext context, ResteasyProviderFactory providerFactory) {
        super(context, OSGiUtil.anyServiceFilter(context, PROVIDER_TRACKER_FILTER), null);
        this.providerFactory = providerFactory;
    }

    ProviderTracker openTracker() {
        super.open();
        return this;
    }

    /**
     * Registers the JAX-RS provider with the RESTEasy {@link ResteasyProviderFactory}.
     *
     * @param reference The reference to the service being added to this {@code ServiceTracker}.
     * @return The service object to be tracked for the service added to this {@code ServiceTracker}.
     */
    @Override
    public Object addingService(ServiceReference<Object> reference) {
        Object provider = super.addingService(reference);
        // Quickly return null so that ServiceTracker will not track the instance.
        if (provider == null) {
            LOGGER.warn("JAX-RS Provider is null for ServiceReference: {}", reference);
            return null;
        }
        try {
            this.providerFactory.registerProviderInstance(provider);
            LOGGER.info("Registered JAX-RS Provider: [{}]", provider);
            return provider;
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Removes the JAX-RS provider from the RESTEasy {@link ResteasyProviderFactory}.
     *
     * @param reference The reference to removed service.
     * @param service   The service object for the removed service.
     */
    @Override
    public void removedService(ServiceReference<Object> reference, Object service) {
        super.removedService(reference, service);
        if (this.providerFactory.getProviderInstances().remove(service)) {
            LOGGER.info("Removed JAX-RS Provider: [{}]", service);
        }
    }
}
