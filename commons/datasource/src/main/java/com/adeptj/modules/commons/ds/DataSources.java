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

import com.adeptj.modules.commons.utils.PropertiesUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Dictionary;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.adeptj.modules.commons.ds.DataSourceConfig.DEFAULT_CONN_TIMEOUT;
import static com.adeptj.modules.commons.ds.DataSourceConfig.DEFAULT_IDLE_TIMEOUT;
import static com.adeptj.modules.commons.ds.DataSourceConfig.DEFAULT_MAX_LIFETIME;
import static com.adeptj.modules.commons.ds.DataSourceConfig.DEFAULT_MAX_POOL_SIZE;
import static com.adeptj.modules.commons.ds.DataSourceConfig.DEFAULT_MIN_IDLE;
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
 * Utility for managing {@link HikariDataSource} instances.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public enum DataSources {

    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSources.class);

    private Map<String, String> pidVsDSNameMapping = new ConcurrentHashMap<>();

    private Map<String, HikariDataSource> dataSources = new ConcurrentHashMap<>();

    public void createDataSource(String pid, Dictionary<String, ?> configs) {
        try {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setPoolName(PropertiesUtil.toString(configs.get(POOL_NAME), null));
            hikariConfig.setJdbcUrl(PropertiesUtil.toString(configs.get(JDBC_URL), null));
            hikariConfig.setDriverClassName(PropertiesUtil.toString(configs.get(DRIVER_CLASS_NAME), null));
            hikariConfig.setUsername(PropertiesUtil.toString(configs.get(USERNAME), null));
            hikariConfig.setPassword(PropertiesUtil.toString(configs.get(PWD), null));
            hikariConfig.setAutoCommit(PropertiesUtil.toBoolean(configs.get(AUTO_COMMIT), true));
            hikariConfig.setConnectionTimeout(PropertiesUtil.toLong(configs.get(CONN_TIMEOUT), DEFAULT_CONN_TIMEOUT));
            hikariConfig.setIdleTimeout(PropertiesUtil.toLong(configs.get(IDLE_TIMEOUT), DEFAULT_IDLE_TIMEOUT));
            hikariConfig.setMaxLifetime(PropertiesUtil.toLong(configs.get(MAX_LIFETIME), DEFAULT_MAX_LIFETIME));
            hikariConfig.setMinimumIdle(PropertiesUtil.toInteger(configs.get(MIN_IDLE), DEFAULT_MIN_IDLE));
            hikariConfig.setMaximumPoolSize(PropertiesUtil.toInteger(configs.get(MAX_POOL_SIZE), DEFAULT_MAX_POOL_SIZE));
            LOGGER.info("Initializing HikariDataSource named: [{}]", hikariConfig.getPoolName());
            this.dataSources.put(hikariConfig.getPoolName(), new HikariDataSource(hikariConfig));
            this.pidVsDSNameMapping.put(pid, hikariConfig.getPoolName());
        } catch (Exception ex) { // NOSONAR
            LOGGER.error("Exception while creating HikariDataSource!!", ex);
        }
    }

    public void handleConfigChange(String pid) {
        Optional.ofNullable(this.pidVsDSNameMapping.remove(pid)).ifPresent(dataSourceName -> {
            LOGGER.info("Closing HikariDataSource named: [{}]", dataSourceName);
            this.closeDataSource(dataSourceName);
        });
    }

    public DataSource getDataSource(String dataSourceName) {
        return this.dataSources.get(dataSourceName);
    }

    public void closeDataSource(String dataSourceName) {
        Optional.ofNullable(this.dataSources.remove(dataSourceName)).ifPresent(this::closeHikariDataSource);
    }

    public void closeAll() {
        this.dataSources.forEach((dataSourceName, dataSource) -> this.closeHikariDataSource(dataSource));
    }

    private void closeHikariDataSource(HikariDataSource dataSource) {
        try {
            dataSource.close();
        } catch (Exception ex) { // NOSONAR
            LOGGER.error("Exception while closing HikariDataSource!!", ex);
        }
    }
}
