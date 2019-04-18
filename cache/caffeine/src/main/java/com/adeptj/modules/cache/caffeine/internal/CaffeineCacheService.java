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
import com.adeptj.modules.cache.caffeine.CacheService;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

/**
 * Default implementation of {@link CacheService}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component
public class CaffeineCacheService implements CacheService {

    private List<CaffeineCache<?, ?>> caches = new CopyOnWriteArrayList<>();

    @SuppressWarnings("unchecked")
    @Override
    public <K, V> Cache<K, V> getCache(String name) {
        return (Cache<K, V>) this.caches.stream()
                .filter(cache -> cache.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("No cache exists with name [%s]!!", name)));
    }

    @Reference(service = CaffeineCacheFactory.class, cardinality = MULTIPLE, policy = DYNAMIC)
    public void bindCaffeineCacheFactory(CaffeineCacheFactory cacheFactory) {
        CaffeineCacheConfig cacheConfig = cacheFactory.getCacheConfig();
        this.caches.add(new CaffeineCache<>(cacheConfig.name(), Caffeine.newBuilder()
                .maximumSize(cacheConfig.maximumSize())
                .expireAfterWrite(cacheConfig.expireAfter(), TimeUnit.MINUTES)
                .build()));
    }

    public void unbindCaffeineCacheFactory(CaffeineCacheFactory cacheFactory) {
        this.caches.removeIf(cache -> StringUtils.equals(cache.getName(), cacheFactory.getCacheConfig().name()));
    }
}
