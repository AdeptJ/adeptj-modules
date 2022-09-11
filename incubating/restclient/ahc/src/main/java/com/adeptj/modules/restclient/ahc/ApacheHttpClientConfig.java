package com.adeptj.modules.restclient.ahc;

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
    boolean skipHostnameVerification() default true;

    @AttributeDefinition(
            name = "Connect Timeout",
            description = "HttpClient request connect timeout (milliseconds)."
    )
    int connectTimeout() default 300000; // Caller has option to override this using RequestConfig

    @AttributeDefinition(
            name = "Connection Request Timeout",
            description = "HttpClient connection request timeout (milliseconds)."
    )
    int connectionRequestTimeout() default 60000;

    @AttributeDefinition(
            name = "Socket Timeout",
            description = "HttpClient request socket timeout (milliseconds)."
    )
    int socketTimeout() default 300000; // Caller has option to override this using RequestConfig

    // <----------------------------- HttpClient ConnectionPool Settings ----------------------------->

    @AttributeDefinition(
            name = "HttpClient ConnectionPool Max Idle Time",
            description = "Maximum time a connection is kept alive in HttpClient ConnectionPool (milliseconds)."
    )
    long maxIdleTime() default 60000;

    @AttributeDefinition(
            name = "HttpClient ConnectionPool Max Connections",
            description = "Maximum number of connections in HttpClient ConnectionPool."
    )
    int maxTotal() default 100;

    @AttributeDefinition(
            name = "HttpClient ConnectionPool Idle Timeout",
            description = "Maximum time a connection remains idle in HttpClient ConnectionPool (seconds)."
    )
    int idleTimeout() default 60;

    @AttributeDefinition(
            name = "HttpClient ConnectionPool Max Connections Per Route",
            description = "Maximum number of default connections per host."
    )
    int maxPerRoute() default 10;

    @AttributeDefinition(
            name = "HttpClient ConnectionPool Inactive Connection Validation Time",
            description = "Time interval for validating the connection after inactivity (milliseconds)."
    )
    int validateAfterInactivity() default 5000;

    @AttributeDefinition(
            name = "Debug Request",
            description = "Debug for detecting any issues with the request execution. Please keep it disabled on production systems."
    )
    boolean debug_request();

    @AttributeDefinition(
            name = "SLF4J MDC Request Attribute Name",
            description = "The attribute might already been setup by application during initial request processing."
    )
    String mdc_req_id_attribute_name() default "REQ_ID";
}
