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

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import static javax.ws.rs.HttpMethod.DELETE;
import static javax.ws.rs.HttpMethod.GET;
import static javax.ws.rs.HttpMethod.HEAD;
import static javax.ws.rs.HttpMethod.OPTIONS;
import static javax.ws.rs.HttpMethod.POST;
import static javax.ws.rs.HttpMethod.PUT;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.CACHE_CONTROL;
import static javax.ws.rs.core.HttpHeaders.CONTENT_LANGUAGE;
import static javax.ws.rs.core.HttpHeaders.CONTENT_LENGTH;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.HttpHeaders.EXPIRES;
import static javax.ws.rs.core.HttpHeaders.LAST_MODIFIED;

/**
 * JAX-RS(RESTEasy) configurations.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@ObjectClassDefinition(
        name = "AdeptJ RESTEasy Configuration",
        description = "Configuration for RESTEasy Framework"
)
public @interface ResteasyConfig {

    String PRAGMA = "Pragma";

    // CorsFilter configs

    @AttributeDefinition(
            name = "CORS Preflight Max Age",
            description = "Max age of preflight CORS response(1 day by default)."
                    + "[Access-Control-Max-Age] header indicates how long the results of a preflight request can be cached."
    )
    int corsMaxAge() default 86400; // Indicates that preflight response is good for 86400 seconds or 1 day

    @AttributeDefinition(
            name = "CORS Allow Credentials",
            description = "Whether to allow credentials for CORS request. "
                    + "Value of [Access-Control-Allow-Credentials] header. "
                    + "Note: When responding to a credentialed request, the server must specify an origin "
                    + "in the value of the [Access-Control-Allow-Origin] header, "
                    + "instead of specifying the (*) wildcard."
    )
    boolean allowCredentials() default true;

    @AttributeDefinition(
            name = "CORS Allowed Methods",
            description = "Allowed methods in a CORS request. "
                    + "Note: The [Access-Control-Allow-Methods] header specifies the method "
                    + "or methods allowed when accessing the resource. "
                    + "This is used in response to a preflight request."
    )
    String[] allowedMethods() default {
            GET,
            POST,
            PUT,
            DELETE,
            OPTIONS,
            HEAD
    };

    @AttributeDefinition(
            name = "CORS Allowed Headers",
            description = "Allowed headers in a CORS request. "
                    + "Note: [Access-Control-Allow-Headers] header is used in response to a preflight "
                    + "request to indicate which HTTP headers "
                    + "can be used when making the actual request"
    )
    String[] allowedHeaders() default {
            CACHE_CONTROL,
            CONTENT_LANGUAGE,
            CONTENT_LENGTH,
            CONTENT_TYPE,
            EXPIRES,
            LAST_MODIFIED,
            PRAGMA,
            ACCEPT
    };

    @AttributeDefinition(
            name = "CORS Exposed Headers",
            description = "This header lets a server whitelist headers that clients are allowed to access. "
                    + "Value of [Access-Control-Expose-Headers] header."
    )
    String[] exposedHeaders() default {
            CONTENT_LENGTH,
            AUTHORIZATION,
    };

    @AttributeDefinition(
            name = "CORS Allowed Origins",
            description = "Allowed origins for CORS request, Note: Please don't use (*) on production systems! "
                    + "Value of [Access-Control-Allow-Origin] header."
    )
    String[] allowedOrigins() default {"*"};

    @AttributeDefinition(
            name = "RESTEasy Provider Blacklist",
            description = "RESTEasy providers which are omitted from deployment."
    )
    String[] blacklistedProviders() default {
            "org.jboss.resteasy.plugins.validation.ValidatorContextResolver",
            "org.jboss.resteasy.plugins.validation.ValidatorContextResolverCDI",
    };

    // Common configs

    @AttributeDefinition(
            name = "Send Exception Trace",
            description = "Whether to send exception trace in response from GenericExceptionHandler."
    )
    boolean sendExceptionTrace() default true;
}
