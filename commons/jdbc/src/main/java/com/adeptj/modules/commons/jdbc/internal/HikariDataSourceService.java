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
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

import static com.adeptj.modules.commons.jdbc.internal.DataSourceConstants.AUTO_COMMIT;
import static com.adeptj.modules.commons.jdbc.internal.DataSourceConstants.CONN_TIMEOUT;
import static com.adeptj.modules.commons.jdbc.internal.DataSourceConstants.DATASOURCE_PROPS;
import static com.adeptj.modules.commons.jdbc.internal.DataSourceConstants.DRIVER_CLASS_NAME;
import static com.adeptj.modules.commons.jdbc.internal.DataSourceConstants.IDLE_TIMEOUT;
import static com.adeptj.modules.commons.jdbc.internal.DataSourceConstants.JDBC_URL;
import static com.adeptj.modules.commons.jdbc.internal.DataSourceConstants.MAX_LIFETIME;
import static com.adeptj.modules.commons.jdbc.internal.DataSourceConstants.MAX_POOL_SIZE;
import static com.adeptj.modules.commons.jdbc.internal.DataSourceConstants.MIN_IDLE;
import static com.adeptj.modules.commons.jdbc.internal.DataSourceConstants.POOL_NAME;
import static com.adeptj.modules.commons.jdbc.internal.DataSourceConstants.PWD;
import static com.adeptj.modules.commons.jdbc.internal.DataSourceConstants.USERNAME;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.metatype.annotations.AttributeType.PASSWORD;

/**
 * Provides {@link HikariDataSource} as the JDBC {@link DataSource} implementation.
 * <p>
 * The {@link HikariDataSource} is configured by the {@link HikariDataSourceService}.
 * <p>
 *
 * @author Rakesh Kumar, AdeptJ
 */
