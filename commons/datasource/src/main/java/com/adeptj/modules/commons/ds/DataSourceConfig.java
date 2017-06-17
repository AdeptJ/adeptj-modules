/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://adeptj.com)                               #
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
package com.adeptj.modules.commons.ds;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * DataSourceConfig.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ObjectClassDefinition(name = "AdeptJ Modules JDBC DataSource Provider Configuration",
        description = "The configuration for the AdeptJ Modules JDBC DataSource Provider.")
public @interface DataSourceConfig {

    @AttributeDefinition(name = "poolName", description = "DataSource Pool Name")
    String poolName();

    @AttributeDefinition(name = "jdbcUrl", description = "JDBC URL for target Database")
    String jdbcUrl() default "jdbc:mysql://localhost:3306/";

    @AttributeDefinition(name = "driverClassName", description = "JDBC driver FQCN")
    String driverClassName() default "com.mysql.jdbc.Driver";

    @AttributeDefinition(name = "username", description = "JDBC default authentication username")
    String username() default "root";

    @AttributeDefinition(name = "password", description = "JDBC default authentication password", type = AttributeType.PASSWORD)
    String password();

    @AttributeDefinition(name = "autoCommit", description = "JDBC auto-commit behavior of connections")
    boolean autoCommit() default true;

    @AttributeDefinition(name = "connectionTimeout", description = "Maximum number of milliseconds that a client will wait for a connection from the pool")
    long connectionTimeout() default 60000; // 60 Seconds

    @AttributeDefinition(name = "idleTimeout", description = "Maximum amount of time that a connection is allowed to sit idle in the pool")
    long idleTimeout() default 600000; // 10 Minutes

    @AttributeDefinition(name = "maxLifetime", description = "Maximum lifetime of a connection in the pool")
    long maxLifetime() default 1800000; // 30 Minutes

    @AttributeDefinition(name = "minimumIdle", description = "Minimum number of idle connections that HikariCP tries to maintain in the pool")
    int minimumIdle() default 32; // 32 Connections;

    @AttributeDefinition(name = "maximumPoolSize", description = "Maximum size that the pool is allowed to reach, including both idle and in-use connections")
    int maximumPoolSize() default 32; // 32 Connections;
}
