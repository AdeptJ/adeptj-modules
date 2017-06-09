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
package com.adeptj.modularweb.security.shiro.cache;

import java.util.Collection;
import java.util.Set;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.session.Session;

/**
 * Shiro {@link Session} Cache backed by an EhCache.
 * 
 * @author Rakesh.Kumar, AdeptJ..
 */
public class ShiroSessionCache<K, V> implements Cache<K, V> {

	private com.adeptj.modularweb.cache.api.Cache<K, V> cache;

	public ShiroSessionCache(com.adeptj.modularweb.cache.api.Cache<K, V> backingCache) {
		this.cache = backingCache;
	}

	@Override
	public V get(K key) throws CacheException {
		return this.cache.get(key);
	}

	@Override
	public V put(K key, V value) throws CacheException {
		return this.cache.put(key, value);
	}

	@Override
	public V remove(K key) throws CacheException {
		return this.cache.remove(key);
	}

	@Override
	public void clear() throws CacheException {
		this.cache.clear();
	}

	/**
	 * NOTE: Very expensive.
	 */
	@Override
	public int size() {
		return this.cache.size();
	}

	/**
	 * NOTE: Very expensive.
	 */
	@Override
	public Set<K> keys() {
		return this.cache.keys();
	}

	/**
	 * NOTE: Very expensive.
	 */
	@Override
	public Collection<V> values() {
		return this.cache.values();
	}

}
