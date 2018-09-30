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

import com.adeptj.modules.commons.jdbc.DataSourceNotConfiguredException;
import com.adeptj.modules.commons.jdbc.DataSourceService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.sql.DataSource;
import java.util.Optional;

/**
 * Provides {@link com.zaxxer.hikari.HikariDataSource} as the JDBC {@link DataSource} implementation.
 * <p>
 * The {@link com.zaxxer.hikari.HikariDataSource} is configured by the {@link DataSourceManager}.
 * <p>
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(service = DataSourceService.class)
public class HikariDataSourceService implements DataSourceService {

    private static final String JDBC_DS_NOT_CONFIGURED_MSG = "HikariDataSource: [%s] is not configured!!";

    /**
     * Statically referenced OSGi service.
     */
    @Reference
    private DataSourceManager dataSourceManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSource getDataSource(String name) {
        Validate.isTrue(StringUtils.isNotEmpty(name), "name can't be blank!!");
        return Optional.ofNullable(this.dataSourceManager.getDataSource(name))
                .orElseThrow(() -> new DataSourceNotConfiguredException(String.format(JDBC_DS_NOT_CONFIGURED_MSG, name)));
    }
}
