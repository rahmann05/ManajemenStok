package com.jejekatering.jstok.dao;

import com.jejekatering.jstok.model.Bahan;
import com.jejekatering.jstok.util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BahanDAO {

    public ObservableList<Bahan> getAllBahan() {
        ObservableList<Bahan> listBahan = FXCollections.observableArrayList();
        String query = "SELECT * FROM bahan ORDER BY nama_bahan ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                listBahan.add(new Bahan(
                        rs.getInt("id_bahan"),
                        rs.getString("nama_bahan"),
                        rs.getString("satuan"),
                        rs.getInt("stok_saat_ini"),
                        rs.getInt("stok_minimum")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listBahan;
    }

    public boolean tambahBahan(Bahan bahan) {
        String query = "INSERT INTO bahan (nama_bahan, satuan, stok_saat_ini, stok_minimum) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, bahan.getNamaBahan());
            stmt.setString(2, bahan.getSatuan());
            stmt.setInt(3, bahan.getStokSaatIni());
            stmt.setInt(4, bahan.getStokMinimum());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBahan(Bahan bahan) {
        String query = "UPDATE bahan SET nama_bahan = ?, satuan = ?, stok_saat_ini = ?, stok_minimum = ? WHERE id_bahan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, bahan.getNamaBahan());
            stmt.setString(2, bahan.getSatuan());
            stmt.setInt(3, bahan.getStokSaatIni());
            stmt.setInt(4, bahan.getStokMinimum());
            stmt.setInt(5, bahan.getIdBahan());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hapusBahan(int idBahan) {
        String query = "DELETE FROM bahan WHERE id_bahan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idBahan);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}