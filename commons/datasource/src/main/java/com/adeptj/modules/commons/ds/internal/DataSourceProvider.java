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
package com.adeptj.modules.commons.ds.internal;

import com.adeptj.modules.commons.ds.DataSourceConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;

/**
 * DataSourceProvider.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = DataSourceConfig.class)
@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class DataSourceProvider implements DataSourceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceProvider.class);

    private HikariDataSource dataSource;

    @Override
    public DataSource createDataSource(Properties props) throws SQLException {
        return this.dataSource;
    }

    @Override
    public ConnectionPoolDataSource createConnectionPoolDataSource(Properties props) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public XADataSource createXADataSource(Properties props) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public Driver createDriver(Properties props) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Activate
    protected void activate(DataSourceConfig config) {
        Properties properties = new Properties();
        properties.put("poolName", config.poolName());
        properties.put("jdbcUrl", config.jdbcUrl());
        properties.put("driverClassName", config.driverClassName());
        properties.put("username", config.username());
        properties.put("password", config.password());
        properties.put("autoCommit", config.autoCommit());
        properties.put("connectionTimeout", config.connectionTimeout());
        properties.put("idleTimeout", config.idleTimeout());
        properties.put("maxLifetime", config.maxLifetime());
        properties.put("minimumIdle", config.minimumIdle());
        properties.put("maximumPoolSize", config.maximumPoolSize());
        LOGGER.info("Initializing JDBC Connection Pool: [{}]", config.poolName());
        this.dataSource = new HikariDataSource(new HikariConfig(properties));
    }

    @Deactivate
    protected void deactivate() {
        this.dataSource.close();
    }

}
