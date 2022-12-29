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

package com.adeptj.modules.commons.cache;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

/**
 * The {@link Cache}
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public interface Cache<K, V> {

    /**
     * Each Cache instance has a name which this method returns to the caller.
     *
     * @return the unique cache name.
     */
    String getName();

    /**
     * Return the value to which this cache maps the specified key, obtaining that value from valueLoader if necessary.
     * This method provides a simple substitute for the conventional "if cached, return; otherwise create, cache and return" pattern.
     *
     * @param key         the key whose associated value is to be returned
     * @param valueLoader loads the value against the given key.
     * @return the value to which this cache maps the specified key
     */
    V get(K key, Function<? super K, ? extends V> valueLoader);

    /**
     * Perform an actual lookup in the underlying cache.
     *
     * @param key the key whose associated value is to be returned
     * @return the value for the key, or null if none
     */
    @Nullable
    V getIfPresent(K key);

    /**
     * Gets all the mappings of key value pairs found in cache against the keys passed.
     *
     * @param keys the keys to be found in cache
     * @return the mappings of key value pairs found in cache.
     */
    Map<K, V> getAllPresent(Iterable<K> keys);

    /**
     * Gets all the mappings of key value pairs found in cache.
     *
     * @return the mappings of key value pairs found in cache.
     */
    Map<K, V> getAll();

    /**
     * Associate the specified value with the specified key in this cache.
     * If the cache previously contained a mapping for this key, the old value is replaced by the specified value.
     *
     * @param key   the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     */
    void put(K key, V value);

    /**
     * Evict the mapping for this key from this cache if it is present.
     *
     * @param key the key to be evicted.
     */
    void evict(K key);

    /**
     * Evict the mapping for given keys from this cache if it is present.
     *
     * @param keys the keys to be evicted.
     */
    void evictMany(Iterable<K> keys);

    /**
     * Clear the cache through removing all mappings.
     */
    void clear();
}
