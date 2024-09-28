/*
###############################################################################
#                                                                             #
#    Copyright 2016-2024, AdeptJ (http://adeptj.com)                          #
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
package com.adeptj.modules.commons.cache.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import static com.adeptj.modules.commons.cache.internal.CaffeineCacheConfigFactory.PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * Factory for creating CaffeineCache configurations.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = CaffeineCacheConfigFactory.CaffeineCacheConfig.class, factory = true)
@Component(service = CaffeineCacheConfigFactory.class, name = PID, configurationPolicy = REQUIRE)
public class CaffeineCacheConfigFactory {

    static final String PID = "com.adeptj.modules.cache.caffeine.CaffeineCacheConfig.factory";

    public CaffeineCacheConfigFactory() {
        // Added for Sonar issue.
    }

    /**
     * Configuration for Caffeine cache.
     *
     * @author Rakesh Kumar, AdeptJ
     */
    @ObjectClassDefinition(
            name = "AdeptJ CaffeineCache Configuration Factory",
            description = "Factory for creating AdeptJ CaffeineCache Configurations."
    )
    public @interface CaffeineCacheConfig {

        @AttributeDefinition(name = "Cache Name", description = "A meaningful name of the configured cache.")
        String cache_name(); // NOSONAR

        @AttributeDefinition(
                name = "Cache Spec",
                description = "The cache spec literal for configuring Caffeine cache. " +
                        "Please see - https://github.com/ben-manes/caffeine/wiki/Specification"
        )
        String cache_spec() default "maximumSize=16,expireAfterWrite=3600s"; // NOSONAR

        // name hint non-editable property
        String webconsole_configurationFactory_nameHint() default
                "Caffeine Cache ({" + "cache.name" + "}" + ": " + "{" + "cache.spec" + "})"; // NOSONAR
    }
}