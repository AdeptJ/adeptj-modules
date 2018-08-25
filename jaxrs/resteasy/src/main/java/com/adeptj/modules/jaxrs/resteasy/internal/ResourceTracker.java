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
import com.adeptj.modules.jaxrs.core.BaseResource;
import org.jboss.resteasy.spi.Registry;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

import static com.adeptj.modules.commons.utils.Constants.AESTRISK;
import static com.adeptj.modules.jaxrs.core.JaxRSConstants.PROPERTY_RESOURCE_NAME;
import static com.adeptj.modules.jaxrs.resteasy.internal.ResteasyConstants.SERVICE_TRACKER_FORMAT;

/**
 * ResourceTracker is an OSGi ServiceTracker which registers the services annotated with JAX-RS &#064;Path
 * annotation with RESTEasy resource registry.
 * <p>
 * Note: All the registered JAX-RS resources are singleton by default.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class ResourceTracker extends ServiceTracker<Object, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String RES_FILTER_EXPR = String.format(SERVICE_TRACKER_FORMAT, PROPERTY_RESOURCE_NAME, AESTRISK);

    private Registry registry;

    ResourceTracker(BundleContext context, Registry registry) {
        super(context, OSGiUtil.specificServiceOrFilter(context, BaseResource.class, RES_FILTER_EXPR), null);
        this.registry = registry;
        this.open();
    }

    /**
     * Registers the JAX-RS resource with the RESTEasy {@link Registry}.
     *
     * @param reference The reference to the service being added to this {@code ServiceTracker}.
     * @return The service object to be tracked for the service added to this {@code ServiceTracker}.
     */
    @Override
    public Object addingService(ServiceReference<Object> reference) {
        Object resource = super.addingService(reference);
        if (resource == null) {
            LOGGER.warn("JAX-RS Resource is null for ServiceReference: {}", reference);
            return null;
        } else if (ResteasyUtil.isNotAnnotatedWithPath(resource)) {
            LOGGER.warn("JAX-RS Resource [{}] isn't annotated with @Path, ignoring it!!", resource);
            return null;
        } else {
            LOGGER.info("Adding JAX-RS Resource: [{}]", resource);
            this.registry.addSingletonResource(resource);
            return resource;
        }
    }

    /**
     * Removes the given resource from RESTEasy {@link Registry} and registers again the modified service.
     *
     * @param reference the OSGi service reference of JAX-RS resource.
     * @param service   the OSGi service of JAX-RS resource.
     */
    @Override
    public void modifiedService(ServiceReference<Object> reference, Object service) {
        if (service == null) {
            LOGGER.warn("JAX-RS service is null, can't remove the resource from RESTEasy's registry!!");
            return;
        }
        LOGGER.info("Service is modified, removing JAX-RS Resource: [{}]", service);
        this.registry.removeRegistrations(service.getClass());
        if (ResteasyUtil.isNotAnnotatedWithPath(service)) {
            LOGGER.warn("JAX-RS Resource [{}] isn't annotated with @Path, ignoring it!!", service);
        } else {
            LOGGER.info("Adding JAX-RS Resource [{}] again!!", service);
            this.registry.addSingletonResource(service);
        }
    }

    /**
     * Removes the JAX-RS resource from the RESTEasy {@link Registry}.
     *
     * @param reference The reference to removed service.
     * @param service   The service object for the removed service.
     */
    @Override
    public void removedService(ServiceReference<Object> reference, Object service) {
        super.removedService(reference, service);
        LOGGER.info("Removing JAX-RS Resource: [{}]", service);
        this.registry.removeRegistrations(service.getClass());
    }
}
