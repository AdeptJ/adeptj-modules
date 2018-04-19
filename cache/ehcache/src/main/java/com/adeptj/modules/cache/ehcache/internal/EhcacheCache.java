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

package com.adeptj.modules.cache.ehcache.internal;

import com.adeptj.modules.cache.api.Cache;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation for Cache interface, internally this uses the EHCache for performing the low level operations.
 *
 * @author Rakesh.Kumar
 */
public class EhcacheCache<K, V> implements Cache<K, V> {

    private org.ehcache.Cache<K, V> ehcache;

    EhcacheCache(org.ehcache.Cache<K, V> backingCache) {
        this.ehcache = backingCache;
    }

    @Override
    public V get(K key) {
        return this.ehcache.get(key);
    }

    @Override
    public void put(K key, V value) {
        this.ehcache.putIfAbsent(key, value);
    }

    @Override
    public void remove(K key) {
        this.ehcache.remove(key);
    }

    @Override
    public void clear() {
        this.ehcache.clear();
    }

    @Override
    public long size() {
        return CollectionUtils.size(ehcache);
    }

    @Override
    public Set<K> keys() {
        Set<K> keys = new HashSet<>();
        this.ehcache.forEach(entry -> keys.add(entry.getKey()));
        return keys;
    }

    @Override
    public Collection<V> values() {
        List<V> values = new ArrayList<>();
        this.ehcache.forEach(entry -> values.add(entry.getValue()));
        return values;
    }

}