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
package com.adeptj.modularweb.cache.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import com.adeptj.modularweb.cache.api.Cache;

/**
 * Implementation for Cache interface, internally this uses the EHCache
 * CacheManager for performing the low level operations.
 * 
 * @author Rakesh.Kumar
 */
public class CacheImpl<K, V> implements Cache<K, V> {

	private org.ehcache.Cache<K, V> ehCache;

	public CacheImpl(org.ehcache.Cache<K, V> backingCache) {
		this.ehCache = backingCache;
	}

	@Override
	public V get(K key) {
		return this.ehCache.get(key);
	}

	@Override
	public V put(K key, V value) {
		return this.ehCache.putIfAbsent(key, value);
	}

	@Override
	public V remove(K key) {
		V element = null;
		if (this.ehCache.containsKey(key)) {
			element = this.ehCache.get(key);
			this.ehCache.remove(key);
		}
		return element;
	}

	@Override
	public void clear() {
		this.ehCache.clear();
	}

	/**
	 * NOTE: Very expensive.
	 */
	@Override
	public int size() {
		return CollectionUtils.size(ehCache);
	}

	/**
	 * NOTE: Very expensive.
	 */
	@Override
	public Set<K> keys() {
		Set<K> keys = new HashSet<>();
		this.ehCache.iterator().forEachRemaining(action -> {
			keys.add(action.getKey());
		});
		return keys;
	}

	/**
	 * NOTE: Very expensive.
	 */
	@Override
	public Collection<V> values() {
		List<V> values = new ArrayList<>();
		this.ehCache.iterator().forEachRemaining(action -> {
			values.add(action.getValue());
		});
		return values;
	}

}