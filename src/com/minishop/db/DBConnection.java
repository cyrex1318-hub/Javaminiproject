package com.minishop.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/online_shop_awt?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // set your MySQL root password

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Add mysql-connector-j to classpath.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}


