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

package com.adeptj.modules.commons.ds.internal;

import com.adeptj.modules.commons.ds.DataSourceConfig;
import com.adeptj.modules.commons.ds.DataSources;
import com.adeptj.modules.commons.utils.Loggers;
import com.zaxxer.hikari.HikariDataSource;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A factory for closing all the remaining opened {@link com.zaxxer.hikari.HikariDataSource}
 * instances when this bundle stops.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(service = DataSourceManager.class)
public class DataSourceManager {

    private static final Logger LOGGER = Loggers.get(MethodHandles.lookup().lookupClass());

    private ConcurrentMap<String, HikariDataSource> hikariDataSources = new ConcurrentHashMap<>();

    void createDataSource(DataSourceConfig config) {
        this.hikariDataSources.put(config.poolName(), DataSources.createDataSource(config));
        LOGGER.info("HikariDataSource: [{}] initialized!!", config.poolName());
    }

    HikariDataSource getDataSource(String dataSourceName) {
        return this.hikariDataSources.get(dataSourceName);
    }

    void closeDataSource(String dataSourceName) {
        Optional.ofNullable(this.hikariDataSources.remove(dataSourceName))
                .ifPresent(dataSource -> {
                    try {
                        dataSource.close();
                        LOGGER.info("HikariDataSource: [{}] closed!!", dataSourceName);
                    } catch (Exception ex) { // NOSONAR
                        LOGGER.error("Exception while closing HikariDataSource!!", ex);
                    }
                });
    }
}
