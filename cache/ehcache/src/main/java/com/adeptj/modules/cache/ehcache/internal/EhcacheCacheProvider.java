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
import com.adeptj.modules.cache.api.CacheProvider;
import org.ehcache.CacheManager;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static com.adeptj.modules.cache.ehcache.internal.EhcacheCacheProvider.COMPONENT_NAME;
import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * EhcacheCacheProvider.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = EhcacheCacheConfig.class, factory = true)
@Component(
        immediate = true,
        name = COMPONENT_NAME,
        property = SERVICE_PID + "=" + COMPONENT_NAME,
        configurationPolicy = REQUIRE
)
public class EhcacheCacheProvider implements CacheProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(EhcacheCacheProvider.class);

    static final String COMPONENT_NAME = "com.adeptj.modules.cache.ehcache.CacheProvider.factory";

    private CacheManager cacheMgr;

    @Override
    public String getName() {
        return "EHCAHE";
    }

    @Override
    public <K, V> Optional<Cache<K, V>> getCache(String name, Class<K> keyType, Class<V> valueType) {
        return Optional.of(new EhcacheCache<>(this.cacheMgr.getCache(name, keyType, valueType)));
    }

    @Activate
    protected void start(EhcacheCacheConfig config) {
    }

    @Deactivate
    protected void stop(EhcacheCacheConfig config) {
    }
}