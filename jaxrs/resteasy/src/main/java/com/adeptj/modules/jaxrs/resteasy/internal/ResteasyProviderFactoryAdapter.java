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

import org.jboss.resteasy.core.ResteasyProviderFactoryImpl;
import org.jboss.resteasy.plugins.providers.jackson.PatchMethodFilter;
import org.jboss.resteasy.plugins.validation.ValidatorContextResolver;
import org.jboss.resteasy.plugins.validation.ValidatorContextResolverCDI;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The {@link ResteasyProviderFactory} adapter provides the access to {@link #providerInstances} which is used in adding
 * and removing the provider instances through OSGi {@link org.osgi.util.tracker.ServiceTracker} mechanism.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class ResteasyProviderFactoryAdapter extends ResteasyProviderFactoryImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<Class<?>> blacklistedProviders;

    ResteasyProviderFactoryAdapter() {
        this.blacklistedProviders = new ArrayList<>(3);
        this.blacklistedProviders.add(PatchMethodFilter.class);
        this.blacklistedProviders.add(ValidatorContextResolver.class);
        this.blacklistedProviders.add(ValidatorContextResolverCDI.class);
    }

    /**
     * See class header for description.
     *
     * @return the provider instances.
     */
    @Override
    public Set<Object> getProviderInstances() {
        return super.providerInstances;
    }

    @Override
    public void registerProvider(Class provider, Integer priorityOverride, boolean isBuiltin, Map<Class<?>, Integer> contracts) {
        if (this.blacklistedProviders.contains(provider)) {
            LOGGER.info("Provider [{}] is blacklisted!!", provider);
        } else {
            super.registerProvider(provider, priorityOverride, isBuiltin, contracts);
        }
    }
}
