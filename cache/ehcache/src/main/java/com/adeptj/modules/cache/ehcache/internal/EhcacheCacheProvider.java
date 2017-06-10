/** 
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
package com.adeptj.modules.cache.ehcache.internal;

import com.adeptj.modules.cache.api.CacheProvider;
import com.adeptj.modules.cache.common.Cache;
import com.adeptj.modules.cache.common.CacheConfig;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.ehcache.expiry.Expiry;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * OSGi service for cache manager, this services initializes the EHcache
 * CacheManager that gives handle to the cache instances configured in cache XML
 * and also provides API for creating cache dynamically either applying the
 * default configurations or providing at creation time.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(immediate = true, ds = false, metatype = true, configurationFactory = true, name = EhcacheCacheProvider.FACTORY_PID, 
	label = "AdeptJ Modules Ehcache Cache Factory", description = "AdeptJ Modules Ehcache CacheProvider creates or gets cache on demand")
public class EhcacheCacheProvider implements CacheProvider, ManagedServiceFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(EhcacheCacheProvider.class);
	
	public static final String FACTORY_PID = "cache.ehcache.CacheProvider.factory";

	@Property(label = "Cache Name", description = "Cache Name", value = "")
	public static final String CACHE_NAME = "cache.name";

	@Property(label = "Cache TTL", description = "Cache TTL(in seconds)", longValue = 720)
	public static final String CACHE_TTL = "cache.ttl";
	
	@Property(label = "Cache Entries", description = "Number of elements in Cache", longValue = 1000)
	public static final String CACHE_ENTRIES = "cache.entries";

	private CacheManager cacheMgr;

	public EhcacheCacheProvider(CacheManager cacheMgr) {
		this.cacheMgr = cacheMgr;
	}

	private ConcurrentMap<String, CacheConfig> cacheConfigs = new ConcurrentHashMap<>();

	@Override
	public <K, V> Optional<Cache<K, V>> getCache(String name, Class<K> keyType, Class<V> valueType) {
		Cache<K, V> cache = null;
		Optional<CacheConfig> optionalCacheConfig = this.cacheConfigs.entrySet().stream().filter(entry -> {
			return entry.getValue().getCacheName().equals(name); }).map(entry -> { return entry.getValue(); }).findFirst();
		if (optionalCacheConfig.isPresent()) {
			org.ehcache.Cache<K, V> ehcache = this.cacheMgr.getCache(name, keyType, valueType);
			if (ehcache == null) {
				try {
					CacheConfig cacheConfig = optionalCacheConfig.get();
					Expiry<Object, Object> timeToLiveExpiration = Expirations
							.timeToLiveExpiration(new Duration((long) cacheConfig.getTtlSeconds(), TimeUnit.SECONDS));
					cache = new EhcacheCache<>(this.cacheMgr.createCache(cacheConfig.getCacheName(),
							CacheConfigurationBuilder
									.newCacheConfigurationBuilder(keyType, valueType, ResourcePoolsBuilder.heap(1000l))
									.withExpiry(timeToLiveExpiration).build()));
				} catch (Exception ex) {
					LOGGER.error("Could not get Cache with name: [{}], Exception!!", name, ex);
				}
			}
		} else {
			LOGGER.warn("CacheConfig against name [{}] doesn't exist, please create it first!", name);
		}
		return Optional.ofNullable(cache);
	}

	@Override
	public String getName() {
		return "AdeptJ Modules Ehcache Cache Factory";
	}

	@Override
	public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
		String cacheName = (String) Objects.requireNonNull(properties.get(CACHE_NAME), "Cache name can't be null!!");
		Long cacheTTL = (Long) properties.get(CACHE_TTL);
		Long cacheEntries = (Long) properties.get(CACHE_ENTRIES);
		CacheConfig newCacheConfig = new CacheConfig(cacheName, pid, cacheTTL, cacheEntries);
		CacheConfig storedCacheConfig = this.cacheConfigs.get(pid);
		// If no CacheConfig against the given PID, create one.
		if (storedCacheConfig == null) {
			this.cacheConfigs.put(pid, newCacheConfig);
		} else if (storedCacheConfig.equals(newCacheConfig)) {
			// If CacheConfig unchanged(user just saved the Factory configuration without changing values), do nothing. 
			LOGGER.warn("Unchanged CacheConfig, ignoring it!!");
		} else {
			// Just remove the cache from CacheManager and update the CacheConfig.
			LOGGER.info("Removing cache with name: [{}] from Ehcache CacheManager.", cacheName);
			this.cacheMgr.removeCache(cacheName);
			storedCacheConfig.setCacheEntries(cacheEntries);
			storedCacheConfig.setTtlSeconds(cacheTTL);
			storedCacheConfig.setCacheName(cacheName);
		}
	}

	@Override
	public void deleted(String pid) {
		CacheConfig cacheCfg = this.cacheConfigs.remove(pid);
		if (cacheCfg != null) {
			String cacheName = cacheCfg.getCacheName();
			this.cacheMgr.removeCache(cacheName);
			LOGGER.info("Removed Cache: {}", cacheName);
		}
	}
}