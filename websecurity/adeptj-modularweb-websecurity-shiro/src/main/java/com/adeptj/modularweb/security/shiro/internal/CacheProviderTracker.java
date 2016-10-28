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
package com.adeptj.modularweb.security.shiro.internal;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.adeptj.modularweb.cache.api.CacheProvider;

/**
 * Track the {@link CacheProvider}.
 * 
 * @author Rakesh.Kumar, AdeptJ..
 */
public class CacheProviderTracker extends ServiceTracker<CacheProvider, CacheProvider> {

	private static CacheProvider cacheProvider;

	public CacheProviderTracker(BundleContext context, Class<CacheProvider> clazz) {
		super(context, clazz, null);
	}

	@Override
	public CacheProvider addingService(ServiceReference<CacheProvider> reference) {
		cacheProvider = super.addingService(reference);
		return cacheProvider;
	}

	@Override
	public void modifiedService(ServiceReference<CacheProvider> reference, CacheProvider service) {
		super.modifiedService(reference, service);
	}

	@Override
	public void removedService(ServiceReference<CacheProvider> reference, CacheProvider service) {
		super.removedService(reference, service);
		cacheProvider = null;
	}

	public static CacheProvider getCacheProvider() {
		return cacheProvider;
	}
}
