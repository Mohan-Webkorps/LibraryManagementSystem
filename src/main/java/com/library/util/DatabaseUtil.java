package com.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class DatabaseUtil implements ServletContextListener {
    private static final String URL = "jdbc:mysql://localhost:3306/library_management_system";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static boolean initialized = false;


    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            throw new SQLException("Database connection not initialized. Make sure the application has started properly.");
        }
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            conn.setAutoCommit(true);
            return conn;
        } catch (SQLException e) {
            System.err.println("Error establishing database connection: " + e.getMessage());
            throw e;
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
} 