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

package com.adeptj.modules.commons.cache.internal;

import com.adeptj.modules.commons.cache.Cache;
import com.adeptj.modules.commons.cache.CacheService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

/**
 * Caffeine cache based implementation of {@link CacheService}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(immediate = true)
public class CaffeineCacheService implements CacheService {

    private static final String KEY_CACHE_NAME = "cache.name";

    private static final String KEY_CACHE_SPEC = "cache.spec";

    private final ConcurrentMap<String, Cache<?, ?>> caches;

    private final ConcurrentMap<String, String> pidCacheNameMapping;

    public CaffeineCacheService() {
        this.caches = new ConcurrentHashMap<>();
        this.pidCacheNameMapping = new ConcurrentHashMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <K, V> @Nullable Cache<K, V> getCache(String cacheName) {
        return (Cache<K, V>) this.caches.get(cacheName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evictCaches(String... cacheNames) {
        if (ArrayUtils.isNotEmpty(cacheNames)) {
            Stream.of(cacheNames).forEach(cacheName -> this.doEviction(this.getCache(cacheName)));
        }
    }

    private void doEviction(Cache<?, ?> cache) {
        if (cache != null) {
            cache.evict();
        }
    }

    // <<------------------------------------------- OSGi INTERNAL ------------------------------------------->>

    /**
     * First evict all the caches and then clears the {@link Cache} instance holding map.
     */
    @Deactivate
    protected void stop() {
        this.caches.values().forEach(this::doEviction);
        this.caches.clear();
    }

    @Reference(service = CaffeineCacheConfigFactory.class, cardinality = MULTIPLE, policy = DYNAMIC)
    protected void bindCaffeineCacheConfigFactory(@NotNull Map<String, Object> properties) {
        String cacheName = (String) properties.get(KEY_CACHE_NAME);
        if (this.caches.containsKey(cacheName)) {
            throw new CaffeineCacheConfigFactoryBindException(String.format("Cache:(%s) already exists!!", cacheName));
        }
        this.caches.put(cacheName, new CaffeineCache<>(cacheName, (String) properties.get(KEY_CACHE_SPEC)));
        this.pidCacheNameMapping.put((String) properties.get(SERVICE_PID), cacheName);
    }

    protected void unbindCaffeineCacheConfigFactory(@NotNull Map<String, Object> properties) {
        String pid = (String) properties.get(SERVICE_PID);
        String cacheName = (String) properties.get(KEY_CACHE_NAME);
        if (StringUtils.equals(cacheName, this.pidCacheNameMapping.get(pid))) {
            this.doEviction(this.caches.remove(cacheName));
        }
    }
}
