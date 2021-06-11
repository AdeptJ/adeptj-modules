package com.adeptj.modules.restclient.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * AdeptJ Jetty HttpClient configurations.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@ObjectClassDefinition(
        name = "AdeptJ Jetty HttpClient Configuration",
        description = "AdeptJ Jetty HttpClient Configuration"
)
public @interface JettyHttpClientConfig {

    @AttributeDefinition(
            name = "Jetty HttpClient Name",
            description = "Name to be used by Jetty QueuedThreadPool(QTP) for its name."
    )
    String name() default "AdeptJ Jetty HttpClient";

    @AttributeDefinition(
            name = "Jetty HttpClient Connect Timeout",
            description = "The max time, in milliseconds, a connection can take to connect to destinations. " +
                    "Zero value means infinite timeout."
    )
    long connect_timeout() default 60000L;

    @AttributeDefinition(
            name = "Jetty HttpClient Idle Timeout",
            description = "The max time, in milliseconds, " +
                    "a connection can be idle (that is, without traffic of bytes in either direction)"
    )
    long idle_timeout() default 60000L;

    @AttributeDefinition(
            name = "Jetty HttpClient Max Connections Per Destination",
            description = "Sets the max number of connections to open to each destinations. " +
                    "RFC 2616 suggests that 2 connections should be opened per each destination, " +
                    "but browsers commonly open 6."
    )
    int max_connections_per_destination() default 64;

    @AttributeDefinition(
            name = "Jetty HttpClient Max Requests Queued Per Destination",
            description = "Sets the max number of requests that may be queued to a destination. " +
                    "If this HttpClient performs a high rate of requests to a destination, and all the connections" +
                    "managed by that destination are busy with other requests, then new requests will be queued up in the destination. " +
                    "This parameter controls how many requests can be queued before starting to reject them."
    )
    int max_requests_queued_per_destination() default 1024;

    @AttributeDefinition(
            name = "Jetty HttpClient Request Buffer Size",
            description = "The size of the buffer used to write requests."
    )
    int request_buffer_size() default 4096; // 4k

    @AttributeDefinition(
            name = "Jetty HttpClient Response Buffer Size",
            description = "The size of the buffer used to read responses."
    )
    int response_buffer_size() default 16384; // 16k

    @AttributeDefinition(
            name = "Jetty HttpClient Max Redirects",
            description = "Name to be used by Jetty QueuedThreadPool(QTP)"
    )
    int max_redirects() default 8;

    @AttributeDefinition(
            name = "Jetty HttpClient Address Resolution Timeout",
            description = "Sets the socket address resolution timeout(in milliseconds) used by the default " +
                    "SocketAddressResolver created by this HttpClient at startup."
    )
    long address_resolution_timeout() default 15000L;

    @AttributeDefinition(
            name = "Jetty HttpClient TCP No Delay",
            description = "Whether TCP_NODELAY is enabled."
    )
    boolean tcp_no_delay() default true;

    @AttributeDefinition(
            name = "Debug Request",
            description = "Debug for detecting any issues with the request execution. Please keep it disabled on production systems."
    )
    boolean debug_request();
}
