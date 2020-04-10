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

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * Implementation for Cache interface, internally this uses the Caffeine cache.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class CaffeineCache<K, V> implements Cache<K, V> {

    private final String cacheName;

    private final com.github.benmanes.caffeine.cache.Cache<K, V> caffeineCache;

    CaffeineCache(String cacheName, com.github.benmanes.caffeine.cache.Cache<K, V> caffeineCache) {
        this.cacheName = cacheName;
        this.caffeineCache = caffeineCache;
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
    public V getIfPresent(K key) {
        return this.caffeineCache.getIfPresent(key);
    }

    @Override
    public Map<K, V> getAllPresent(Iterable<K> keys) {
        return this.caffeineCache.getAllPresent(keys);
    }

    @Override
    public Map<K, V> getAll() {
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
    public void clear() {
        this.caffeineCache.invalidateAll();
    }

    @Override
    public long size() {
        this.caffeineCache.cleanUp();
        return this.caffeineCache.estimatedSize();
    }

    @Override
    public Set<K> keys() {
        return this.caffeineCache.asMap().keySet();
    }

    @Override
    public Collection<V> values() {
        return this.caffeineCache.asMap().values();
    }

    // <<----------------------- Generated ----------------------->>

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaffeineCache<?, ?> that = (CaffeineCache<?, ?>) o;
        return Objects.equals(this.cacheName, that.cacheName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.cacheName);
    }
}