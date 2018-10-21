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

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

import static com.adeptj.modules.commons.jdbc.internal.DataSourceProvider.PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * A factory for creating {@link com.zaxxer.hikari.HikariDataSource} instances.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = DataSourceConfig.class, factory = true)
@Component(service = DataSourceProvider.class, name = PID, configurationPolicy = REQUIRE)
public class DataSourceProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    static final String PID = "com.adeptj.modules.commons.jdbc.DataSource.factory";

    private HikariDataSource dataSource;

    HikariDataSource getDataSource() {
        return this.dataSource;
    }

    // <----------------------------------------------- OSGi INTERNAL ------------------------------------------------->

    /**
     * Initialize the {@link HikariDataSource} using the configuration passed.
     *
     * @param config the Hikari {@link DataSourceConfig}
     * @throws DataSourceConfigurationException so that component activation fails and SCR ignores the service.
     */
    @Activate
    protected void start(DataSourceConfig config) {
        try {
            String poolName = config.poolName();
            Validate.isTrue(StringUtils.isNotEmpty(poolName), "JDBC Pool Name can't be blank!!");
            this.dataSource = DataSources.newDataSource(config);
            LOGGER.info("HikariDataSource: [{}] initialized!!", poolName);
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
            throw new DataSourceConfigurationException(ex);
        }
    }

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
            this.dataSource = null;
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
