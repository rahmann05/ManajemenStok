package com.jejekatering.jstok.dao;

import com.jejekatering.jstok.config.KonekDB;
import com.jejekatering.jstok.model.Pengguna;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PenggunaDAO {

    public Pengguna validasiLogin(String username, String passwordInput) {
        Pengguna pengguna = null;
        String query = "SELECT * FROM pengguna WHERE username = ? AND password = ?";

        try (Connection conn = KonekDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, passwordInput);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                pengguna = new Pengguna();
                pengguna.setIdPengguna(rs.getInt("id_pengguna"));
                pengguna.setUsername(rs.getString("username"));
                pengguna.setPassword(rs.getString("password"));
                pengguna.setRole(rs.getString("role"));
            }

        } catch (SQLException e) {
            System.err.println("Error Login: " + e.getMessage());
        }

        return pengguna;
    }
}