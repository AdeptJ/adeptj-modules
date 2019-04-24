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
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jboss.resteasy.core.providerfactory.ResteasyProviderFactoryImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * The {@link ResteasyProviderFactory} adapter sets the field providerInstances in ResteasyProviderFactoryImpl which is used in adding
 * and removing the provider instances through OSGi {@link org.osgi.util.tracker.ServiceTracker} mechanism.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class ResteasyProviderFactoryAdapter extends ResteasyProviderFactoryImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String FIELD_PROVIDER_INSTANCES = "providerInstances";

    private final String[] blacklistedProviders;

    private final Set<Object> providers;

    ResteasyProviderFactoryAdapter(String[] blacklistedProviders) throws ServletException {
        this.blacklistedProviders = blacklistedProviders;
        this.providers = new CopyOnWriteArraySet<>();
        // 27.03.2019 - Using reflection as the field is made private in 4.0.0.RC1, it was protected earlier.
        try {
            FieldUtils.writeField(this, FIELD_PROVIDER_INSTANCES, this.providers, true);
        } catch (IllegalAccessException iae) {
            throw new ServletException(iae);
        }
    }

    /**
     * See class header for description.
     *
     * @return the providers reference.
     */
    @Override
    public Set<Object> getProviderInstances() {
        return this.providers;
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
