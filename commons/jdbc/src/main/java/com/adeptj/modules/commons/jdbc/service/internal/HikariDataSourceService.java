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

package com.adeptj.modules.commons.jdbc.service.internal;

import com.adeptj.modules.commons.jdbc.service.DataSourceService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.invoke.MethodHandles;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * Provides {@link HikariDataSource} as the JDBC {@link DataSource} implementation.
 * <p>
 * The {@link HikariDataSource} is configured by the {@link HikariDataSourceService}.
 * <p>
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = DataSourceConfig.class)
@Component(service = DataSourceService.class, configurationPolicy = REQUIRE)
public class HikariDataSourceService implements DataSourceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String EQ = "=";

    private final HikariDataSource dataSource;

    /**
     * Initialize the {@link HikariDataSource} using the required configurations.
     *
     * @param config the Hikari {@link DataSourceConfig}
     * @throws DataSourceConfigurationException so that component activation fails and SCR ignores the service.
     */
    @Activate
    public HikariDataSourceService(DataSourceConfig config) {
        try {
            this.dataSource = this.createHikariDataSource(config);
            LOGGER.info("HikariDataSource: [{}] initialized!!", config.poolName());
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
            throw new DataSourceConfigurationException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull DataSource getDataSource() {
        return new DataSourceWrapper(this.dataSource);
    }

    @NotNull
    private HikariDataSource createHikariDataSource(DataSourceConfig config) {
        Validate.isTrue(StringUtils.isNotEmpty(config.poolName()), "JDBC Pool Name can't be null!!");
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName(config.poolName());
        hikariConfig.setJdbcUrl(config.jdbcUrl());
        hikariConfig.setDriverClassName(config.driverClassName());
        hikariConfig.setUsername(config.username());
        hikariConfig.setPassword(config.password());
        hikariConfig.setAutoCommit(config.autoCommit());
        hikariConfig.setConnectionTimeout(config.connectionTimeout());
        hikariConfig.setIdleTimeout(config.idleTimeout());
        hikariConfig.setMaxLifetime(config.maxLifetime());
        hikariConfig.setMinimumIdle(config.minimumIdle());
        hikariConfig.setMaximumPoolSize(config.maximumPoolSize());
        for (String row : config.dataSourceProperties()) {
            String[] mapping = StringUtils.split(row, EQ);
            if (ArrayUtils.getLength(mapping) == 2) {
                hikariConfig.addDataSourceProperty(mapping[0].trim(), mapping[1].trim());
            }
        }
        return new HikariDataSource(hikariConfig);
    }

    // <<------------------------------------- OSGi Internal  -------------------------------------->>

    /**
     * Close the {@link HikariDataSource}.
     *
     * @param config the Hikari {@link DataSourceConfig}
     */
    @Deactivate
    protected void stop(DataSourceConfig config) {
        try {
            this.dataSource.close();
            LOGGER.info("HikariDataSource: [{}] closed!!", config.poolName());
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
