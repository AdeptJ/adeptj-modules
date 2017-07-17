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
package com.adeptj.modules.commons.oauth.internal;

import static com.adeptj.modules.commons.oauth.common.Constants.MANAGED_SERVICE_FACTORY;
import static com.adeptj.modules.commons.oauth.common.Constants.OAUTH_PROVIDER_FACTORY;

import java.util.Dictionary;
import java.util.Hashtable;

import com.adeptj.modules.commons.oauth.provider.impl.OAuthProviderFactoryImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

/**
 * Activator.
 * 
 * @author Rakesh.Kumar, AdeptJ
 */
public class Activator implements BundleActivator {

	private ServiceRegistration<?> serviceReg;

	@Override
	public void start(BundleContext context) throws Exception {
		Dictionary<String, Object> props = new Hashtable<>();
		props.put(Constants.SERVICE_PID, OAuthProviderFactoryImpl.SERVICE_PID);
		props.put(Constants.SERVICE_VENDOR, "AdeptJ Modular Web");
		props.put(Constants.SERVICE_DESCRIPTION, "AdeptJ Modular Web OAuthProviderFactory");
		this.serviceReg = context.registerService(new String[] { MANAGED_SERVICE_FACTORY, OAUTH_PROVIDER_FACTORY },
				new OAuthProviderFactoryImpl(), props);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		this.serviceReg.unregister();
	}

}
