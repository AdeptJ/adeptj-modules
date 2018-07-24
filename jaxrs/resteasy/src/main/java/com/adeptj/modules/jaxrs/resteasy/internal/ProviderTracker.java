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

import static com.adeptj.modules.commons.utils.Constants.AESTRISK;
import static com.adeptj.modules.jaxrs.core.JaxRSConstants.PROPERTY_PROVIDER_NAME;
import static com.adeptj.modules.jaxrs.resteasy.internal.ResteasyConstants.SERVICE_TRACKER_FORMAT;

/**
 * ProviderTracker is an OSGi ServiceTracker which registers the services annotated with JAX-RS &#064;Provider
 * annotation with RESTEasy provider registry.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class ProviderTracker extends ServiceTracker<Object, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderTracker.class);

    private static final String PROVIDER_FILTER_EXPR = String.format(SERVICE_TRACKER_FORMAT, PROPERTY_PROVIDER_NAME, AESTRISK);

    private ResteasyProviderFactory providerFactory;

    ProviderTracker(BundleContext context, ResteasyProviderFactory providerFactory) {
        super(context, OSGiUtil.anyServiceFilter(context, PROVIDER_FILTER_EXPR), null);
        this.providerFactory = providerFactory;
        this.open();
    }

    /**
     * Registers the JAX-RS provider with the RESTEasy {@link ResteasyProviderFactory}.
     *
     * @param reference The reference to the service being added to this {@code ServiceTracker}.
     * @return The service object to be tracked for the service added to this {@code ServiceTracker}.
     */
    @Override
    public Object addingService(ServiceReference<Object> reference) {
        Object resource = super.addingService(reference);
        if (resource == null) {
            LOGGER.warn("JAX-RS Provider is null for ServiceReference: {}", reference);
        } else {
            LOGGER.info("Adding JAX-RS Provider: [{}]", resource);
            this.providerFactory.register(resource);
        }
        return resource;
    }

    /**
     * Removes the given provider from RESTEasy {@link ResteasyProviderFactory} and registers again the modified service.
     *
     * @param reference the OSGi service reference of JAX-RS Provider.
     * @param service   the OSGi service of JAX-RS Provider.
     */
    @Override
    public void modifiedService(ServiceReference<Object> reference, Object service) {
        LOGGER.info("Service is modified, removing JAX-RS Provider: [{}]", service);
        ResteasyUtil.removeJaxRSProvider(this.providerFactory, service);
        LOGGER.info("Adding JAX-RS Provider again: [{}]", service);
        this.providerFactory.register(service);
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
        LOGGER.info("Removing JAX-RS Provider: [{}]", service);
        ResteasyUtil.removeJaxRSProvider(this.providerFactory, service);
    }
}
