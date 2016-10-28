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
package com.adeptj.modularweb.osgi.jaxrs.dispatcher;

import static org.apache.commons.lang3.reflect.FieldUtils.getDeclaredField;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.validation.GeneralValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adeptj.modularweb.common.ClassLoaders;
import com.adeptj.modularweb.jaxrs.common.JaxrsResourceCollector;
import com.adeptj.modularweb.jaxrs.plugins.validation.GeneralValidatorContextResolver;

/**
 * ExtHttpServletDispatcher extends Resteasy HttpServletDispatcher so that the
 * providers can be loaded from this bundle's META-INF/services using bundle
 * class loader.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public class ExtHttpServletDispatcher extends HttpServletDispatcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExtHttpServletDispatcher.class);

	private static final long serialVersionUID = 8759503561853047365L;

	private volatile boolean initialized;

	private ConcurrentMap<String, Boolean> processedResources = new ConcurrentHashMap<>(32);

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		// For loading the providers from this bundle's META-INF/services directory.
		ClassLoaders.executeWith(this.getClass().getClassLoader(), () -> {
			try {
				super.init(servletConfig);
				Dispatcher dispatcher = this.getDispatcher();
				this.handleGeneralValidatorContextResolver(dispatcher.getProviderFactory());
				JaxrsResourceCollector.INSTANCE.getResources().forEach((alias, resource) -> {
					dispatcher.getRegistry().addSingletonResource(resource);
					processedResources.put(alias, Boolean.TRUE);
				});
				initialized = true;
				LOGGER.info("Resteasy HttpServletDispatcher initialized successfully!!");
			} catch (Exception ex) {
				LOGGER.error("Exception while adding resource", ex);
			}
			return null;
		});
	}

	protected void handleGeneralValidatorContextResolver(ResteasyProviderFactory factory) {
		try {
			Map.class.cast(getDeclaredField(ResteasyProviderFactory.class, "contextResolvers", true).get(factory))
					.remove(GeneralValidator.class);
			Set.class.cast(getDeclaredField(ResteasyProviderFactory.class, "providerClasses", true).get(factory))
					.remove(GeneralValidatorContextResolver.class);
			factory.registerProvider(GeneralValidatorContextResolver.class);
		} catch (IllegalArgumentException | IllegalAccessException ex) {
			LOGGER.error("Exception while adding ContextResolver", ex);
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		processedResources.clear();
		JaxrsResourceCollector.INSTANCE.getResources().forEach((alias, resource) -> {
			this.getDispatcher().getRegistry().removeRegistrations(resource.getClass());
		});
	}

	public synchronized boolean isInitialized() {
		return initialized;
	}

	public Map<String, Boolean> getProcessedResources() {
		return processedResources;
	}
}
