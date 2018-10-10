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

/**
 * Utilities for RESTEasy bootstrap process.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class ResteasyUtil {

    private ResteasyUtil() {
    }

    static CorsFilter buildCorsFilter(ResteasyConfig config) {
        return CorsFilterBuilder.newBuilder()
                .allowCredentials(config.allowCredentials())
                .corsMaxAge(config.corsMaxAge())
                .exposedHeaders(config.exposedHeaders())
                .allowedMethods(config.allowedMethods())
                .allowedHeaders(config.allowedHeaders())
                .allowedOrigins(config.allowedOrigins())
                .build();
    }

    static boolean isNotAnnotatedWithPath(Object resource) {
        return !resource.getClass().isAnnotationPresent(Path.class);
    }
}
