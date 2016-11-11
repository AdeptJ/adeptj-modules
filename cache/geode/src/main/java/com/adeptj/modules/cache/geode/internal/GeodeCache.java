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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.adeptj.modules.cache.api.Cache;

/**
 * Implementation for Cache interface, internally this uses the EHCache
 * CacheManager for performing the low level operations.
 * 
 * @author Rakesh.Kumar
 */
public class GeodeCache<K, V> implements Cache<K, V> {

	public GeodeCache() {
	}

	@Override
	public V get(K key) {
		return null;
	}

	@Override
	public V put(K key, V value) {
		return null;
	}

	@Override
	public V remove(K key) {
		return null;
	}

	@Override
	public void clear() {
	}

	/**
	 * NOTE: Very expensive.
	 */
	@Override
	public int size() {
		return 0;
	}

	/**
	 * NOTE: Very expensive.
	 */
	@Override
	public Set<K> keys() {
		Set<K> keys = new HashSet<>();
		return keys;
	}

	/**
	 * NOTE: Very expensive.
	 */
	@Override
	public Collection<V> values() {
		List<V> values = new ArrayList<>();
		return values;
	}

}