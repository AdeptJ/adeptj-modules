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

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
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
            description = "Host name or ip for Mongo DB connection",
            defaultValue = "127.0.0.1"
    )
    String hostName();

    @AttributeDefinition(
            name = "Port",
            description = "Mongo DB connection port",
            defaultValue = "27017",
            type = AttributeType.INTEGER
    )
    int port();

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
            description = "Sets the maximum number of connections per host.",
            defaultValue = "100",
            type = AttributeType.INTEGER
    )
    int maxConnectionsPerHost();

    @AttributeDefinition(
            name = "Server Selection Timeout",
            description = "Sets the server selection timeout in milliseconds, which defines how long the driver will wait for server selection to succeed before throwing an exception.",
            defaultValue = "30000",
            type = AttributeType.INTEGER
    )
    int serverSelectionTimeout();

    @AttributeDefinition(
            name = "Max Wait Time",
            description = "Sets the maximum time that a thread will block waiting for a connection.",
            defaultValue = "120000",
            type = AttributeType.INTEGER
    )
    int maxWaitTime();

    @AttributeDefinition(
            name = "Max Connection IdleTime",
            description = "Sets the maximum idle time for a pooled connection.(the maximum idle time, in milliseconds, which must be >= 0. A zero value indicates no limit to the life time.)",
            defaultValue = "100000",
            type = AttributeType.INTEGER
    )
    int maxConnectionIdleTime();

    @AttributeDefinition(
            name = "ssl Enabled",
            description = "Sets whether to use SSL.",
            type = AttributeType.BOOLEAN,
            defaultValue = "false"
    )
    int sslEnabled();

    @AttributeDefinition(
            name = "ConnectTimeout",
            description = "Sets the connection timeout.(the connection timeout, in milliseconds, which must be > 0)",
            type = AttributeType.INTEGER,
            defaultValue = "30000"
    )
    int connectTimeout();

    @AttributeDefinition(
            name = "Max Connection LifeTime",
            description = "Sets the maximum life time for a pooled connection.(he maximum life time, in milliseconds, which must be >= 0. A zero value indicates no limit to the life time.)",
            type = AttributeType.INTEGER,
            defaultValue = "300000"
    )
    int maxConnectionLifeTime();

}
