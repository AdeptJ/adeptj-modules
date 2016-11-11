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
package com.adeptj.modules.jaxrs.resteasy.internal;

import java.lang.reflect.Field;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JaxrsResourceInjectedOSGiServiceTracker.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public class JaxrsResourceInjectedOSGiServiceTracker extends ServiceTracker<Object, Object> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JaxrsResourceInjectedOSGiServiceTracker.class);

	private Object jaxrsResource;

	private Field injected;

	public JaxrsResourceInjectedOSGiServiceTracker(BundleContext context, Class<?> klazz, Object jaxrsResource,
			Field injected) {
		super(context, klazz.getName(), null);
		this.jaxrsResource = jaxrsResource;
		this.injected = injected;
	}

	@Override
	public Object addingService(ServiceReference<Object> reference) {
		Object service = super.addingService(reference);
		try {
			this.injected.setAccessible(true);
			this.injected.set(jaxrsResource, service);
		} catch (IllegalAccessException ex) {
			LOGGER.error("Exception!!", ex);
		}
		return service;
	}

	@Override
	public void removedService(ServiceReference<Object> reference, Object service) {
		super.removedService(reference, service);
		try {
			this.injected.setAccessible(true);
			this.injected.set(jaxrsResource, null);
		} catch (IllegalAccessException ex) {
			LOGGER.error("Exception!!", ex);
		}
	}

}
