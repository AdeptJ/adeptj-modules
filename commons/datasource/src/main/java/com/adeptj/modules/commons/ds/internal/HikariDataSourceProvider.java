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
import com.adeptj.modules.commons.ds.api.DataSourceProvider;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

import javax.sql.DataSource;
import java.util.Dictionary;

import static com.adeptj.modules.commons.ds.internal.HikariDataSourceProvider.COMPONENT_NAME;
import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.IGNORE;

/**
 * JDBC DataSource implementation HikariDataSource is being configured and returned to the callers.
 * <p>
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = DataSourceConfig.class, factory = true)
@Component(
        immediate = true,
        name = COMPONENT_NAME,
        property = SERVICE_PID + "=" + COMPONENT_NAME,
        configurationPolicy = IGNORE
)
public class HikariDataSourceProvider implements DataSourceProvider, ManagedServiceFactory {

    static final String COMPONENT_NAME = "com.adeptj.modules.commons.ds.DataSourceProvider.factory";

    private static final String FACTORY_NAME = "AdeptJ JDBC DataSource Factory";

    @Override
    public DataSource getDataSource(String dataSourceName) {
        return DataSources.INSTANCE.getDataSource(dataSourceName);
    }

    // -------------------- INTERNAL --------------------
    // Methods implemented from ManagedServiceFactory

    @Override
    public String getName() {
        return FACTORY_NAME;
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) {
        DataSources.INSTANCE.handleConfigChange(pid);
        DataSources.INSTANCE.createDataSource(pid, properties);
    }

    @Override
    public void deleted(String pid) {
        DataSources.INSTANCE.handleConfigChange(pid);
    }
}
