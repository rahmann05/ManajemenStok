package com.jejekatering.jstok.dao;

import com.jejekatering.jstok.config.KonekDB;
import com.jejekatering.jstok.model.Bahan;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class StokDAO {

    public boolean simpanStokMasuk(Bahan bahan, int jumlah, LocalDate tanggal, String keterangan) {
        // Nama tabel disesuaikan: transaksi_stok
        String queryHistory = "INSERT INTO transaksi_stok (id_bahan, id_pengguna, jenis_transaksi, jumlah, tanggal, keterangan) VALUES (?, 1, 'Masuk', ?, ?, ?)";
        String queryUpdateStok = "UPDATE bahan SET stok_saat_ini = stok_saat_ini + ? WHERE id_bahan = ?";

        try (Connection conn = KonekDB.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psHistory = conn.prepareStatement(queryHistory);
                 PreparedStatement psUpdate = conn.prepareStatement(queryUpdateStok)) {

                psHistory.setInt(1, bahan.getIdBahan());
                psHistory.setInt(2, jumlah);
                psHistory.setDate(3, Date.valueOf(tanggal));
                psHistory.setString(4, keterangan);
                psHistory.executeUpdate();

                psUpdate.setInt(1, jumlah);
                psUpdate.setInt(2, bahan.getIdBahan());
                psUpdate.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, String> getRingkasanHariIni() {
        Map<String, String> stats = new HashMap<>();
        
        String query = "SELECT SUM(jumlah) as total_item, COUNT(DISTINCT id_bahan) as total_jenis, MAX(tanggal) as jam_terakhir " +
                "FROM transaksi_stok WHERE jenis_transaksi = 'Masuk' AND DATE(tanggal) = CURDATE()";

        try (Connection conn = KonekDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                stats.put("total_item", String.valueOf(rs.getInt("total_item")));
                stats.put("total_jenis", String.valueOf(rs.getInt("total_jenis")));
                Timestamp ts = rs.getTimestamp("jam_terakhir");
                stats.put("jam_terakhir", ts != null ? new java.text.SimpleDateFormat("HH:mm").format(ts) : "-");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
}