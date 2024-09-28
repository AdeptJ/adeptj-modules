/*
###############################################################################
#                                                                             #
#    Copyright 2016-2024, AdeptJ (http://adeptj.com)                          #
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

/**
 * Caffeine cache based implementation of {@link CacheService}.
 *
 * @author Rakesh Kumar, AdeptJ
 */
@Component
public class CaffeineCacheService implements CacheService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ConcurrentMap<String, Cache<?, ?>> caches;

    private final List<String> cacheConfigPids;

    public CaffeineCacheService() {
        this.caches = new ConcurrentHashMap<>();
        this.cacheConfigPids = new ArrayList<>();
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
    public void clearCache(String cacheName) {
        Cache<?, ?> cache = this.getCache(cacheName);
        if (cache == null) {
            LOGGER.warn("Nothing to clear as Cache({}) doesn't exist!!", cacheName);
            return;
        }
        cache.clear();
        LOGGER.info("Cache({}) cleared successfully!!", cache.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearCaches(@NotNull Iterable<String> cacheNames) {
        cacheNames.forEach(this::clearCache);
    }

    // <<------------------------------------------- OSGi Internal ------------------------------------------->>

    /**
     * First clear all mappings in the Caffeine caches and then clears the {@link #caches} map.
     */
    @Deactivate
    protected void stop() {
        LOGGER.info("CacheService is being stopped, it will clear all the Cache instances.");
        this.clearCaches(this.caches.keySet());
        this.caches.forEach((cacheName, cache) -> cache.clear());
        this.caches.clear();
    }

    @Reference(service = CaffeineCacheConfigFactory.class, cardinality = MULTIPLE, policy = DYNAMIC)
    protected void bindCaffeineCacheConfigFactory(@NotNull Map<String, Object> properties) {
        String pid = CacheUtil.getServicePid(properties);
        LOGGER.info("Binding CaffeineCacheConfigFactory with pid - {}", pid);
        String cacheName = StringUtils.trim(CacheUtil.getCacheName(properties));
        if (this.caches.containsKey(cacheName)) {
            String msg = "Cache(%s) already exists, please consider using a unique name." +
                    " The factory pid to identify the config in Felix WebConsole - (%s).";
            throw new CaffeineCacheConfigFactoryBindException(String.format(msg, cacheName, pid));
        }
        String cacheSpec = CacheUtil.getCacheSpec(properties);
        this.caches.put(cacheName, new CaffeineCache<>(cacheName, cacheSpec));
        LOGGER.info("CaffeineCache ({}:{}) initialized!!", cacheName, cacheSpec);
        this.cacheConfigPids.add(pid);
    }

    protected void unbindCaffeineCacheConfigFactory(@NotNull Map<String, Object> properties) {
        String pid = CacheUtil.getServicePid(properties);
        LOGGER.info("Unbinding CaffeineCacheConfigFactory with pid - {}", pid);
        if (this.cacheConfigPids.remove(pid)) {
            CacheUtil.nullSafeClear(this.caches.remove(CacheUtil.getCacheName(properties)));
        }
    }
}
