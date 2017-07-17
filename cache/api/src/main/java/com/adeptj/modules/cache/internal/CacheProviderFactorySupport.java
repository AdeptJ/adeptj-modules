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

package com.adeptj.modules.cache.internal;

import com.adeptj.modules.cache.api.CacheProvider;
import com.adeptj.modules.cache.common.CacheProviderType;
import com.adeptj.modules.cache.spi.CacheProviderFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CacheProviderFactorySupport.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(immediate = true)
public class CacheProviderFactorySupport implements CacheProviderFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheProviderFactorySupport.class);

    /**
     * Collect all of the CacheProvider services as and when they become available.
     */
    @Reference(
            service = CacheProvider.class,
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            bind = "bindCacheProvider",
            unbind = "unbindCacheProvider")
    private final Map<String, CacheProvider> cacheProviders = new ConcurrentHashMap<>();

    @Override
    public Optional<CacheProvider> getCacheProvider(CacheProviderType providerType) {
        return Optional.ofNullable(this.cacheProviders.get(providerType.toString()));
    }

    protected void bindCacheProvider(CacheProvider provider) {
        LOGGER.info("Binding CacheProvider: [{}]", provider.getName());
        this.cacheProviders.put(provider.getName(), provider);
    }

    protected void unbindCacheProvider(CacheProvider provider) {
        LOGGER.info("Unbinding CacheProvider: [{}]", provider.getName());
        this.cacheProviders.remove(provider.getName());
    }
}
