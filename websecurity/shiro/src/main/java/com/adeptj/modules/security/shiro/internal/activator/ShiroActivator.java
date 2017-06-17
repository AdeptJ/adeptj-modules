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
package com.adeptj.modules.security.shiro.internal.activator;

import com.adeptj.modularweb.cache.api.CacheProvider;
import com.adeptj.modules.security.shiro.internal.CacheProviderTracker;
import com.adeptj.modules.security.shiro.listener.ExtEnvironmentLoaderListener;

import org.apache.shiro.web.servlet.ShiroFilter;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import javax.servlet.Filter;
import javax.servlet.ServletContextListener;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * ShiroActivator.
 * 
 * @author Rakesh.Kumar, AdeptJ..
 */
public class ShiroActivator implements BundleActivator {

	private ServiceRegistration<Filter> servRegShiroFilter;

	private ServiceRegistration<ServletContextListener> servRegShiroListener;

	private CacheProviderTracker cacheProviderTracker;

	/**
	 * Initializes the Shiro Security Framework.
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		cacheProviderTracker = new CacheProviderTracker(context, CacheProvider.class);
		cacheProviderTracker.open();
		// Register the Shiro EnvironmentLoaderListener first.
		Dictionary<String, Object> shiroListenerProps = new Hashtable<>();
		shiroListenerProps.put(Constants.SERVICE_VENDOR, "AdeptJ");
		shiroListenerProps.put("osgi.http.whiteboard.listener", "true");
		servRegShiroListener = context.registerService(ServletContextListener.class, new ExtEnvironmentLoaderListener(),
				shiroListenerProps);
		// Now Register the ShiroFilter.
		Dictionary<String, Object> shiroFilterProps = new Hashtable<>();
		shiroFilterProps.put(Constants.SERVICE_VENDOR, "AdeptJ");
		shiroFilterProps.put("osgi.http.whiteboard.filter.name", "Shiro Filter");
		shiroFilterProps.put("osgi.http.whiteboard.filter.pattern", "/*");
		shiroFilterProps.put("osgi.http.whiteboard.filter.asyncSupported", "true");
		shiroFilterProps.put("osgi.http.whiteboard.filter.dispatcher",
				new String[] { "REQUEST", "INCLUDE", "FORWARD", "ASYNC", "ERROR" });
		servRegShiroFilter = context.registerService(Filter.class, new ShiroFilter(), shiroFilterProps);
	}

	/**
	 * Shutdown Shiro Security Framework.
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		servRegShiroFilter.unregister();
		servRegShiroListener.unregister();
		cacheProviderTracker.close();
	}
}
