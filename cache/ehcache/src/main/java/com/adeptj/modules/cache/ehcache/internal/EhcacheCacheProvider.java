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

import com.adeptj.modules.cache.api.CacheProvider;
import com.adeptj.modules.cache.common.Cache;
import org.ehcache.CacheManager;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.Optional;

import static com.adeptj.modules.cache.ehcache.internal.EhcacheCacheProvider.COMPONENT_NAME;
import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.IGNORE;

/**
 * OSGi service for cache manager, this services initializes the EHcache
 * CacheManager that gives handle to the cache instances configured in cache XML
 * and also provides API for creating cache dynamically either applying the
 * default configurations or providing at creation time.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = EhcacheCacheConfig.class, factory = true)
@Component(
        immediate = true,
        name = COMPONENT_NAME,
        property = SERVICE_PID + "=" + COMPONENT_NAME,
        configurationPolicy = IGNORE
)
public class EhcacheCacheProvider implements CacheProvider, ManagedServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(EhcacheCacheProvider.class);

    static final String COMPONENT_NAME = "com.adeptj.modules.cache.ehcache.CacheProvider.factory";

    private CacheManager cacheMgr;

    EhcacheCacheProvider(CacheManager cacheMgr) {
        this.cacheMgr = cacheMgr;
    }

    @Override
    public <K, V> Optional<Cache<K, V>> getCache(String name, Class<K> keyType, Class<V> valueType) {
        return null;
    }

    @Override
    public String getName() {
        return "AdeptJ Modules Ehcache Cache Factory";
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {

    }

    @Override
    public void deleted(String pid) {
    }
}