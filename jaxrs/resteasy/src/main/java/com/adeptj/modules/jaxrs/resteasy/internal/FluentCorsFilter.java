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

import org.jboss.resteasy.plugins.interceptors.CorsFilter;

import java.util.stream.Stream;

import static com.adeptj.modules.commons.utils.Constants.COMMA;
import static java.util.stream.Collectors.toSet;

/**
 * Utility to build Resteasy's {@link CorsFilter} in fluent style.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
class FluentCorsFilter {

    private CorsFilter corsFilter;

    FluentCorsFilter() {
        this.corsFilter = new CorsFilter();
    }

    FluentCorsFilter allowCredentials(boolean allowCredentials) {
        this.corsFilter.setAllowCredentials(allowCredentials);
        return this;
    }

    FluentCorsFilter corsMaxAge(int corsMaxAge) {
        this.corsFilter.setCorsMaxAge(corsMaxAge);
        return this;
    }

    FluentCorsFilter exposedHeaders(String[] exposedHeaders) {
        this.corsFilter.setExposedHeaders(String.join(COMMA, exposedHeaders));
        return this;
    }

    FluentCorsFilter allowedMethods(String[] allowedMethods) {
        this.corsFilter.setAllowedMethods(String.join(COMMA, allowedMethods));
        return this;
    }

    FluentCorsFilter allowedHeaders(String[] allowedHeaders) {
        this.corsFilter.setAllowedHeaders(String.join(COMMA, allowedHeaders));
        return this;
    }

    FluentCorsFilter allowedOrigins(String[] allowedOrigins) {
        this.corsFilter.getAllowedOrigins().addAll(Stream.of(allowedOrigins).collect(toSet()));
        return this;
    }

    CorsFilter corsFilter() {
        return corsFilter;
    }
}
