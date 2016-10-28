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
package com.adeptj.modularweb.security.shiro.cache;

import com.adeptj.modularweb.cache.api.CacheProvider;
import com.adeptj.modularweb.security.shiro.internal.CacheProviderTracker;

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Shiro CacheManager for managing Authorization Cache and Session Cache.
 *
 * @author Rakesh.Kumar, AdeptJ..
 */
public class ShiroCacheManager implements CacheManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiroCacheManager.class);

    public static final String SHIRO_AUTHORIZATION_CACHE = "osgiRealm.authorizationCache";

    public static final String SHIRO_SESSION_CACHE = "shiro-activeSessionCache";

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        LOGGER.info("Getting cache: [{}]", name);
        CacheProvider cacheProvider = CacheProviderTracker.getCacheProvider();
        Cache<K, V> shiroCache = null;
        switch (name) {
            case SHIRO_AUTHORIZATION_CACHE:
                com.adeptj.modularweb.cache.api.Cache<K, V> cache = (com.adeptj.modularweb.cache.api.Cache<K, V>) cacheProvider
                        .getCache(SHIRO_AUTHORIZATION_CACHE, PrincipalCollection.class, AuthorizationInfo.class);
                if (cache == null) {
                    LOGGER.warn("Cache: [{}] is not configured properly!!", name);
                } else {
                    shiroCache = new AuthorizationCache<>(cache);
                }
                break;
            case SHIRO_SESSION_CACHE:
                com.adeptj.modularweb.cache.api.Cache<K, V> sessionCache = (com.adeptj.modularweb.cache.api.Cache<K, V>) cacheProvider
                        .getCache(SHIRO_SESSION_CACHE, Serializable.class, Session.class);
                if (sessionCache == null) {
                    LOGGER.warn("Cache: [{}] is not configured properly!!", name);
                } else {
                    shiroCache = new ShiroSessionCache<>(sessionCache);
                }
                break;
            default:
            	LOGGER.warn("Unknown Cache: {}", name);
                break;
        }
        return shiroCache;

    }
}
