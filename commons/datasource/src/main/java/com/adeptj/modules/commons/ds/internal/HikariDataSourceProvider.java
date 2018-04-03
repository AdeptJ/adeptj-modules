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

import com.adeptj.modules.commons.ds.api.DataSourceProvider;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.sql.DataSource;

/**
 * JDBC DataSource implementation HikariDataSource is being configured and returned to the callers.
 * <p>
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(service = DataSourceProvider.class)
public class HikariDataSourceProvider implements DataSourceProvider {

    @Reference
    private DataSourceManager dataSourceManager;

    @Override
    public DataSource getDataSource(String dataSourceName) {
        return this.dataSourceManager.getDataSource(dataSourceName);
    }
}
