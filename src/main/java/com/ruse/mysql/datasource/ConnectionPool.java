package com.ruse.mysql.datasource;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {

    private static final HikariDataSource ds;

    static {
        ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://localhost:3306/practice");
        ds.setUsername("root");
        ds.setPassword("password");

    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
