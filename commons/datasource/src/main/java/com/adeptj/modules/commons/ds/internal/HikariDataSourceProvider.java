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
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Dictionary;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

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
import static com.adeptj.modules.commons.ds.internal.HikariDataSourceProvider.FACTORY_NAME;
import static com.adeptj.modules.commons.ds.internal.HikariDataSourceProvider.SERVICE_PID_PROPERTY;
import static org.osgi.service.component.annotations.ConfigurationPolicy.IGNORE;

/**
 * JDBC DataSource implementation HikariDataSource is being configured and returned to the callers.
 * <p>
 * NOTE: ConfigurationPolicy.REQUIRE makes sure that this component will only be active when the configurations are provided.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = DataSourceConfig.class)
@Component(immediate = true, name = FACTORY_NAME, property = SERVICE_PID_PROPERTY, configurationPolicy = IGNORE)
public class HikariDataSourceProvider implements DataSourceProvider, ManagedServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(HikariDataSourceProvider.class);

    static final String FACTORY_NAME = "com.adeptj.modules.commons.ds.DataSourceProvider.factory";

    static final String SERVICE_PID_PROPERTY = "service.pid=com.adeptj.modules.commons.ds.DataSourceProvider.factory";

    private Map<String, HikariDataSource> dataSources = new ConcurrentHashMap<>();

    private Map<String, String> pidVsDSNameMapping = new ConcurrentHashMap<>();

    @Override
    public DataSource getDataSource(String dataSourceName) {
        return this.dataSources.get(dataSourceName);
    }

    @Override
    public String getName() {
        return FACTORY_NAME;
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        this.handleConfigChange(pid);
        this.createDataSource(pid, properties);
    }

    @Override
    public void deleted(String pid) {
        this.handleConfigChange(pid);
    }

    private void handleConfigChange(String pid) {
        Optional.ofNullable(this.pidVsDSNameMapping.remove(pid)).ifPresent((String dataSourceName) -> {
            LOGGER.info("Closing HikariDataSource against PoolName: [{}]", dataSourceName);
            try {
                this.dataSources.remove(dataSourceName).close();
            } catch (Exception ex) { // NOSONAR
                LOGGER.error("Exception while closing HikariDataSource!!", ex);
            }
        });
    }

    private void createDataSource(String pid, Dictionary<String, ?> configs) {
        try {
            Properties properties = new Properties();
            String poolName = (String) configs.get(POOL_NAME);
            properties.put(POOL_NAME, poolName);
            properties.put(JDBC_URL, configs.get(JDBC_URL));
            properties.put(DRIVER_CLASS_NAME, configs.get(DRIVER_CLASS_NAME));
            properties.put(USERNAME, configs.get(USERNAME));
            properties.put(PWD, configs.get(PWD));
            properties.put(AUTO_COMMIT, configs.get(AUTO_COMMIT));
            properties.put(CONN_TIMEOUT, configs.get(CONN_TIMEOUT));
            properties.put(IDLE_TIMEOUT, configs.get(IDLE_TIMEOUT));
            properties.put(MAX_LIFETIME, configs.get(MAX_LIFETIME));
            properties.put(MIN_IDLE, configs.get(MIN_IDLE));
            properties.put(MAX_POOL_SIZE, configs.get(MAX_POOL_SIZE));
            LOGGER.info("Initializing JDBC ConnectionPool: [{}]", poolName);
            this.dataSources.put(poolName, new HikariDataSource(new HikariConfig(properties)));
            this.pidVsDSNameMapping.put(pid, poolName);
        } catch (Exception ex) { // NOSONAR
            LOGGER.error("Exception while creating HikariDataSource!!", ex);
        }
    }
}
