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

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;

import static org.apache.commons.lang3.reflect.FieldUtils.getDeclaredField;

/**
 * JaxRSProviders is an OSGi ServiceTrackerCustomizer which registers the services annotated with JAX-RS
 * &#064;Provider annotation with RESTEasy provider registry.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class JaxRSProviders implements ServiceTrackerCustomizer<Object, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRSProviders.class);

    private static final String FIELD_PROVIDER_INSTANCES = "providerInstances";

    private ResteasyProviderFactory providerFactory;

    private BundleContext context;

    JaxRSProviders(BundleContext context, ResteasyProviderFactory providerFactory) {
        this.context = context;
        this.providerFactory = providerFactory;
    }

    @Override
    public Object addingService(ServiceReference<Object> reference) {
        Object resource = this.context.getService(reference);
        if (resource == null) {
            LOGGER.warn("JAX-RS Provider is null for ServiceReference: [{}]", reference);
        } else {
            LOGGER.info("Adding JAX-RS Provider: [{}]", resource);
            this.addProvider(resource);
        }
        return resource;
    }

    /**
     * Removes the given Provider from RESTEasy ProviderFactory and registers again the modified service.
     *
     * @param reference the OSGi service reference of JAX-RS Provider.
     * @param service   the OSGi service of JAX-RS Provider.
     */
    @Override
    public void modifiedService(ServiceReference<Object> reference, Object service) {
        LOGGER.info("Service is modified, removing JAX-RS Provider: [{}]", service);
        Optional.ofNullable(service).ifPresent(this::removeProvider);
        LOGGER.info("Adding JAX-RS Provider again: [{}]", service);
        this.addProvider(service);
    }

    @Override
    public void removedService(ServiceReference<Object> reference, Object service) {
        this.context.ungetService(reference);
        LOGGER.info("Removing JAX-RS Provider: [{}]", service);
        this.removeProvider(service);
    }

    private void addProvider(Object service) {
        this.providerFactory.register(service);
    }

    private void removeProvider(Object provider) {
        try {
            if (Set.class.cast(getDeclaredField(ResteasyProviderFactory.class, FIELD_PROVIDER_INSTANCES, true)
                    .get(this.providerFactory))
                    .remove(provider)) {
                LOGGER.info("Removed JAX-RS Provider: [{}]", provider);
            }
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            LOGGER.error("Exception while removing JAX-RS Provider!!", ex);
        }
    }
}
