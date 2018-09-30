/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://www.adeptj.com)                               #
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

package com.adeptj.modules.commons.jdbc;

/**
 * Constants for {@link com.zaxxer.hikari.HikariDataSource} properties.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
final class DataSourceConstants {

    // Class just declaring constants, no instances required.
    private DataSourceConstants() {
    }

    static final String POOL_NAME = "JDBC Pool Name";

    static final String JDBC_URL = "JDBC URL";

    static final String DRIVER_CLASS_NAME = "JDBC Driver Class Name";

    static final String USERNAME = "JDBC Username";

    static final String PWD = "JDBC Password";

    static final String AUTO_COMMIT = "JDBC AutoCommit";

    static final String CONN_TIMEOUT = "JDBC Connection Timeout";

    static final String IDLE_TIMEOUT = "JDBC Idle Timeout";

    static final String MAX_LIFETIME = "JDBC Max Lifetime";

    static final String MIN_IDLE = "JDBC Minimum Idle";

    static final String MAX_POOL_SIZE = "JDBC Maximum Pool Size";

    static final String DATASOURCE_PROPS = "JDBC DataSource Properties";
}
