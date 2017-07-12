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

package com.adeptj.modules.jaxrs.resteasy.internal;

import com.adeptj.modules.jaxrs.resteasy.JaxRSCoreConfig;
import org.jboss.resteasy.plugins.interceptors.CorsFilter;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

/**
 * JaxRSCorsFeature for configuring RESTEasy {@link CorsFilter}.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@Provider
public class JaxRSCorsFeature implements Feature {

    private static final String JOIN_DELIMITER = ", ";

    private JaxRSCoreConfig config;

    JaxRSCorsFeature(JaxRSCoreConfig config) {
        this.config = config;
    }

    /**
     * A call-back method called when the feature is to be enabled in a given
     * runtime configuration scope.
     * <p>
     * The responsibility of the feature is to properly update the supplied runtime configuration context
     * and return {@code true} if the feature was successfully enabled or {@code false} otherwise.
     * <p>
     * Note that under some circumstances the feature may decide not to enable itself, which
     * is indicated by returning {@code false}. In such case the configuration context does
     * not add the feature to the collection of enabled features and a subsequent call to
     * {Configuration#isEnabled(Feature)} or {Configuration#isEnabled(Class)} method would return {@code false}.
     * </p>
     *
     * @param context configurable context in which the feature should be enabled.
     * @return {@code true} if the feature was successfully enabled, {@code false} otherwise.
     */
    @Override
    public boolean configure(FeatureContext context) {
        CorsFilter corsFilter = new CorsFilter();
        corsFilter.setAllowCredentials(this.config.allowCredentials());
        corsFilter.setAllowedMethods(this.config.allowedMethods());
        corsFilter.setCorsMaxAge(this.config.corsMaxAge());
        corsFilter.setAllowedHeaders(Arrays.stream(this.config.allowedHeaders()).collect(joining(JOIN_DELIMITER)));
        corsFilter.setExposedHeaders(Arrays.stream(this.config.exposedHeaders()).collect(joining(JOIN_DELIMITER)));
        corsFilter.getAllowedOrigins().addAll(Arrays.stream(this.config.allowedOrigins()).collect(toSet()));
        context.register(corsFilter);
        LoggerFactory.getLogger(JaxRSCorsFeature.class).info("RESTEasy CorsFilter Configured Successfully!!");
        // Must return true to get this Feature enabled.
        return true;
    }
}
