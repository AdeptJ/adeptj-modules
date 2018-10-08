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
package com.adeptj.modules.oauth.provider.impl;

import com.adeptj.modules.oauth.common.OAuthProvider;
import com.adeptj.modules.oauth.provider.api.OAuthProviderFactory;
import com.github.scribejava.apis.LinkedInApi20;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.osgi.service.cm.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * OAuthProviderFactoryImpl.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class OAuthProviderFactoryImpl implements OAuthProviderFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuthProviderFactoryImpl.class);

    public static final String SERVICE_PID = "social.OAuthProvider.factory";

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
    public String getName() {
        return "AdeptJ Modular Web OAuthProviderFactory";
    }

    /**
     * {@inheritDoc}
     */
    public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        String providerName = (String) properties.get("");
        String apiKey = (String) properties.get("");
        String apiSecret = (String) properties.get("");
        String callbackURL = (String) properties.get("");
        LOGGER.info("Configs for pid: [{}]", pid);
        this.providers.put(pid, OAuthProvider.builder()
                .providerName(providerName)
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callbackURL(callbackURL)
                .api(LinkedInApi20.instance())
                .build());
        this.providerNamePidMappings.put(providerName, pid);
        LOGGER.info("Provider name to PID Map: [{}]", this.providerNamePidMappings);
    }

    /**
     * {@inheritDoc}
     */
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
