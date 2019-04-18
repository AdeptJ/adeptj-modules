/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://adeptj.com)                               #
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

package com.adeptj.modules.cache.caffeine.internal;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;

import static com.adeptj.modules.cache.caffeine.internal.CaffeineCacheFactory.PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * Factory for creating Caffeine cache.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = CaffeineCacheConfig.class, factory = true)
@Component(service = CaffeineCacheFactory.class, name = PID, configurationPolicy = REQUIRE)
public class CaffeineCacheFactory {

    static final String PID = "com.adeptj.modules.cache.caffeine.CaffeineCache.factory";

    private CaffeineCacheConfig cacheConfig;

    CaffeineCacheConfig getCacheConfig() {
        return cacheConfig;
    }

    @Activate
    protected void start(CaffeineCacheConfig config) {
        this.cacheConfig = config;
    }

    @Deactivate
    protected void stop(CaffeineCacheConfig config) {
        this.cacheConfig = null;
    }
}