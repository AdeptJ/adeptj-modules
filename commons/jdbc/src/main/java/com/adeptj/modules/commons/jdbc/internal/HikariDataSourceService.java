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
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

/**
 * Provides {@link com.zaxxer.hikari.HikariDataSource} as the JDBC {@link DataSource} implementation.
 * <p>
 * The {@link com.zaxxer.hikari.HikariDataSource} is configured by the {@link DataSourceProvider}.
 * <p>
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(service = DataSourceService.class)
public class HikariDataSourceService implements DataSourceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String JDBC_DS_NOT_CONFIGURED_MSG = "HikariDataSource: [%s] is not configured!!";

    private List<HikariDataSource> dataSources = new CopyOnWriteArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSource getDataSource(String name) {
        Validate.isTrue(StringUtils.isNotEmpty(name), "pool name can't be blank!!");
        return this.dataSources.stream()
                .filter(ds -> StringUtils.equals(name, ds.getPoolName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format(JDBC_DS_NOT_CONFIGURED_MSG, name)));
    }

    // <----------------------------------------------- OSGi INTERNAL ------------------------------------------------->

    @Reference(service = DataSourceProvider.class, cardinality = MULTIPLE, policy = DYNAMIC)
    protected void bindDataSourceProvider(DataSourceProvider provider) {
        LOGGER.info("Adding DataSource: [{}]", provider.getDataSource().getPoolName());
        this.dataSources.add(provider.getDataSource());
    }

    protected void unbindDataSourceProvider(DataSourceProvider provider) {
        String poolName = provider.getDataSource().getPoolName();
        LOGGER.info("Removing DataSource: [{}]", poolName);
        this.dataSources.removeIf(ds -> StringUtils.equals(ds.getPoolName(), poolName));
    }
}
