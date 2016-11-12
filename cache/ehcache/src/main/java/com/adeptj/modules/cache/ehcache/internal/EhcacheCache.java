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
package com.adeptj.modules.cache.ehcache.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import com.adeptj.modules.cache.common.Cache;

/**
 * Implementation for Cache interface, internally this uses the EHCache
 * CacheManager for performing the low level operations.
 * 
 * @author Rakesh.Kumar
 */
public class EhcacheCache<K, V> implements Cache<K, V> {

	private org.ehcache.Cache<K, V> ehcache;

	public EhcacheCache(org.ehcache.Cache<K, V> backingCache) {
		this.ehcache = backingCache;
	}

	@Override
	public V get(K key) {
		return this.ehcache.get(key);
	}

	@Override
	public V put(K key, V value) {
		return this.ehcache.putIfAbsent(key, value);
	}

	@Override
	public V remove(K key) {
		V element = null;
		if (this.ehcache.containsKey(key)) {
			element = this.ehcache.get(key);
			this.ehcache.remove(key);
		}
		return element;
	}

	@Override
	public void clear() {
		this.ehcache.clear();
	}

	@Override
	public int size() {
		return CollectionUtils.size(ehcache);
	}

	@Override
	public Set<K> keys() {
		Set<K> keys = new HashSet<>();
		this.ehcache.iterator().forEachRemaining(action -> keys.add(action.getKey()));
		return keys;
	}

	@Override
	public Collection<V> values() {
		List<V> values = new ArrayList<>();
		this.ehcache.iterator().forEachRemaining(action -> values.add(action.getValue()));
		return values;
	}

}