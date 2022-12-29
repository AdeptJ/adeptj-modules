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

import java.util.Map;
import java.util.function.Function;

/**
 * Implementation for Cache interface, internally this uses the Caffeine cache.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class CaffeineCache<K, V> implements Cache<K, V> {

    private final String cacheName;

    private final com.github.benmanes.caffeine.cache.Cache<@NotNull K, @NotNull V> caffeineCache;

    CaffeineCache(String cacheName, String cacheSpec) {
        this.cacheName = cacheName;
        this.caffeineCache = Caffeine.from(cacheSpec).build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.cacheName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V get(K key, Function<? super K, ? extends V> valueLoader) {
        return this.caffeineCache.get(key, valueLoader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable V getIfPresent(K key) {
        return this.caffeineCache.getIfPresent(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Map<K, V> getAllPresent(Iterable<K> keys) {
        return this.caffeineCache.getAllPresent(keys);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Map<K, V> getAll() {
        return this.caffeineCache.asMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(K key, V value) {
        this.caffeineCache.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evict(K key) {
        this.caffeineCache.invalidate(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evictMany(Iterable<K> keys) {
        this.caffeineCache.invalidateAll(keys);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        this.caffeineCache.invalidateAll();
    }
}