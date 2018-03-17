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

package com.adeptj.modules.data.mongo.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

/**
 * OSGI mongo db connection configuration definition.
 *
 * @author prince.arora, AdeptJ.
 */
@ObjectClassDefinition(
        name = "AdeptJ MongoDB Factory Configurations",
        description = "MongoDB Configuration"
)
public @interface MongoConfiguration {

    @AttributeDefinition(
            name = "Unit Name",
            description = "Note: Must be unique"
    )
    String unitName();

    @AttributeDefinition(
            name = "Host",
            description = "Host name or ip for Mongo DB connection"
    )
    String hostName() default "127.0.0.1";

    @AttributeDefinition(
            name = "Port",
            description = "Mongo DB connection port"
    )
    int port() default 27017;

    @AttributeDefinition(
            name = "Database Name",
            description = "MongoDB database name"
    )
    String dbName();

    @AttributeDefinition(
            name = "Username",
            description = "MongoDB authentication username"
    )
    String username();

    @AttributeDefinition(
            name = "Password",
            description = "MongoDB authentication password"
    )
    String password();

    @AttributeDefinition(
            name = "Package To Map",
            description = "MongoDB Collections classes package to Map"
    )
    String mappablePackage();

    @AttributeDefinition(
            name = "ReadPreference",
            description = "ReadPreference for transactions.",
            options = {
                    @Option(label = "PRIMARY", value = "PRIMARY"),
                    @Option(label = "SECONDARY", value = "SECONDARY"),
                    @Option(label = "SECONDARY_PREFERRED", value = "SECONDARY_PREFERRED"),
                    @Option(label = "PRIMARY_PREFERRED", value = "PRIMARY_PREFERRED"),
                    @Option(label = "NEAREST", value = "NEAREST")
            }
    )
    String readPreference();

    @AttributeDefinition(
            name = "WriteConcern",
            description = "WriteConcern for transactions.",
            options = {
                    @Option(label = "ACKNOWLEDGED", value = "ACKNOWLEDGED"),
                    @Option(label = "JOURNALED", value = "JOURNALED"),
                    @Option(label = "MAJORITY", value = "MAJORITY"),
                    @Option(label = "UNACKNOWLEDGED", value = "UNACKNOWLEDGED")
            }
    )
    String writeConcern();

    @AttributeDefinition(
            name = "Max Connections PerHost",
            description = "Sets the maximum number of connections per host."
    )
    int maxConnectionsPerHost() default 100;

    @AttributeDefinition(
            name = "Server Selection Timeout",
            description = "Sets the server selection timeout in milliseconds, " +
                    "which defines how long the driver will wait for server selection to succeed before throwing an exception."
    )
    int serverSelectionTimeout() default 30000;

    @AttributeDefinition(
            name = "Max Wait Time",
            description = "Sets the maximum time that a thread will block waiting for a connection."
    )
    int maxWaitTime() default 120000;

    @AttributeDefinition(
            name = "Max Connection IdleTime",
            description = "Sets the maximum idle time for a pooled connection.(the maximum idle time, in milliseconds, " +
                    "which must be >= 0. A zero value indicates no limit to the life time.)"
    )
    int maxConnectionIdleTime() default 100000;

    @AttributeDefinition(
            name = "ssl Enabled",
            description = "Sets whether to use SSL."
    )
    boolean sslEnabled();

    @AttributeDefinition(
            name = "ConnectTimeout",
            description = "Sets the connection timeout.(the connection timeout, in milliseconds, which must be > 0)"
    )
    int connectTimeout() default 30000;

    @AttributeDefinition(
            name = "Max Connection LifeTime",
            description = "Sets the maximum life time for a pooled connection.(he maximum life time, in milliseconds, " +
                    "which must be >= 0. A zero value indicates no limit to the life time.)"
    )
    int maxConnectionLifeTime() default 30000;

}