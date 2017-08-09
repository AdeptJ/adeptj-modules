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

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility for managing {@link HikariDataSource} instances.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public enum DataSources {

    INSTANCE;

    private Map<String, HikariDataSource> dataSources = new ConcurrentHashMap<>();

    public void addDataSource(String dataSourceName, HikariDataSource dataSource) {
        this.dataSources.put(dataSourceName, dataSource);
    }

    public DataSource getDataSource(String dataSourceName) {
        return this.dataSources.get(dataSourceName);
    }

    public void closeDataSource(String dataSourceName) {
        if (this.dataSources.containsKey(dataSourceName)) {
            this.dataSources.remove(dataSourceName).close();
        }
    }

    public void closeAll() {
       this.dataSources.forEach((dataSourceName, dataSource) -> dataSource.close());
    }
}
