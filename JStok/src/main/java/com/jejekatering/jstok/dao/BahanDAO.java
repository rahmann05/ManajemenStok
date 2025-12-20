package com.jejekatering.jstok.dao;

import com.jejekatering.jstok.config.KonekDB;
import com.jejekatering.jstok.model.Bahan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class BahanDAO {

    // Mengambil semua data bahan untuk ditampilkan di Tabel
    public ObservableList<Bahan> getAllBahan() {
        ObservableList<Bahan> listBahan = FXCollections.observableArrayList();
        String query = "SELECT * FROM bahan";

        try (Connection conn = KonekDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Bahan bahan = new Bahan(
                        rs.getInt("id_bahan"),
                        rs.getString("nama_bahan"),
                        rs.getString("satuan"),
                        rs.getInt("stok_saat_ini"),
                        rs.getInt("stok_minimum")
                );
                listBahan.add(bahan);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listBahan;
    }
}