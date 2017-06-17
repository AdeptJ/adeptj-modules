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
import com.adeptj.modules.commons.ds.api.DataSourceProvider;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Properties;

import static com.adeptj.modules.commons.ds.DataSourceConstants.AUTO_COMMIT;
import static com.adeptj.modules.commons.ds.DataSourceConstants.CONN_TIMEOUT;
import static com.adeptj.modules.commons.ds.DataSourceConstants.DRIVER_CLASS_NAME;
import static com.adeptj.modules.commons.ds.DataSourceConstants.IDLE_TIMEOUT;
import static com.adeptj.modules.commons.ds.DataSourceConstants.JDBC_URL;
import static com.adeptj.modules.commons.ds.DataSourceConstants.MAX_LIFETIME;
import static com.adeptj.modules.commons.ds.DataSourceConstants.MAX_POOL_SIZE;
import static com.adeptj.modules.commons.ds.DataSourceConstants.MIN_IDLE;
import static com.adeptj.modules.commons.ds.DataSourceConstants.POOL_NAME;
import static com.adeptj.modules.commons.ds.DataSourceConstants.PWD;
import static com.adeptj.modules.commons.ds.DataSourceConstants.USERNAME;

/**
 * JDBC DataSource implementation HikariDataSource is being configured and returned to the callers.
 *
 * NOTE: ConfigurationPolicy.REQUIRE makes sure that this component will only be active when the configurations are provided.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = DataSourceConfig.class)
@Component(property = { "osgi.ds.provider=AdeptJ HikariDataSource Provider" }, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class HikariDataSourceProvider implements DataSourceProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(HikariDataSourceProvider.class);

    private HikariDataSource dataSource;

    @Override
    public DataSource getDataSource() {
        return this.dataSource;
    }

    // LifeCycle Methods

    @Activate
    protected void activate(DataSourceConfig config) {
        try {
            Properties properties = new Properties();
            properties.put(POOL_NAME, config.poolName());
            properties.put(JDBC_URL, config.jdbcUrl());
            properties.put(DRIVER_CLASS_NAME, config.driverClassName());
            properties.put(USERNAME, config.username());
            properties.put(PWD, config.password());
            properties.put(AUTO_COMMIT, config.autoCommit());
            properties.put(CONN_TIMEOUT, config.connectionTimeout());
            properties.put(IDLE_TIMEOUT, config.idleTimeout());
            properties.put(MAX_LIFETIME, config.maxLifetime());
            properties.put(MIN_IDLE, config.minimumIdle());
            properties.put(MAX_POOL_SIZE, config.maximumPoolSize());
            LOGGER.info("Initializing JDBC Connection Pool: [{}]", config.poolName());
            this.dataSource = new HikariDataSource(new HikariConfig(properties));
        } catch (Exception ex) { // NOSONAR
            LOGGER.error("Exception while creating HikariDataSource!!", ex);
        }
    }

    @Deactivate
    protected void deactivate() {
        this.dataSource.close();
    }
}
