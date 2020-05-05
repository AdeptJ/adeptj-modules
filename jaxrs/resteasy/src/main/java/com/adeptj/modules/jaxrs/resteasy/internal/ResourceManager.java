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

import org.jboss.resteasy.spi.Registry;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ResourceManager adds the JAX-RS resource to the RESTEasy {@link Registry}
 * <p>
 * Note: All the registered JAX-RS resources are singleton by default.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class ResourceManager<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Lock lock;

    private final Registry registry;

    ResourceManager(Registry registry) {
        this.registry = registry;
        this.lock = new ReentrantLock();
    }

    /**
     * Registers the JAX-RS resource with the RESTEasy {@link Registry}.
     *
     * @param reference The reference to the resource service being added by the ServiceTracker.
     * @return The resource service object to be tracked for the service added to the ServiceTracker.
     */
    public T addResource(ServiceReference<T> reference, T resource) {
        // Quickly return null so that ServiceTracker will not track the instance.
        if (resource == null) {
            LOGGER.warn("JAX-RS Resource is null for ServiceReference: {}", ResteasyUtil.getResourceName(reference));
            return null;
        }
        this.lock.lock();
        try {
            this.registry.addSingletonResource(resource);
            LOGGER.info("Added JAX-RS Resource: [{}]", ResteasyUtil.getResourceName(reference));
            return resource;
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            this.lock.unlock();
        }
        return null;
    }

    /**
     * Removes the JAX-RS resource from the RESTEasy {@link Registry}.
     *
     * @param resource The service object for the removed service.
     */
    public void removeResource(ServiceReference<T> reference, @NotNull Object resource) {
        this.lock.lock();
        try {
            this.registry.removeRegistrations(resource.getClass());
            LOGGER.info("Removed JAX-RS Resource: [{}]", ResteasyUtil.getResourceName(reference));
        } finally {
            this.lock.unlock();
        }
    }
}
