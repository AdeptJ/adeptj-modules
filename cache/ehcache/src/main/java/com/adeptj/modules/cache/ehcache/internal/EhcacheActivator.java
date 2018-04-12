/*
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

package com.adeptj.modules.cache.ehcache.internal;

import com.adeptj.modules.cache.api.CacheProvider;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;

import java.util.Hashtable;

/**
 * EhcacheActivator.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public class EhcacheActivator implements BundleActivator {

	private ServiceRegistration<?> svcReg;

	private CacheManager cacheMgr;

	@Override
	public void start(BundleContext context) throws Exception {
		Hashtable<String, Object> props = new Hashtable<>();
		props.put(Constants.SERVICE_VENDOR, "AdeptJ");
		// SERVICE_PID should match the name property of the @Component annotation on EhcacheCacheProvider.
		// Otherwise ConfigurationAdmin won't be able to bind the factory configuration.
		//props.put(Constants.SERVICE_PID, EhcacheCacheProvider.FACTORY_PID);
		props.put(Constants.SERVICE_DESCRIPTION, "AdeptJ Modules Ehcache Cache Factory");
		this.cacheMgr = CacheManagerBuilder.newCacheManagerBuilder().build(true);
		this.svcReg = context.registerService(
				new String[] { ManagedServiceFactory.class.getName(), CacheProvider.class.getName() },
				new EhcacheCacheProvider(this.cacheMgr), props);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		svcReg.unregister();
		this.cacheMgr.close();
	}

}
