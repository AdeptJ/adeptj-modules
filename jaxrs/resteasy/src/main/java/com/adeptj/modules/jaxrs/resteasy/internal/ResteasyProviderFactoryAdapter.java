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

import org.apache.commons.lang3.ArrayUtils;
import org.jboss.resteasy.core.providerfactory.ResteasyProviderFactoryImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Map;

/**
 * The {@link ResteasyProviderFactory} adapter sets the field providerInstances in ResteasyProviderFactoryImpl which is used in adding
 * and removing the provider instances through OSGi {@link org.osgi.util.tracker.ServiceTracker} mechanism.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class ResteasyProviderFactoryAdapter extends ResteasyProviderFactoryImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final String[] blacklistedProviders;

    ResteasyProviderFactoryAdapter(String[] blacklistedProviders) {
        this.blacklistedProviders = blacklistedProviders;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void registerProvider(Class provider, Integer priorityOverride, boolean isBuiltin, Map<Class<?>, Integer> contracts) {
        if (ArrayUtils.contains(this.blacklistedProviders, provider.getName())) {
            LOGGER.info("Provider [{}] is blacklisted!!", provider);
            return;
        }
        super.registerProvider(provider, priorityOverride, isBuiltin, contracts);
    }
}
