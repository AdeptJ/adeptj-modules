package com.adeptj.modules.data.jpa;

import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class MyPGSimpleDataSource extends PGSimpleDataSource {
    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = super.getConnection();
        connection.setAutoCommit(false);
        return connection;
    }
}
