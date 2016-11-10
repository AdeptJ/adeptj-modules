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
package com.adeptj.modularweb.jaxrs.internal.activator;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.Servlet;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import com.adeptj.modularweb.osgi.jaxrs.dispatcher.ExtHttpServletDispatcher;

/**
 * ResteasyActivator.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public class ResteasyActivator implements BundleActivator {

	private ServiceRegistration<Servlet> servRegDispatcher;

	/**
	 * Registers the RESTEasy HttpServletDispatcher
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		Dictionary<String, Object> props = new Hashtable<>();
		props.put(Constants.SERVICE_VENDOR, "AdeptJ");
		props.put("osgi.http.whiteboard.servlet.name", "RESTEasy HttpServletDispatcher");
		props.put("osgi.http.whiteboard.servlet.pattern", "/*");
		props.put("osgi.http.whiteboard.servlet.asyncSupported", "true");
		props.put("servlet.init.resteasy.servlet.mapping.prefix", "/");
		servRegDispatcher = context.registerService(Servlet.class, new ExtHttpServletDispatcher(), props);
	}

	/**
	 * Unregister the RESTEasy HttpServletDispatcher
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		servRegDispatcher.unregister();
	}
}
