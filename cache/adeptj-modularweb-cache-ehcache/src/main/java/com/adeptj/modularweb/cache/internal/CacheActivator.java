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
package com.adeptj.modularweb.cache.internal;

import java.util.Hashtable;

import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;

import com.adeptj.modularweb.cache.api.CacheProvider;
import com.adeptj.modularweb.cache.impl.CacheProviderImpl;

/**
 * CacheActivator.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public class CacheActivator implements BundleActivator {

	private ServiceRegistration<?> svcReg;

	private CacheManager cacheMgr;

	@Override
	public void start(BundleContext context) throws Exception {
		Hashtable<String, Object> props = new Hashtable<>();
		props.put(Constants.SERVICE_VENDOR, "AdeptJ");
		props.put(Constants.SERVICE_PID, CacheProviderImpl.SERVICE_PID);
		props.put(Constants.SERVICE_DESCRIPTION, "AdeptJ OSGi CacheProvider Factory");
		this.cacheMgr = CacheManagerBuilder.newCacheManagerBuilder().build(true);
		this.svcReg = context.registerService(
				new String[] { ManagedServiceFactory.class.getName(), CacheProvider.class.getName() },
				new CacheProviderImpl(this.cacheMgr), props);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		svcReg.unregister();
		this.cacheMgr.close();
	}

}
