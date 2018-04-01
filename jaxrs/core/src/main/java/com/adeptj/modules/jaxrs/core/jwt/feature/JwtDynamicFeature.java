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

package com.adeptj.modules.jaxrs.core.jwt.feature;

import com.adeptj.modules.jaxrs.core.jwt.filter.JwtFilter;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.Priorities.AUTHENTICATION;

/**
 * JwtDynamicFeature
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Provider
@Designate(ocd = JwtDynamicFeatureConfig.class)
@Component(
        immediate = true,
        configurationPolicy = ConfigurationPolicy.REQUIRE,
        property = "osgi.jaxrs.provider=jwt-dyna-feature"
)
public class JwtDynamicFeature implements DynamicFeature {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtDynamicFeature.class);

    private Map<String, List<String>> resourceClassMethodMapping;

    @Reference(target = "(filter.name=dyna-jwt)")
    private JwtFilter jwtFilter;

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        this.resourceClassMethodMapping.entrySet()
                .stream()
                .filter(entry -> StringUtils.equals(entry.getKey(), resourceInfo.getResourceClass().getName()))
                .filter(entry -> entry.getValue().contains(resourceInfo.getResourceMethod().getName()))
                .forEach(entry -> {
                            LOGGER.info("Enabling DynamicJwtFilter for mapping [{}#{}]",
                                    resourceInfo.getResourceClass().getName(),
                                    resourceInfo.getResourceMethod().getName());
                            context.register(this.jwtFilter, AUTHENTICATION);
                        }
                );
    }

    // -------------------- INTERNAL --------------------

    // Component Lifecycle Methods

    @Activate
    protected void start(JwtDynamicFeatureConfig config) {
        this.resourceClassMethodMapping = new HashMap<>();
        for (String mapping : config.resourceClassMethodMapping()) {
            String[] classMethodMapping = mapping.split("=");
            this.resourceClassMethodMapping.put(classMethodMapping[0], Arrays.asList(classMethodMapping[1].split(",")));
        }
    }
}
