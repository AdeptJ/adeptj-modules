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
import com.adeptj.modules.cache.caffeine.CaffeineCacheConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

/**
 * Caffeine cache based implementation of {@link CacheService}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(immediate = true)
public class CaffeineCacheService implements CacheService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ConcurrentMap<String, CaffeineCache<?, ?>> caches;

    public CaffeineCacheService() {
        this.caches = new ConcurrentHashMap<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <K, V> Cache<K, V> getCache(String cacheName) {
        Validate.isTrue(StringUtils.isNotEmpty(cacheName), "cacheName can't be blank!!");
        return (Cache<K, V>) this.caches.entrySet().stream()
                .filter(entry -> StringUtils.equals(entry.getKey(), cacheName))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("No cache exists with name [%s]!!", cacheName)));
    }

    @Override
    public void evictCache(String cacheName) {
        Validate.isTrue(StringUtils.isNotEmpty(cacheName), "cacheName can't be blank!!");
        this.caches.entrySet().stream()
                .filter(entry -> StringUtils.equals(entry.getKey(), cacheName))
                .findFirst()
                .ifPresent(entry -> entry.getValue().clear());
    }

    @Override
    public void evictCaches(Iterable<String> cacheNames) {
        cacheNames.forEach((String cacheName) -> Optional.ofNullable(this.caches.get(cacheName))
                .ifPresent(cache -> {
                    try {
                        LOGGER.info("Invalidating cache: [{}]", cache.getName());
                        cache.clear();
                    } catch (Exception ex) { // NOSONAR
                        LOGGER.error(ex.getMessage(), ex);
                    }
                }));
    }

    // <<------------------------------------------- OSGi INTERNAL ------------------------------------------->>

    @Deactivate
    protected void stop() {
        this.caches.forEach((String cacheName, Cache<?, ?> cache) -> {
            try {
                LOGGER.info("Invalidating cache: [{}]", cacheName);
                cache.clear();
            } catch (Exception ex) { // NOSONAR
                LOGGER.error(ex.getMessage(), ex);
            }
        });
        this.caches.clear();
    }

    @Reference(service = CaffeineCacheConfigFactory.class, cardinality = MULTIPLE, policy = DYNAMIC)
    protected void bindCaffeineCacheConfigFactory(CaffeineCacheConfigFactory configFactory) {
        CaffeineCache<?, ?> cache = new CaffeineCache<>(configFactory.getCacheName(), configFactory.getCacheSpec());
        this.caches.put(configFactory.getCacheName(), cache);
    }

    protected void unbindCaffeineCacheConfigFactory(CaffeineCacheConfigFactory configFactory) {
        Optional.ofNullable(this.caches.remove(configFactory.getCacheName()))
                .ifPresent(CaffeineCache::clear);
    }
}
