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
package com.adeptj.modules.cache.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;

import com.adeptj.modules.cache.api.CacheProvider;
import com.adeptj.modules.cache.api.CacheProviderFactory;
import com.adeptj.modules.cache.api.CacheProviderType;

/**
 * CacheProviderFactoryImpl.
 * 
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(immediate = true)
@Service
public class CacheProviderFactoryImpl implements CacheProviderFactory {

	/**
	 * Collect all of the CacheProvider services as and when they become available.
	 */
	@Reference(referenceInterface = CacheProvider.class, cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	private final Map<String, CacheProvider> cacheProviders = new ConcurrentHashMap<>();

	@Override
	public CacheProvider getCacheProvider(CacheProviderType providerType) {
		return this.cacheProviders.get(providerType.toString());
	}

	protected void bindCacheProvider(CacheProvider provider) {
		this.cacheProviders.put(provider.getName(), provider);
	}

	protected void unbindCacheProvider(CacheProvider provider) {
		this.cacheProviders.remove(provider.getName());
	}
}
