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
package com.adeptj.modules.cache.hazelcast.internal;

import java.util.Dictionary;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adeptj.modules.cache.common.Cache;
import com.adeptj.modules.cache.common.CacheConfig;
import com.adeptj.modules.cache.spi.CacheProvider;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.osgi.HazelcastOSGiService;

/**
 * OSGi service for cache manager, this services initializes the Hazelcast
 * CacheManager that gives handle to the cache instances configured in cache XML
 * and also provides API for creating cache dynamically either applying the
 * default configurations or providing at creation time.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(immediate = true, metatype = true, ds = false, configurationFactory = true, name = HazelcastCacheProvider.FACTORY_PID,
	label = "AdeptJ Modules Hazelcast CacheProvider", description = "AdeptJ Modules Hazelcast CacheProvider for getting or creating caches on demand")
public class HazelcastCacheProvider extends ServiceTracker<HazelcastOSGiService, HazelcastOSGiService> implements CacheProvider, ManagedServiceFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(HazelcastCacheProvider.class);
	
	public static final String FACTORY_PID = "cache.hazelcast.CacheProvider.factory";

	@Property(label = "Cache Name", description = "Cache Name", value = "")
	public static final String CACHE_NAME = "cache.name";

	@Property(label = "Cache TTL", description = "Cache TTL(in seconds)", intValue = 720)
	public static final String CACHE_TTL = "cache.ttl";
	
	@Property(label = "Cache Entries", description = "Number of elements in Cache", intValue = 1000)
	public static final String CACHE_ENTRIES = "cache.entries";
	
	private HazelcastOSGiService hazelcastOSGiService;
	
	private HazelcastInstance hazelcast;

	private ConcurrentMap<String, CacheConfig> cacheConfigs = new ConcurrentHashMap<>();

	protected HazelcastCacheProvider(BundleContext context) {
		super(context, HazelcastOSGiService.class, null);
	}
	
	protected void initHazelcastInstance() {
		long startTime = System.nanoTime();
		if (this.hazelcast == null) {
			Config config = new Config();
			config.setInstanceName("AdeptJ Modules Hazelcast CacheProvider Instance");
			this.hazelcast = this.hazelcastOSGiService.newHazelcastInstance(config);
			LOGGER.info("Hazelcast initialization took: [{}] ms!!", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
		}
	}

	@Override
	public HazelcastOSGiService addingService(ServiceReference<HazelcastOSGiService> reference) {
		HazelcastOSGiService hazelcastService = super.addingService(reference);
		this.hazelcastOSGiService = hazelcastService;
		return hazelcastService;
	}

	@Override
	public <K, V> Optional<Cache<K, V>> getCache(String name, Class<K> keyType, Class<V> valueType) {
		Cache<K, V> cache = null;
		Optional<CacheConfig> optionalCacheConfig = this.cacheConfigs.entrySet().stream().filter(entry -> {
			return entry.getValue().getCacheName().equals(name); }).map(entry -> { return entry.getValue(); }).findFirst();
		if (optionalCacheConfig.isPresent()) {
			Config config = this.hazelcast.getConfig();
			MapConfig mapConfig = config.getMapConfigs().get(name);
			// If no MapConfig found then create one with the name passed using the respective CacheConfig already saved via OSGi.
			if (mapConfig == null) {
				CacheConfig cacheConfig = optionalCacheConfig.get();
				mapConfig = new MapConfig();
				mapConfig.setName(cacheConfig.getCacheName()).setBackupCount(0)
						.setTimeToLiveSeconds((int) cacheConfig.getTtlSeconds()).getMaxSizeConfig()
						.setSize((int) cacheConfig.getCacheEntries());
				config.addMapConfig(mapConfig);
			}
			cache = new HazelcastCache<>(hazelcast.getMap(name));
		} else {
			LOGGER.warn("CacheConfig against name [{}] doesn't exist, please create it first!", name);
		}
		return Optional.ofNullable(cache);
	}

	@Override
	public String getName() {
		return "AdeptJ Modules Hazelcast Cache Factory";
	}

	@Override
	public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
		this.initHazelcastInstance();
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
			String oldCacheName = storedCacheConfig.getCacheName();
			LOGGER.info("Removing old cache with name: [{}] from Hazelcast.", oldCacheName);
			Config config = this.hazelcast.getConfig();
			MapConfig mapConfig = config.getMapConfigs().get(oldCacheName);
			if (mapConfig != null) {
				this.hazelcast.getMap(mapConfig.getName()).destroy();
				LOGGER.info("Removed Cache: {}", cacheName);
			}
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
			IMap<Object, Object> cache = this.hazelcast.getMap(cacheName);
			if (cache != null) {
				cache.destroy();
				LOGGER.info("Removed Cache: {}", cacheName);
			}
		}
	}

}