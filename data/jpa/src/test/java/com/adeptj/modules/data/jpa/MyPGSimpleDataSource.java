package com.adeptj.modules.data.jpa;

import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class MyPGSimpleDataSource extends PGSimpleDataSource {

    private final boolean autoCommit;

    public MyPGSimpleDataSource(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = super.getConnection();
        connection.setAutoCommit(this.autoCommit);
        return connection;
    }
}
