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
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Set;

/**
 * The {@link ResteasyProviderFactory} extension which adds the support of skipping the registration of desired
 * provider instances as per the {@link #providerDenyList} array.
 * <p>
 * It also overrides the {@link #getProviderInstances} to expose the {@link #providerInstances} Set which helps in
 * removing the provider instances through {@link ProviderManager#removeProvider}, this is needed because the super
 * method returns the unmodifiable copy of {@link #providerInstances} Set which doesn't allow removal of any element.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class ExtendedResteasyProviderFactory extends ResteasyProviderFactoryImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final String[] providerDenyList;

    ExtendedResteasyProviderFactory(String[] providerDenyList) {
        this.providerDenyList = providerDenyList;
    }

    @Override
    public void registerProvider(@NotNull Class provider, Integer priorityOverride, boolean isBuiltin, Map<Class<?>, Integer> contracts) {
        if (ArrayUtils.contains(this.providerDenyList, provider.getName())) {
            LOGGER.info("Provider [{}] is skipped from deployment!!", provider.getName());
        } else {
            super.registerProvider(provider, priorityOverride, isBuiltin, contracts);
        }
    }

    @Override
    public Set<Object> getProviderInstances() {
        return super.providerInstances;
    }
}
