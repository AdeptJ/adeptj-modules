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

import java.util.Collection;
import java.util.Set;

import com.adeptj.modules.cache.common.Cache;

/**
 * Implementation for Cache interface, internally this uses the Infinispan for performing the low level operations.
 * 
 * @author Rakesh.Kumar
 */
public class InfinispanCache<K, V> implements Cache<K, V> {
	
	private org.infinispan.Cache<K, V> cache;

	public InfinispanCache(org.infinispan.Cache<K, V> cache) {
		this.cache = cache;
	}

	@Override
	public V get(K key) {
		return cache.get(key);
	}

	@Override
	public V put(K key, V value) {
		return cache.put(key, value);
	}

	@Override
	public V remove(K key) {
		return cache.remove(key);
	}

	@Override
	public void clear() {
		cache.clear();
	}

	@Override
	public int size() {
		return cache.size();
	}

	@Override
	public Set<K> keys() {
		return cache.keySet();
	}

	@Override
	public Collection<V> values() {
		return cache.values();
	}
	
}