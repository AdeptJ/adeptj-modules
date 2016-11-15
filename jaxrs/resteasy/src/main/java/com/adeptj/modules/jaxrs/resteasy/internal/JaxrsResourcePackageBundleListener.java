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

import com.adeptj.modularweb.common.OSGiService;
import com.adeptj.modules.jaxrs.resteasy.adapter.api.ResteasyResourceAdapter;
import com.adeptj.modules.jaxrs.resteasy.common.JaxrsResourceInjectedOSGiServiceTrackers;

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Path;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * JaxrsResourcePackageBundleListener.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public class JaxrsResourcePackageBundleListener implements BundleTrackerCustomizer<Map<String, Class<?>>> {

	public static final String HEADER = "JAXRS-Resource-Packages";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JaxrsResourcePackageBundleListener.class);

	private final BundleTracker<Map<String, Class<?>>> bundleTracker;

	private ResteasyResourceAdapter resourceAdapter;

	private ConcurrentMap<String, List<ServiceReference<?>>> serviceRefs = new ConcurrentHashMap<>(32);

	public JaxrsResourcePackageBundleListener(BundleContext bundleContext, ResteasyResourceAdapter resourceAdapter) {
		this.resourceAdapter = resourceAdapter;
		this.bundleTracker = new BundleTracker<>(bundleContext, Bundle.ACTIVE, this);
		this.bundleTracker.open();
	}

	@Override
	public Map<String, Class<?>> addingBundle(Bundle bundle, BundleEvent event) {
		Map<String, Class<?>> resourceMap = new ConcurrentHashMap<>(32);
		Dictionary<?, ?> headers = bundle.getHeaders();
		String packageList = (String) headers.get(HEADER);
		List<ServiceReference<?>> serviceReferences = new ArrayList<>();
		if (packageList != null) {
			BundleContext bundleContext = bundle.getBundleContext();
			packageList = StringUtils.deleteWhitespace(packageList);
			String[] packages = packageList.split(",");
			for (String singlePackage : packages) {
				Enumeration<URL> classUrls = bundle.findEntries("/" + singlePackage.replace('.', '/'), "*.class", true);
				if (classUrls == null) {
					continue;
				}
				while (classUrls.hasMoreElements()) {
					URL url = classUrls.nextElement();
					String className = toClassName(url);
					try {
						Class<?> resourceType = bundle.loadClass(className);
						Path path = resourceType.getAnnotation(Path.class);
						if (path != null) {
							this.registerResource(resourceMap, serviceReferences, bundleContext, resourceType, path);
						}
					} catch (ClassNotFoundException ex) {
						LOGGER.error("Exception!!", ex);
					}
				}
			}
			this.serviceRefs.put(bundle.getSymbolicName(), serviceReferences);
		}
		return resourceMap;
	}

	private void registerResource(Map<String, Class<?>> resourceMap, List<ServiceReference<?>> serviceReferences,
			BundleContext bundleContext, Class<?> resourceType, Path path) {
		try {
			Object resource = resourceType.newInstance();
			Field[] fields = resourceType.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(OSGiService.class)) {
					Class<?> type = field.getType();
					ServiceReference<?> serviceReference = bundleContext.getServiceReference(type);
					if (serviceReference == null) {
						// In case service is not available yet, open a ServiceTracker and inject the specified
						// OSGi Service when available in ServiceTracker#addingService.
						JaxrsResourceInjectedOSGiServiceTracker tracker = new JaxrsResourceInjectedOSGiServiceTracker(
								bundleContext, type, resource, field);
						tracker.open();
						JaxrsResourceInjectedOSGiServiceTrackers.INSTANCE.addServiceTracker(tracker);
					} else {
						field.setAccessible(true);
						field.set(resource, bundleContext.getService(serviceReference));
						serviceReferences.add(serviceReference);
					}
				}
			}
			this.resourceAdapter.addSingletonResource(path.value(), resource);
			resourceMap.put(path.value(), resourceType);
		} catch (InstantiationException | IllegalAccessException ex) {
			LOGGER.error("Exception!!", ex);
		}
	}

	private String toClassName(URL url) {
		final String file = url.getFile();
		final String cn = file.substring(1, file.length() - ".class".length());
		return cn.replace('/', '.');
	}

	public synchronized void unregisterAll() {
		this.bundleTracker.close();
	}

	@Override
	public void modifiedBundle(Bundle bundle, BundleEvent event, Map<String, Class<?>> resourceMap) {
		// NOP
	}

	@Override
	public void removedBundle(Bundle bundle, BundleEvent event, Map<String, Class<?>> resourceMap) {
		resourceMap.forEach((path, resourceType) -> {
			this.resourceAdapter.removeSingletonResource(path, resourceType);
			// Clean the tracked reference as well.
			Field[] fields = resourceType.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(OSGiService.class)) {
					JaxrsResourceInjectedOSGiServiceTrackers.INSTANCE.getTrackers().forEach(tracker -> {
						Object service = tracker.getService();
						if (service != null && field.getType().equals(service.getClass())) {
							tracker.close();
						}
					});
				}
			}
		});
		BundleContext bundleContext = bundle.getBundleContext();
		serviceRefs.forEach((symbolicName, services) -> {
			if (StringUtils.equals(symbolicName, bundle.getSymbolicName())) {
				services.forEach(servicesRef -> {
					bundleContext.ungetService(servicesRef);
				});
			}
		});
	}

}
