package com.jejekatering.jstok.dao;

import com.jejekatering.jstok.util.DatabaseConnection;
import javafx.scene.chart.XYChart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardDAO {

    public int getTotalStok() {
        String query = "SELECT SUM(stok_saat_ini) as total FROM bahan";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getStokMasukBulanIni() {
        String query = "SELECT SUM(jumlah) as total FROM transaksi_stok WHERE jenis_transaksi = 'Masuk' AND MONTH(tanggal) = MONTH(CURRENT_DATE()) AND YEAR(tanggal) = YEAR(CURRENT_DATE())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getStokKeluarBulanIni() {
        String query = "SELECT SUM(jumlah) as total FROM transaksi_stok WHERE jenis_transaksi = 'Keluar' AND MONTH(tanggal) = MONTH(CURRENT_DATE()) AND YEAR(tanggal) = YEAR(CURRENT_DATE())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getItemKritisCount() {
        String query = "SELECT COUNT(*) as total FROM bahan WHERE stok_saat_ini <= stok_minimum";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public XYChart.Series<String, Number> getChartData() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Aktivitas");

        String query = "SELECT DATE_FORMAT(tanggal, '%d %b') as hari, SUM(jumlah) as total " +
                "FROM transaksi_stok " +
                "WHERE tanggal >= DATE(NOW()) - INTERVAL 7 DAY " +
                "GROUP BY DATE(tanggal) " +
                "ORDER BY tanggal ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(rs.getString("hari"), rs.getInt("total")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return series;
    }
}