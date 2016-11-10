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
package com.adeptj.modularweb.thymeleaf.cache;

import java.util.Set;

import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheEntryValidityChecker;

public class ThymeleafEhCache<K, V> implements ICache<K, V> {

	@Override
	public void put(K key, V value) {
		
	}

	@Override
	public V get(K key) {
		return null;
	}

	@Override
	public V get(K key, ICacheEntryValidityChecker<? super K, ? super V> validityChecker) {
		return null;
	}

	@Override
	public void clear() {
		
	}

	@Override
	public void clearKey(K key) {
		
	}

	@Override
	public Set<K> keySet() {
		return null;
	}

}
