/* 
 * =============================================================================
 * 
 * Copyright (c) 2016 AdeptJ
 * Copyright (c) 2016 Rakesh Kumar <irakeshk@outlook.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * =============================================================================
 */
package com.adeptj.modularweb.datasource.core;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.metatype.annotations.Designate;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;

/**
 * DataSourceProvider.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component
@Designate(ocd = DatasourceConfig.class)
public class DataSourceProvider implements DataSourceFactory {

    @Override
    public DataSource createDataSource(Properties props) throws SQLException {
        return new HikariDataSource(new HikariConfig(props));
    }

    @Override
    public ConnectionPoolDataSource createConnectionPoolDataSource(Properties props) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public XADataSource createXADataSource(Properties props) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public Driver createDriver(Properties props) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Activate
    protected void activate(DatasourceConfig config) {
    }

}