@Designate(ocd = HikariDataSourceService.DataSourceConfig.class)
@Component(service = DataSourceService.class, configurationPolicy = REQUIRE)
public class HikariDataSourceService implements DataSourceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String EQ = "=";

    private final HikariDataSource hikariDataSource;

    /**
     * Initialize the {@link HikariDataSource} using the required configurations.
     *
     * @param config the Hikari {@link DataSourceConfig}
     * @throws DataSourceConfigurationException so that component activation fails and SCR ignores the service.
     */
    @Activate
    public HikariDataSourceService(@NotNull DataSourceConfig config) {
        Validate.isTrue(StringUtils.isNotEmpty(config.pool_name()), "JDBC Pool Name can't be null!!");
        try {
            HikariConfig hikariConfig = this.getHikariConfig(config);
            // Extra DataSource properties are in [key=value] format.
            Stream.of(config.datasource_properties())
                    .filter(StringUtils::isNotEmpty)
                    .map(row -> StringUtils.split(row, EQ))
                    .filter(parts -> ArrayUtils.getLength(parts) == 2)
                    .forEach(parts -> hikariConfig.addDataSourceProperty(parts[0].trim(), parts[1].trim()));
            this.hikariDataSource = new HikariDataSource(hikariConfig);
            LOGGER.info("HikariDataSource: [{}] initialized!!", config.pool_name());
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
            throw new DataSourceConfigurationException(ex);
        }
    }

    @NotNull
    private HikariConfig getHikariConfig(@NotNull DataSourceConfig config) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName(config.pool_name());
        hikariConfig.setJdbcUrl(config.jdbc_url());
        hikariConfig.setDriverClassName(config.driver_class_name());
        hikariConfig.setUsername(config.db_username());
        hikariConfig.setPassword(config.db_password());
        hikariConfig.setAutoCommit(config.auto_commit());
        hikariConfig.setConnectionTimeout(config.connection_timeout());
        hikariConfig.setIdleTimeout(config.idle_timeout());
        hikariConfig.setMaxLifetime(config.max_lifetime());
        hikariConfig.setMinimumIdle(config.minimum_idle());
        hikariConfig.setMaximumPoolSize(config.maximum_pool_size());
        return hikariConfig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull DataSource getDataSource() {
        return new DataSourceWrapper(this.hikariDataSource);
    }

    // <<----------------------------------------- OSGi Internal  ------------------------------------------>>

    /**
     * Close the {@link HikariDataSource}.
     *
     * @param config the Hikari {@link DataSourceConfig}
     */
    @Deactivate
    protected void stop(@NotNull DataSourceConfig config) {
        try {
            this.hikariDataSource.close();
            LOGGER.info("HikariDataSource: [{}] closed!!", config.pool_name());
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * HikariDataSource configuration, few configurations defaults to MySQL DB.
     *
     * @author Rakesh.Kumar, AdeptJ
     */
    @ObjectClassDefinition(
            name = "AdeptJ JDBC DataSource Configuration",
            description = "Configuration for JDBC DataSource(HikariDataSource)."
    )
    public @interface DataSourceConfig {

        long DEFAULT_CONN_TIMEOUT = 30000L;

        long DEFAULT_IDLE_TIMEOUT = 600000L;

        long DEFAULT_MAX_LIFETIME = 1800000L;

        int DEFAULT_MIN_IDLE = 8;

        int DEFAULT_MAX_POOL_SIZE = 8;

        String DEFAULT_JDBC_URL = "jdbc:mysql://localhost:3306/db";

        String JDBC_DRIVER_CLASSNAME = "com.mysql.cj.jdbc.Driver";

        String JDBC_USER = "root";

        boolean JDBC_AUTO_COMMIT = false;

        @AttributeDefinition(name = POOL_NAME, description = "DataSource Pool Name")
        String pool_name();

        @AttributeDefinition(name = JDBC_URL, description = "JDBC URL of target database")
        String jdbc_url() default DEFAULT_JDBC_URL;

        @AttributeDefinition(name = DRIVER_CLASS_NAME, description = "JDBC Driver FQCN")
        String driver_class_name() default JDBC_DRIVER_CLASSNAME;

        @AttributeDefinition(name = USERNAME, description = "DB authentication username")
        String db_username() default JDBC_USER;

        @AttributeDefinition(
                name = PWD,
                description = "DB authentication password",
                type = PASSWORD
        )
        String db_password();

        @AttributeDefinition(name = AUTO_COMMIT, description = "JDBC auto-commit behavior of connections")
        boolean auto_commit() default JDBC_AUTO_COMMIT;

        @AttributeDefinition(
                name = CONN_TIMEOUT,
                description = "Maximum number of milliseconds that a client will wait for a connection from the pool"
        )
        long connection_timeout() default DEFAULT_CONN_TIMEOUT;

        @AttributeDefinition(
                name = IDLE_TIMEOUT,
                description = "Maximum amount of time that a connection is allowed to sit idle in the pool"
        )
        long idle_timeout() default DEFAULT_IDLE_TIMEOUT;

        @AttributeDefinition(name = MAX_LIFETIME, description = "Maximum lifetime of a connection in the pool")
        long max_lifetime() default DEFAULT_MAX_LIFETIME;

        // Configure HikariDataSource as a fixed size pool.
        @AttributeDefinition(
                name = MIN_IDLE,
                description = "Minimum number of idle connections that HikariCP tries to maintain in the pool"
        )
        int minimum_idle() default DEFAULT_MIN_IDLE;

        @AttributeDefinition(
                name = MAX_POOL_SIZE,
                description = "Maximum size that the pool is allowed to reach, including both idle and in-use connections"
        )
        int maximum_pool_size() default DEFAULT_MAX_POOL_SIZE;

        @AttributeDefinition(
                name = DATASOURCE_PROPS,
                description = "Underlying JDBC data source specific properties, in key=value format, defaults are only for MySQL!"
        )
        String[] datasource_properties() default {
                "useSSL=false",
                "allowPublicKeyRetrieval=true",
                "prepStmtCacheSize=250",
                "prepStmtCacheSqlLimit=2048",
                "useServerPrepStmts=true",
                "cachePrepStmts=true",
                "useLocalSessionState=true",
                "useLocalTransactionState=true",
                "rewriteBatchedStatements=true",
                "cacheResultSetMetadata=true",
                "cacheServerConfiguration=true",
                "elideSetAutoCommits=true",
                "maintainTimeStats=false",
        };
    }
}
