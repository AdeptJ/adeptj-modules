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

package com.adeptj.modules.commons.ds;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * HikariDataSorce configurations, few configurations defaults to MySQL DB.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ObjectClassDefinition(
        name = "AdeptJ JDBC DataSource Configurations",
        description = "Configurations for JDBC DataSource(HikariDataSorce)."
)
public @interface DataSourceConfig {

    long DEFAULT_CONN_TIMEOUT = 30000L;

    long DEFAULT_IDLE_TIMEOUT = 600000L;

    long DEFAULT_MAX_LIFETIME = 1800000L;

    int DEFAULT_MIN_IDLE = 32;

    int DEFAULT_MAX_POOL_SIZE = 32;

    String DEFAULT_JDBC_URL = "jdbc:mysql://localhost:3306/db?useSSL=false&nullNamePatternMatchesAll=true";

    String DEFAULT_DRIVER_CLASSNAME = "com.mysql.cj.jdbc.Driver";

    String DEFAULT_USER = "root";

    boolean DEFAULT_AUTO_COMMIT = true;

    @AttributeDefinition(name = "poolName", description = "DataSource Pool Name")
    String poolName();

    @AttributeDefinition(name = "jdbcUrl", description = "JDBC URL for target Database")
    String jdbcUrl() default DEFAULT_JDBC_URL;

    @AttributeDefinition(name = "driverClassName", description = "JDBC driver FQCN")
    String driverClassName() default DEFAULT_DRIVER_CLASSNAME;

    @AttributeDefinition(name = "username", description = "JDBC default authentication username")
    String username() default DEFAULT_USER;

    @AttributeDefinition(
            name = "password",
            description = "JDBC default authentication password",
            type = AttributeType.PASSWORD
    )
    String password();

    @AttributeDefinition(name = "autoCommit", description = "JDBC auto-commit behavior of connections")
    boolean autoCommit() default DEFAULT_AUTO_COMMIT;

    @AttributeDefinition(
            name = "connectionTimeout",
            description = "Maximum number of milliseconds that a client will wait for a connection from the pool"
    )
    long connectionTimeout() default DEFAULT_CONN_TIMEOUT; // 30 Seconds

    @AttributeDefinition(
            name = "idleTimeout",
            description = "Maximum amount of time that a connection is allowed to sit idle in the pool"
    )
    long idleTimeout() default DEFAULT_IDLE_TIMEOUT; // 10 Minutes

    @AttributeDefinition(name = "maxLifetime", description = "Maximum lifetime of a connection in the pool")
    long maxLifetime() default DEFAULT_MAX_LIFETIME; // 30 Minutes

    // Configure HikariDataSource as a fixed size pool.
    @AttributeDefinition(
            name = "minimumIdle",
            description = "Minimum number of idle connections that HikariCP tries to maintain in the pool")
    int minimumIdle() default DEFAULT_MIN_IDLE; // 32 Connections;

    @AttributeDefinition(
            name = "maximumPoolSize",
            description = "Maximum size that the pool is allowed to reach, including both idle and in-use connections"
    )
    int maximumPoolSize() default DEFAULT_MAX_POOL_SIZE; // 32 Connections;
}
