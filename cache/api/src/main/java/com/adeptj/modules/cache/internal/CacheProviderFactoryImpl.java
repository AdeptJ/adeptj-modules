/*
###############################################################################
#                                                                             # 
#    Copyright 2016, AdeptJ (http://www.adeptj.com)                           #
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

package com.adeptj.modules.cache.internal;

import com.adeptj.modules.cache.api.CacheProvider;
import com.adeptj.modules.cache.spi.CacheProviderFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * CacheProviderFactoryImpl.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = CacheProviderConfig.class)
@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class CacheProviderFactoryImpl implements CacheProviderFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheProviderFactoryImpl.class);

    private CacheProviderConfig config;

    /**
     * Collect all of the CacheProvider services as and when they become available.
     * <p>
     * Note: As per Felix SCR, dynamic references should be declared as volatile.
     */
    @Reference(
            service = CacheProvider.class,
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC
    )
    private volatile List<CacheProvider> cacheProviders = new ArrayList<>();

    @Override
    public Optional<CacheProvider> getCacheProvider(String providerName) {
        Objects.requireNonNull(providerName, "providerName can't be null!!");
        LOGGER.info("Getting [{}] CacheProvider.", providerName);
        return this.cacheProviders
                .stream()
                .filter(cacheProvider -> Arrays.asList(this.config.cacheProviders()).contains(providerName)
                        && providerName.equalsIgnoreCase(cacheProvider.getName()))
                .findFirst();
    }

    @Activate
    protected void start(CacheProviderConfig config) {
        this.config = config;
    }
}
