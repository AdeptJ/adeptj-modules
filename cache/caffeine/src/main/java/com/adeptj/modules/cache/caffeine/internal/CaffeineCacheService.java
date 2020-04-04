/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://adeptj.com)                               #
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

package com.adeptj.modules.cache.caffeine.internal;

import com.adeptj.modules.cache.caffeine.Cache;
import com.adeptj.modules.cache.caffeine.CacheService;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

/**
 * Default implementation of {@link CacheService}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component
public class CaffeineCacheService implements CacheService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<CaffeineCache<?, ?>> caches;

    public CaffeineCacheService() {
        this.caches = new CopyOnWriteArrayList<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <K, V> Cache<K, V> getCache(String cacheName) {
        return (Cache<K, V>) this.caches.stream()
                .filter(cache -> cache.getName().equals(cacheName))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("No cache exists with name [%s]!!", cacheName)));
    }

    @Override
    public void evictCache(String cacheName) {
        this.caches.stream()
                .filter(cache -> cache.getName().equals(cacheName))
                .forEach(CaffeineCache::clear);
    }

    @Override
    public void evictAllCaches() {
        this.caches.forEach(cache -> {
            try {
                LOGGER.info("Invalidating cache: [{}]", cache.getName());
                cache.clear();
            } catch (Exception ex) { // NOSONAR
                LOGGER.error(ex.getMessage(), ex);
            }
        });
    }

    // <<------------------------------------------- OSGi INTERNAL ------------------------------------------->>

    @Deactivate
    protected void stop() {
        this.evictAllCaches();
        this.caches.clear();
    }

    @Reference(service = CaffeineCacheConfigFactory.class, cardinality = MULTIPLE, policy = DYNAMIC)
    public void bindCaffeineCacheFactory(CaffeineCacheConfigFactory cacheConfigFactory) {
        CaffeineCacheConfig cacheConfig = cacheConfigFactory.getCacheConfig();
        this.caches.add(new CaffeineCache<>(cacheConfig.cache_name(), Caffeine.from(cacheConfig.cache_spec()).build()));
    }

    public void unbindCaffeineCacheFactory(CaffeineCacheConfigFactory cacheConfigFactory) {
        String cacheName = cacheConfigFactory.getCacheConfig().cache_name();
        this.evictCache(cacheName);
        this.caches.removeIf(cache -> StringUtils.equals(cache.getName(), cacheName));
    }
}
