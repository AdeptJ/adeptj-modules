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
import com.adeptj.modules.commons.cache.CacheUtil;
import com.adeptj.modules.commons.cache.CaffeineCacheConfigFactoryBindException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

/**
 * Caffeine cache based implementation of {@link CacheService}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component
public class CaffeineCacheService implements CacheService {

    private final ConcurrentMap<String, Cache<?, ?>> caches;

    private final List<String> configPids;

    public CaffeineCacheService() {
        this.caches = new ConcurrentHashMap<>();
        this.configPids = new CopyOnWriteArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <K, V> @Nullable Cache<K, V> getCache(String cacheName) {
        Validate.isTrue(StringUtils.isNotEmpty(cacheName), "cacheName can't be null!!");
        return (Cache<K, V>) this.caches.get(cacheName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evictCaches(String... cacheNames) {
        if (ArrayUtils.isNotEmpty(cacheNames)) {
            Stream.of(cacheNames).forEach(cacheName -> CacheUtil.nullSafeEvict(this.getCache(cacheName)));
        }
    }

    // <<------------------------------------------- OSGi Internal ------------------------------------------->>

    /**
     * First evict all the caches and then clears the {@link #caches} map.
     */
    @Deactivate
    protected void stop() {
        this.caches.values().forEach(Cache::evict);
        this.caches.clear();
    }

    @Reference(service = CaffeineCacheConfigFactory.class, cardinality = MULTIPLE, policy = DYNAMIC)
    protected void bindCaffeineCacheConfigFactory(CaffeineCacheConfigFactory factory, Map<String, Object> properties) {
        String cacheName = CacheUtil.getCacheName(properties);
        if (this.caches.containsKey(cacheName)) {
            throw new CaffeineCacheConfigFactoryBindException(String.format("Cache:(%s) already exists!!", cacheName));
        }
        this.caches.put(cacheName, new CaffeineCache<>(cacheName, CacheUtil.getCacheSpec(properties)));
        this.configPids.add(CacheUtil.getServicePid(properties));
    }

    protected void unbindCaffeineCacheConfigFactory(Map<String, Object> properties) {
        if (this.configPids.remove(CacheUtil.getServicePid(properties))) {
            CacheUtil.nullSafeEvict(this.caches.remove(CacheUtil.getCacheName(properties)));
        }
    }
}
