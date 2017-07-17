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

package com.adeptj.modules.cache.api;

import com.adeptj.modules.cache.common.Cache;

import java.util.Optional;

/**
 * The CacheProvider provides access to all caches in the system.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public interface CacheProvider {

    /**
     * Get the name of this provider.
     *
     * @return provider name
     */
    String getName();

    /***
     * Get the cache for the given name and the types(key and value)
     *
     * @param name the cache name
     * @param keyType type of the cache key
     * @param valueType type of the cache value
     * @return Cache
     */
    <K, V> Optional<Cache<K, V>> getCache(String name, Class<K> keyType, Class<V> valueType);

}
