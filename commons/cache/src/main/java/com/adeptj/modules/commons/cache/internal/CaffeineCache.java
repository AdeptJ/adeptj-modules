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
import com.github.benmanes.caffeine.cache.Caffeine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Implementation for Cache interface, internally this uses the Caffeine cache.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class CaffeineCache<K, V> implements Cache<K, V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final String cacheName;

    private final com.github.benmanes.caffeine.cache.Cache<@NotNull K, @NotNull V> caffeineCache;

    CaffeineCache(String cacheName, String cacheSpec) {
        this.cacheName = cacheName;
        this.caffeineCache = Caffeine.from(cacheSpec).build();
        LOGGER.info("CaffeineCache ({}:{}) initialized!!", cacheName, cacheSpec);
    }

    @Override
    public String getName() {
        return this.cacheName;
    }

    @Override
    public V get(K key, Function<? super K, ? extends V> mappingFunction) {
        return this.caffeineCache.get(key, mappingFunction);
    }

    @Override
    public @Nullable V getIfPresent(K key) {
        return this.caffeineCache.getIfPresent(key);
    }

    @Override
    public @NotNull Map<K, V> getAllPresent(Iterable<K> keys) {
        return this.caffeineCache.getAllPresent(keys);
    }

    @Override
    public @NotNull Map<K, V> getAll() {
        return this.caffeineCache.asMap();
    }

    @Override
    public void put(K key, V value) {
        this.caffeineCache.put(key, value);
    }

    @Override
    public void remove(K key) {
        this.caffeineCache.invalidate(key);
    }

    @Override
    public void remove(Iterable<K> keys) {
        this.caffeineCache.invalidateAll(keys);
    }

    @Override
    public void evict() {
        try {
            this.caffeineCache.invalidateAll();
            LOGGER.info("CaffeineCache ({}) evicted!!", this.getName());
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @Override
    public long size() {
        this.caffeineCache.cleanUp();
        return this.caffeineCache.estimatedSize();
    }

    @Override
    public @NotNull Set<K> keys() {
        return this.caffeineCache.asMap().keySet();
    }

    @Override
    public @NotNull Collection<V> values() {
        return this.caffeineCache.asMap().values();
    }
}