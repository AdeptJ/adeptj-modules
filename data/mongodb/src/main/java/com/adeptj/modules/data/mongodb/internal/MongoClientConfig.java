package com.adeptj.modules.data.mongodb.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import static org.osgi.service.metatype.annotations.AttributeType.PASSWORD;

@ObjectClassDefinition(
        name = "AdeptJ MongoClient Configuration",
        description = "Configuration for AdeptJ MongoClient"
)
public @interface MongoClientConfig {

    @AttributeDefinition(
            name = "Server Addresses",
            description = "MongoDB server addresses in host:port format",
            cardinality = 50
    )
    String[] server_addresses() default {
            "127.0.0.1:27017",
    };

    @AttributeDefinition(name = "Connection Auth Username", description = "Connection auth username")
    String auth_username();

    @AttributeDefinition(name = "Connection Auth Password", description = "Connection auth password", type = PASSWORD)
    String auth_password();

    @AttributeDefinition(name = "Connection Auth Database", description = "Connection auth database")
    String auth_database();

    @AttributeDefinition(name = "TLS Enabled", description = "Whether to use SSL/TLS connection")
    boolean tls_enabled();
}
