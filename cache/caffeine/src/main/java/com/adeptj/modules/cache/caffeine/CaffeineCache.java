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

package com.adeptj.modules.cache.caffeine;

import java.util.Collection;
import java.util.Set;

/**
 * Implementation for Cache interface, internally this uses the Caffeine's caffeineCache.
 *
 * @author Rakesh.Kumar
 */
public class CaffeineCache<K, V> implements Cache<K, V> {

    private com.github.benmanes.caffeine.cache.Cache<K, V> caffeineCache;

    CaffeineCache(com.github.benmanes.caffeine.cache.Cache<K, V> caffeineCache) {
        this.caffeineCache = caffeineCache;
    }

    @Override
    public V get(K key) {
        return this.caffeineCache.getIfPresent(key);
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
    public void clear() {
        this.caffeineCache.invalidateAll();
    }

    @Override
    public long size() {
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

}