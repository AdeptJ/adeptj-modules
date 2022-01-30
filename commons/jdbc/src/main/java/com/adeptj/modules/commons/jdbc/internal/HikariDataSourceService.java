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

package com.adeptj.modules.commons.jdbc.internal;

import com.adeptj.modules.commons.jdbc.DataSourceService;
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
import java.util.stream.Stream;

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
     * @param dsc the Hikari {@link DataSourceConfig}
     * @throws DataSourceConfigurationException so that component activation fails and SCR ignores the service.
     */
    @Activate
    public HikariDataSourceService(@NotNull DataSourceConfig dsc) {
        Validate.isTrue(StringUtils.isNotEmpty(dsc.pool_name()), "JDBC Pool Name can't be null!!");
        try {
            HikariConfig config = new HikariConfig();
            config.setPoolName(dsc.pool_name());
            config.setJdbcUrl(dsc.jdbc_url());
            config.setDriverClassName(dsc.driver_class_name());
            config.setUsername(dsc.db_username());
            config.setPassword(dsc.db_password());
            config.setAutoCommit(dsc.auto_commit());
            config.setConnectionTimeout(dsc.connection_timeout());
            config.setIdleTimeout(dsc.idle_timeout());
            config.setMaxLifetime(dsc.max_lifetime());
            config.setMinimumIdle(dsc.minimum_idle());
            config.setMaximumPoolSize(dsc.maximum_pool_size());
            // Extra DataSource properties are in [key=value] format.
            Stream.of(dsc.datasource_properties())
                    .filter(StringUtils::isNotEmpty)
                    .map(row -> StringUtils.split(row, EQ))
                    .filter(parts -> ArrayUtils.getLength(parts) == 2)
                    .forEach(parts -> config.addDataSourceProperty(parts[0].trim(), parts[1].trim()));
            this.dataSource = new HikariDataSource(config);
            LOGGER.info("HikariDataSource: [{}] initialized!!", dsc.pool_name());
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

    // <<----------------------------------------- OSGi Internal  ------------------------------------------>>

    /**
     * Close the {@link HikariDataSource}.
     *
     * @param config the Hikari {@link DataSourceConfig}
     */
    @Deactivate
    protected void stop(@NotNull DataSourceConfig config) {
        try {
            this.dataSource.close();
            LOGGER.info("HikariDataSource: [{}] closed!!", config.pool_name());
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
