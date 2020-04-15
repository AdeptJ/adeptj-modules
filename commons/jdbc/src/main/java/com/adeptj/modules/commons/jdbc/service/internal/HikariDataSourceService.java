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

import com.adeptj.modules.commons.jdbc.exception.DataSourceConfigurationException;
import com.adeptj.modules.commons.jdbc.service.DataSourceService;
import com.adeptj.modules.commons.jdbc.util.DataSources;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.nullness.qual.NonNull;
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
 * Provides {@link com.zaxxer.hikari.HikariDataSource} as the JDBC {@link DataSource} implementation.
 * <p>
 * The {@link com.zaxxer.hikari.HikariDataSource} is configured by the {@link HikariDataSourceService}.
 * <p>
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = DataSourceConfig.class)
@Component(service = DataSourceService.class, configurationPolicy = REQUIRE)
public class HikariDataSourceService implements DataSourceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final HikariDataSource dataSource;

    /**
     * Initialize the {@link HikariDataSource} using the configuration passed.
     *
     * @param config the Hikari {@link DataSourceConfig}
     * @throws DataSourceConfigurationException so that component activation fails and SCR ignores the service.
     */
    @Activate
    public HikariDataSourceService(@NonNull DataSourceConfig config) {
        try {
            Validate.isTrue(StringUtils.isNotEmpty(config.poolName()), "JDBC Pool Name can't be blank!!");
            this.dataSource = DataSources.newDataSource(config);
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
    public @NonNull DataSource getDataSource() {
        return new DataSourceWrapper(this.dataSource);
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
