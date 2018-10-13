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
import org.apache.commons.lang3.ArrayUtils;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

import static com.adeptj.modules.jaxrs.core.JaxRSConstants.PROPERTY_PROVIDER_NAME;
import static com.adeptj.modules.jaxrs.core.JaxRSConstants.PROPERTY_RESOURCE_NAME;
import static com.adeptj.modules.jaxrs.resteasy.internal.ResteasyConstants.SERVICE_TRACKER_FORMAT;

/**
 * ProviderTracker is an OSGi ServiceTracker which registers the services annotated with JAX-RS &#064;Provider
 * annotation with RESTEasy provider registry.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class CompositeTracker extends ServiceTracker<Object, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String FILTER_EXPR = String.format(SERVICE_TRACKER_FORMAT, PROPERTY_PROVIDER_NAME, PROPERTY_RESOURCE_NAME);

    private final ResteasyProviderFactory providerFactory;

    private final Registry registry;

    CompositeTracker(BundleContext context, Dispatcher dispatcher) {
        super(context, OSGiUtil.anyServiceFilter(context, FILTER_EXPR), null);
        this.providerFactory = dispatcher.getProviderFactory();
        this.registry = dispatcher.getRegistry();
    }

    /**
     * Registers the JAX-RS provider with the RESTEasy {@link ResteasyProviderFactory}.
     *
     * @param reference The reference to the service being added to this {@code ServiceTracker}.
     * @return The service object to be tracked for the service added to this {@code ServiceTracker}.
     */
    @Override
    public Object addingService(ServiceReference<Object> reference) {
        Object service = super.addingService(reference);
        // Quickly return null so that ServiceTracker will not track the instance.
        if (service == null) {
            LOGGER.warn("OSGi service is null for ServiceReference: {}", reference);
            return null;
        }
        if (ArrayUtils.contains(reference.getPropertyKeys(), PROPERTY_PROVIDER_NAME)) {
            LOGGER.info("Adding JAX-RS Provider: [{}]", service);
            this.providerFactory.registerProviderInstance(service);
            return service;
        }
        if (ResteasyUtil.isPathAnnotationPresent(service)) {
            LOGGER.info("Adding JAX-RS Resource: [{}]", service);
            this.registry.addSingletonResource(service);
            return service;
        }
        return null;
    }

    /**
     * Removes the given provider from RESTEasy {@link ResteasyProviderFactory} and registers again the modified service.
     *
     * @param reference the OSGi service reference of JAX-RS Provider.
     * @param service   the OSGi service of JAX-RS Provider.
     */
    @Override
    public void modifiedService(ServiceReference<Object> reference, Object service) {
        if (ArrayUtils.contains(reference.getPropertyKeys(), PROPERTY_PROVIDER_NAME)) {
            LOGGER.info("Service is modified, removing JAX-RS Provider: [{}]", service);
            if (this.providerFactory.getProviderInstances().remove(service)) {
                LOGGER.info("Removed JAX-RS Provider: [{}]", service);
            }
            LOGGER.info("Adding JAX-RS Provider again: [{}]", service);
            this.providerFactory.registerProviderInstance(service);
        } else {
            LOGGER.info("Service is modified, removing JAX-RS Resource: [{}]", service);
            this.registry.removeRegistrations(service.getClass());
            LOGGER.info("Adding JAX-RS Resource [{}] again!!", service);
            this.registry.addSingletonResource(service);
        }
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
        if (ArrayUtils.contains(reference.getPropertyKeys(), PROPERTY_PROVIDER_NAME)) {
            LOGGER.info("Removing JAX-RS Provider: [{}]", service);
            if (this.providerFactory.getProviderInstances().remove(service)) {
                LOGGER.info("Removed JAX-RS Provider: [{}]", service);
            }
        } else {
            LOGGER.info("Removing JAX-RS Resource: [{}]", service);
            this.registry.removeRegistrations(service.getClass());
        }
    }
}
