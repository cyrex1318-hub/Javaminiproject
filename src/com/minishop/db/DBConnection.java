package com.minishop.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/online_shop_awt?serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static final String URL = resolve("DB_URL", DEFAULT_URL);
    private static final String USER = resolve("DB_USER", DEFAULT_USER);
    private static final String PASSWORD = resolve("DB_PASSWORD", DEFAULT_PASSWORD);

    private DBConnection() {}

    private static String resolve(String key, String def) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isEmpty()) return sys;
        String env = System.getenv(key);
        if (env != null && !env.isEmpty()) return env;
        return def;
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Add mysql-connector-j to classpath.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}


