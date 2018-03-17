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
package com.adeptj.modules.cache.infinispan.internal;

import com.adeptj.modules.cache.api.CacheProvider;
import com.adeptj.modules.cache.common.Cache;
import com.adeptj.modules.cache.common.CacheConfig;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.eviction.EvictionType;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * InfinispanCacheProvider.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(immediate = true, ds = false, metatype = true, configurationFactory = true, name = InfinispanCacheProvider.SERVICE_PID, 
label = "AdeptJ Modules Infinispan Cache Factory", description = "AdeptJ Modules Infinispan Cache Factory creates or gets cache on demand")
public class InfinispanCacheProvider implements CacheProvider, ManagedServiceFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(InfinispanCacheProvider.class);
	
	public static final String SERVICE_PID = "cache.infinispan.CacheProvider.factory";

	@Property(label = "Cache Name", description = "Cache Name", value = "")
	public static final String CACHE_NAME = "cache.name";

	@Property(label = "Cache TTL", description = "Cache TTL(in seconds)", longValue = 720)
	public static final String CACHE_TTL = "cache.ttl";
	
	@Property(label = "Cache Entries", description = "Number of elements in Cache", longValue = 1000)
	public static final String CACHE_ENTRIES = "cache.entries";
	
	private EmbeddedCacheManager cacheManager;

	private ConcurrentMap<String, CacheConfig> cacheConfigs = new ConcurrentHashMap<>();

	protected void initInfinispan() {
		long startTime = System.nanoTime();
		if (this.cacheManager == null) {
			Configuration defaultConfiguration = new ConfigurationBuilder().simpleCache(true).eviction()
					.strategy(EvictionStrategy.LRU).type(EvictionType.COUNT).size(1000l).expiration()
					.lifespan(60, TimeUnit.MINUTES).maxIdle(30, TimeUnit.MINUTES).build();
			this.cacheManager = new DefaultCacheManager(defaultConfiguration);
			LOGGER.info("Infinispan initialization took: [{}] ms!!", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
		}
	}
	
	protected void shutdownInfinispan() {
		if (this.cacheManager != null) {
			LOGGER.info("Shutting down Infinispan CacheManager!!");
			this.cacheManager.stop();
		}
	}

	@Override
	public <K, V> Optional<Cache<K, V>> getCache(String name, Class<K> keyType, Class<V> valueType) {
		Cache<K, V> cache = null;
		Optional<CacheConfig> optionalCacheConfig = this.cacheConfigs.entrySet().stream().filter(entry -> {
			return entry.getValue().getCacheName().equals(name); }).map(entry -> { return entry.getValue(); }).findFirst();
		if (optionalCacheConfig.isPresent()) {
			if (this.cacheManager.cacheExists(name)) {
				cache = new InfinispanCache<>(this.cacheManager.getCache(name));
			} else {
				cache = new InfinispanCache<>(this.cacheManager.getCache(name, true));
			}
		} else {
			LOGGER.warn("CacheConfig against name [{}] doesn't exist, please create it first!", name);
		}
		return Optional.ofNullable(cache);
	}

	@Override
	public String getName() {
		return "AdeptJ Modules Infinispan Cache Factory";
	}

	@Override
	public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
		this.initInfinispan();
		String cacheName = (String) properties.get(CACHE_NAME);
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
			String oldCacheName = storedCacheConfig.getCacheName();
			LOGGER.info("Removing old cache with name: [{}] from Infinispan.", oldCacheName);
			this.cacheManager.removeCache(oldCacheName);
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
			this.cacheManager.removeCache(cacheName);
			LOGGER.info("Removed Cache: {}", cacheName);
		}
	}
}