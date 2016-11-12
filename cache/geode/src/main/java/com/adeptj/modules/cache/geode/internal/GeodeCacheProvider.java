/* 
 * =============================================================================
 * 
 * Copyright (c) 2016 AdeptJ
 * Copyright (c) 2016 Rakesh Kumar <irakeshk@outlook.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * =============================================================================
 */
package com.adeptj.modules.cache.geode.internal;

import java.util.Dictionary;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adeptj.modules.cache.common.Cache;
import com.adeptj.modules.cache.common.CacheConfig;
import com.adeptj.modules.cache.spi.CacheProvider;

/**
 * OSGi service for cache manager, this services initializes the EHcache
 * CacheManager that gives handle to the cache instances configured in cache XML
 * and also provides API for creating cache dynamically either applying the
 * default configurations or providing at creation time.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(immediate = true, ds = false, metatype = true, configurationFactory = true, name = GeodeCacheProvider.SERVICE_PID, 
label = "AdeptJ Modular Web CacheProviderFactory", description = "AdeptJ Modular Web CacheProvider creates or gets cache on demand")
public class GeodeCacheProvider implements CacheProvider, ManagedServiceFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(GeodeCacheProvider.class);
	
	public static final String SERVICE_PID = "cache.CacheProvider.factory";

	@Property(label = "Cache Name", description = "Cache Name", value = "")
	public static final String CACHE_NAME = "cache.name";

	@Property(label = "Cache TTL", description = "Cache TTL(in seconds)", longValue = 720)
	public static final String CACHE_TTL = "cache.ttl";
	
	@Property(label = "Cache Entries", description = "Number of elements in Cache", longValue = 1000)
	public static final String CACHE_ENTRIES = "cache.entries";

	public GeodeCacheProvider() {
	}

	private ConcurrentMap<String, CacheConfig> configMap = new ConcurrentHashMap<>();

	private ConcurrentMap<String, Cache<?, ?>> cacheMap = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public <K, V> Optional<Cache<K, V>> getCache(String name, Class<K> keyType, Class<V> valueType) {
		LOGGER.info("Getting Cache with name: [{}]", name);
		// First check in the local cache map.
		Cache<?, ?> cache = this.cacheMap.get(name);
		if (cache == null) {
			
		}
		return Optional.ofNullable((Cache<K, V>) cache);
	}

	@Override
	public String getName() {
		return "AdeptJ OSGi Cache Provider Factory";
	}

	@Override
	public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
		String cacheName = (String) properties.get(CACHE_NAME);
		Long cacheTTL = (Long) properties.get(CACHE_TTL);
		Long cacheEntries = (Long) properties.get(CACHE_ENTRIES);
		CacheConfig cacheConfig = new CacheConfig(cacheName, pid, cacheTTL, cacheEntries);
		this.configMap.putIfAbsent(pid, cacheConfig);
		if (cacheConfig.equals(this.configMap.get(pid))) {
			LOGGER.warn("Unchanged CacheConfig, ignoring it!!");
		} else {
			this.configMap.put(pid, cacheConfig);
		}
	}

	@Override
	public void deleted(String pid) {
		CacheConfig cacheCfg = this.configMap.remove(pid);
		if (cacheCfg != null) {
			String cacheName = cacheCfg.getCacheName();
			LOGGER.info("Removed Cache: {}", cacheName);
		}
	}
}