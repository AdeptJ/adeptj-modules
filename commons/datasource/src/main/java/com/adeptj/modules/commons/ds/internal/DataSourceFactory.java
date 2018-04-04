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
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import static com.adeptj.modules.commons.ds.internal.DataSourceFactory.COMPONENT_NAME;
import static com.adeptj.modules.commons.utils.Constants.EQ;
import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * A factory for creating {@link com.zaxxer.hikari.HikariDataSource} instances.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = DataSourceConfig.class, factory = true)
@Component(
        immediate = true,
        name = COMPONENT_NAME,
        property = SERVICE_PID + EQ + COMPONENT_NAME,
        configurationPolicy = REQUIRE
)
public class DataSourceFactory {

    static final String COMPONENT_NAME = "com.adeptj.modules.commons.ds.DataSourceProvider.factory";

    @Reference
    private DataSourceManager dataSourceManager;

    @Activate
    protected void start(DataSourceConfig config) {
        this.dataSourceManager.createDataSource(config);
    }

    @Deactivate
    protected void stop(DataSourceConfig config) {
        this.dataSourceManager.closeDataSource(config.poolName());
    }
}
