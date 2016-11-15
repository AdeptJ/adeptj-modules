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
package com.adeptj.modularweb.oauth.provider.impl;
import java.util.Dictionary;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adeptj.modularweb.oauth.common.OAuthProvider;
import com.adeptj.modularweb.oauth.provider.api.OAuthProviderFactory;
import com.github.scribejava.apis.LinkedInApi20;
import com.github.scribejava.core.oauth.OAuth20Service;

/**
 * OAuthProviderFactoryImpl.
 * 
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(configurationFactory = true, ds = false, metatype = true, name = OAuthProviderFactoryImpl.SERVICE_PID, 
	label = "AdeptJ Modular Web OAuthProviderFactory", description = "AdeptJ Modular Web OAuthProviderFactory for OAuth Login using multiple providers.")
public class OAuthProviderFactoryImpl implements OAuthProviderFactory, ManagedServiceFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(OAuthProviderFactoryImpl.class);
	
	public static final String SERVICE_PID = "social.OAuthProvider.factory";

	@Property(label = "Provider Name", description = "OAuth Provider Name", value = "")
	private static final String PROVIDER_NAME = "providerName";

	@Property(label = "Provider Api Key", description = "OAuth Provider Api Key", value = "")
	private static final String API_KEY = "apiKey";

	@Property(label = "Provider Api Secret", description = "OAuth Provider Api Secret", passwordValue = "")
	private static final String API_SECRET = "apiSecret";

	@Property(label = "Provider Callback URL", description = "OAuth Provider Callback URL", value = "")
	private static final String CALLBACK_URL = "callbackURL";
	
	/**
	 * Map that will hold the OAuthProvider Instances against a PID.
	 */
	private final ConcurrentMap<String, OAuthProvider> providers = new ConcurrentHashMap<>();
	
	/**
	 * Map that will hold the OAuth20Service Instances against a provider name.
	 */
	private final ConcurrentMap<String, OAuth20Service> oAuth2Services = new ConcurrentHashMap<>();

	/**
	 * Map that will hold the Provider name to PID mapping.
	 */
	private final ConcurrentMap<String, String> providerNamePidMappings = new ConcurrentHashMap<>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return "AdeptJ Modular Web OAuthProviderFactory";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
		String providerName = (String) properties.get(PROVIDER_NAME);
		String apiKey = (String) properties.get(API_KEY);
		String apiSecret = (String) properties.get(API_SECRET);
		String callbackURL = (String) properties.get(CALLBACK_URL);
		LOGGER.info("Configs for pid: [{}]", pid);
		this.providers.put(pid, new OAuthProvider.Builder().providerName(providerName).apiKey(apiKey)
				.apiSecret(apiSecret).callbackURL(callbackURL).api(LinkedInApi20.instance()).build());
		this.providerNamePidMappings.put(providerName, pid);
		LOGGER.info("Provider name to PID Map: [{}]", this.providerNamePidMappings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleted(String pid) {
		LOGGER.info("Removing configs for pid: [{}]", pid);
		OAuthProvider provider = this.providers.remove(pid);
		if (provider != null) {
			this.providerNamePidMappings.remove(provider.getProviderName());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OAuthProvider getProvider(String providerName) {
		LOGGER.info("Getting OAuthProvider for: [{}]", providerName);
		return this.providers.get(this.providerNamePidMappings.get(providerName));
	}

	@Override
	public void addOAuth2Service(String providerName, OAuth20Service service) {
		this.oAuth2Services.put(providerName, service);
	}

	@Override
	public OAuth20Service getOAuth2Service(String providerName) {
		return this.oAuth2Services.get(providerName);
	}

}
