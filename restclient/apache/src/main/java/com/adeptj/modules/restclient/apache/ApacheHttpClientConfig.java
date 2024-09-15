/*
###############################################################################
#                                                                             #
#    Copyright 2016-2024, AdeptJ (http://www.adeptj.com)                      #
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
package com.adeptj.modules.restclient.apache;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Apache HttpClient configurations.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ObjectClassDefinition(
        name = "AdeptJ Apache HttpClient Configuration",
        description = "AdeptJ Apache HttpClient Configuration"
)
public @interface ApacheHttpClientConfig {

    @AttributeDefinition(
            name = "Skip HostName Verification",
            description = "Whether to skip HostName verification"
    )
    boolean skip_hostname_verification() default true;

    @AttributeDefinition(
            name = "Disable Cookie Management",
            description = "Whether to disable cookie management"
    )
    boolean disable_cookie_management();

    @AttributeDefinition(
            name = "Connect Timeout",
            description = "HttpClient request connect timeout (milliseconds)."
    )
    int connect_timeout() default 300000; // Caller has option to override this using RequestConfig

    @AttributeDefinition(
            name = "Connection Request Timeout",
            description = "HttpClient connection request timeout (milliseconds)."
    )
    int connection_request_timeout() default 60000;

    @AttributeDefinition(
            name = "Socket Timeout",
            description = "HttpClient request socket timeout (milliseconds)."
    )
    int socket_timeout() default 300000; // Caller has option to override this using RequestConfig

    // <----------------------------- HttpClient ConnectionPool Settings ----------------------------->

    @AttributeDefinition(
            name = "HttpClient ConnectionPool Max Idle Time",
            description = "Maximum time a connection is kept alive in HttpClient ConnectionPool (milliseconds)."
    )
    long max_idle_time() default 60000;

    @AttributeDefinition(
            name = "HttpClient ConnectionPool Max Connections",
            description = "Maximum number of connections in HttpClient ConnectionPool."
    )
    int max_total() default 100;

    @AttributeDefinition(
            name = "HttpClient ConnectionPool Idle Timeout",
            description = "Maximum time a connection remains idle in HttpClient ConnectionPool (seconds)."
    )
    int idle_timeout() default 60;

    @AttributeDefinition(
            name = "HttpClient ConnectionPool Max Connections Per Route",
            description = "Maximum number of default connections per host."
    )
    int max_per_route() default 10;

    @AttributeDefinition(
            name = "HttpClient ConnectionPool Inactive Connection Validation Time",
            description = "Time interval for validating the connection after inactivity (milliseconds)."
    )
    int validate_after_inactivity() default 5000;

    // <-------------------------------------------- RestClient Settings -------------------------------------------->

    @AttributeDefinition(
            name = "Debug Request",
            description = "Debug for detecting any issues with the request execution. Please keep it disabled on production systems."
    )
    boolean debug_request();

    @AttributeDefinition(
            name = "SLF4J MDC Request Attribute Name",
            description = "The attribute might already been setup by application during initial request processing."
    )
    String mdc_req_id_attribute_name() default "APACHE_HTTP_CLIENT_REQ_ID";
}
