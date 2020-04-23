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
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ProviderManager adds the JAX-RS provider to the RESTEasy {@link ResteasyProviderFactory}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class ProviderManager<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Lock lock;

    private final ResteasyProviderFactory providerFactory;

    ProviderManager(ResteasyProviderFactory providerFactory) {
        this.providerFactory = providerFactory;
        this.lock = new ReentrantLock(true);
    }

    /**
     * Registers the JAX-RS provider with the RESTEasy {@link ResteasyProviderFactory}.
     *
     * @param reference The reference to the provider service being added by the ServiceTracker.
     * @return The provider service object to be tracked for the service added to the ServiceTracker.
     */
    public T addProvider(ServiceReference<T> reference, T provider) {
        // Quickly return null so that ServiceTracker will not track the instance.
        if (provider == null) {
            LOGGER.warn("JAX-RS Provider is null for ServiceReference: {}", ResteasyUtil.getProviderName(reference));
            return null;
        }
        this.lock.lock();
        try {
            this.providerFactory.registerProviderInstance(provider);
            LOGGER.info("Registered JAX-RS Provider: [{}]", ResteasyUtil.getProviderName(reference));
            return provider;
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            this.lock.unlock();
        }
        return null;
    }

    /**
     * Removes the JAX-RS provider from the RESTEasy {@link ResteasyProviderFactory}.
     *
     * @param provider The service object for the removed service.
     */
    public void removeProvider(ServiceReference<T> reference, T provider) {
        this.lock.lock();
        try {
            if (this.providerFactory.getProviderInstances().remove(provider)) {
                LOGGER.info("Removed JAX-RS Provider: [{}]", ResteasyUtil.getProviderName(reference));
            }
        } finally {
            this.lock.unlock();
        }
    }
}
