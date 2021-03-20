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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static org.osgi.framework.Constants.SERVICE_PID;

/**
 * Utilities for cache module.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class CacheUtil {

    private static final String KEY_CACHE_NAME = "cache.name";

    private static final String KEY_CACHE_SPEC = "cache.spec";

    public static String getCacheName(@NotNull Map<String, Object> properties) {
        String cacheName = StringUtils.trim((String) properties.get(KEY_CACHE_NAME));
        Validate.isTrue(StringUtils.isNotEmpty(cacheName), "cache.name property can't be null!!");
        return cacheName;
    }

    public static String getCacheSpec(@NotNull Map<String, Object> properties) {
        final String cacheSpec = StringUtils.trim((String) properties.get(KEY_CACHE_SPEC));
        Validate.isTrue(StringUtils.isNotEmpty(cacheSpec), "cache.spec property can't be null!!");
        return cacheSpec;
    }

    public static String getServicePid(@NotNull Map<String, Object> properties) {
        return (String) properties.get(SERVICE_PID);
    }

    public static void nullSafeEvict(Cache<?, ?> cache) {
        if (cache != null) {
            cache.evict();
        }
    }
}
