package com.jejekatering.jstok.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class KonekDB {

    private static final String URL = "jdbc:mysql://localhost:3306/db_manajemen_stok";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        Connection connection = null;
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("KonekDB: Berhasil terhubung ke Database!");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("KonekDB Error: " + e.getMessage());
        }
        return connection;
    }
}