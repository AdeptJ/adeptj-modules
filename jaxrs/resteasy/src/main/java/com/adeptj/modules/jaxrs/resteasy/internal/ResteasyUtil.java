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

import com.adeptj.modules.jaxrs.resteasy.ResteasyConfig;
import org.jboss.resteasy.plugins.interceptors.CorsFilter;

import javax.ws.rs.Path;
import java.util.Arrays;

import static com.adeptj.modules.commons.utils.Constants.COMMA;

/**
 * Utilities for RESTEasy bootstrap process.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class ResteasyUtil {

    private ResteasyUtil() {
    }

    static CorsFilter newCorsFilter(ResteasyConfig config) {
        CorsFilter corsFilter = new CorsFilter();
        corsFilter.setAllowCredentials(config.allowCredentials());
        corsFilter.setCorsMaxAge(config.corsMaxAge());
        corsFilter.setExposedHeaders(String.join(COMMA, config.exposedHeaders()));
        corsFilter.setAllowedMethods(String.join(COMMA, config.allowedMethods()));
        corsFilter.setAllowedHeaders(String.join(COMMA, config.allowedHeaders()));
        corsFilter.getAllowedOrigins().addAll(Arrays.asList(config.allowedOrigins()));
        return corsFilter;
    }

    static boolean isPathAnnotationPresent(Object resource) {
        return resource.getClass().isAnnotationPresent(Path.class);
    }
}
