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

package com.adeptj.modules.jaxrs.resteasy;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * JAX-RS(RESTEasy) configurations.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@ObjectClassDefinition(name = "AdeptJ JAX-RS Core Configuration", description = "Configuration for RESTEasy Framework")
public @interface JaxRSCoreConfig {

    // CorsFilter configs

    @AttributeDefinition(name = "CORS Preflight Max Age", description = "Max age of preflight CORS response")
    int corsMaxAge() default 86400; // Indicates that preflight response is good for 86400 seconds or 1 day

    @AttributeDefinition(name = "CORS Allow Credentials", description = "Whether to allow credentials for CORS request")
    boolean allowCredentials() default true;

    @AttributeDefinition(name = "CORS Allowed Methods", description = "Allowed methods in a CORS request")
    String allowedMethods() default "GET, POST, PUT, OPTIONS, HEAD, DELETE";

    @AttributeDefinition(name = "CORS Allowed Headers", description = "Allowed headers in a CORS request")
    String[] allowedHeaders();

    @AttributeDefinition(name = "CORS Exposed Headers", description = "Exposed headers in a CORS request")
    String[] exposedHeaders();

    @AttributeDefinition(name = "CORS Allowed Origins", description = "Allowed origins for CORS request")
    String[] allowedOrigins() default "*";
}
