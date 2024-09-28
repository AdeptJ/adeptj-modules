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
package com.adeptj.modules.commons.cache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ProviderType;

/**
 * The {@link CacheService} for in memory local caching.
 *
 * @author Rakesh Kumar, AdeptJ
 */
@ProviderType
public interface CacheService {

    /**
     * Gets the {@link Cache} configured for the given name.
     *
     * @param cacheName The cache name.
     * @param <K>       The cache key.
     * @param <V>       The cache value
     * @return the {@link Cache} instance or null if none exists for given name.
     */
    <K, V> @Nullable Cache<K, V> getCache(String cacheName);

    /**
     * Clears the {@link Cache} instance resolved against the given name.
     *
     * @param cacheName The cache name.
     */
    void clearCache(String cacheName);

    /**
     * Clears all the {@link Cache} instances resolved against the given names.
     *
     * @param cacheNames The cache names.
     */
    void clearCaches(@NotNull Iterable<String> cacheNames);
}
