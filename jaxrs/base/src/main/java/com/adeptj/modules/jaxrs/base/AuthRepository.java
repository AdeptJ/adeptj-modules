package com.adeptj.modules.jaxrs.base;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * AuthRepository for H2 DB.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@Component(immediate = true, service = AuthRepository.class)
public class AuthRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthRepository.class);

    @Reference(target = "(osgi.jdbc.driver.name=H2 JDBC Driver)")
    private DataSourceFactory dsFactory;

    private DataSource dataSource;

    public void saveJaxRSAuthConfig(JaxRSAuthConfig config) {
        try(Connection connection = this.dataSource.getConnection();
            PreparedStatement psm = connection.prepareStatement("");) {
            psm.setString(1, config.getSubject());
            psm.executeUpdate();
        } catch (SQLException ex) {
        }
    }

    public void updateJaxRSAuthConfig(JaxRSAuthConfig config) {
        try(Connection connection = this.dataSource.getConnection();
            PreparedStatement psm = connection.prepareStatement("");) {
            psm.setString(1, config.getSubject());
            psm.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.error("SQLException!!", ex);
        }
    }

    public void deleteJaxRSAuthConfig(JaxRSAuthConfig config) {
        try(Connection connection = this.dataSource.getConnection();
            PreparedStatement psm = connection.prepareStatement("");) {
            psm.setString(1, config.getSubject());
            psm.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.error("SQLException!!", ex);
        }
    }

    public void getJaxRSAuthConfig(String subject) {
        try(Connection connection = this.dataSource.getConnection();
            PreparedStatement psm = connection.prepareStatement("");) {
            psm.setString(1, subject);
            psm.execute();
        } catch (SQLException ex) {
            LOGGER.error("SQLException!!", ex);
        }
    }

    // LifeCycle methods.

    @Activate
    protected void activate() {
        Properties jdbcProperties = new Properties();
        jdbcProperties.put(DataSourceFactory.JDBC_USER, "sa");
        jdbcProperties.put(DataSourceFactory.JDBC_PASSWORD, "sa");
        jdbcProperties.put(DataSourceFactory.JDBC_URL, "jdbc:h2:file:./deployment/h2db/db:adeptj;DB_CLOSE_DELAY=-1");
        try {
            this.dataSource = dsFactory.createDataSource(jdbcProperties);
            String q1 = "CREATE SCHEMA rest_auth_schema AUTHORIZATION sa;";
            try(Connection connection = this.dataSource.getConnection();
                Statement statement = connection.createStatement();) {
                statement.execute(q1);
                statement.close();
                connection.commit();
            } catch (Exception ex) {
                LOGGER.error("SQLException!!", ex);
            }
        } catch (SQLException ex) {
            LOGGER.error("SQLException!!", ex);
        }
    }

}
