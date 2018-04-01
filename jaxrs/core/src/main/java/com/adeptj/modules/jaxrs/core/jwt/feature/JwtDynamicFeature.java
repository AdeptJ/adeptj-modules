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
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
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

    private Map<String, List<String>> resourceClassMethodsMapping;

    @Reference(target = "(filter.name=dyna-jwt)")
    private JwtFilter jwtFilter;

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        String resource = resourceInfo.getResourceClass().getName();
        String method = resourceInfo.getResourceMethod().getName();
        this.resourceClassMethodsMapping.entrySet()
                .stream()
                .filter(entry -> resource.equals(entry.getKey()) && entry.getValue().contains(method))
                .peek(entry -> LOGGER.info("Enabling DynamicJwtFilter for mapping [{}#{}]", resource, method))
                .forEach(entry -> context.register(this.jwtFilter, AUTHENTICATION));
    }

    // -------------------- INTERNAL --------------------

    // Component Lifecycle Methods

    @Activate
    protected void start(JwtDynamicFeatureConfig config) {
        this.resourceClassMethodsMapping = Stream.of(config.resourceClassAndMethodsMapping())
                .map(mapping -> mapping.split("="))
                .collect(toMap(mapping -> mapping[0], mapping -> Arrays.asList(mapping[1].split(","))));
    }
}
