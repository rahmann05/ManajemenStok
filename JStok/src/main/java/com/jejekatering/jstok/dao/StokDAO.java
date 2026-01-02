package com.jejekatering.jstok.dao;

import com.jejekatering.jstok.config.KonekDB;
import com.jejekatering.jstok.model.Bahan;
import com.jejekatering.jstok.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class StokDAO {

    public boolean simpanStokMasuk(Bahan bahan, int jumlah, LocalDate tanggal, String keterangan) {
        int idPengguna = 1;
        if (SessionManager.getCurrentUser() != null) {
            idPengguna = SessionManager.getCurrentUser().getIdPengguna();
        }

        String queryHistory = "INSERT INTO transaksi_stok (id_bahan, id_pengguna, jenis_transaksi, jumlah, tanggal, keterangan) VALUES (?, ?, 'Masuk', ?, ?, ?)";
        String queryUpdateStok = "UPDATE bahan SET stok_saat_ini = stok_saat_ini + ? WHERE id_bahan = ?";

        try (Connection conn = KonekDB.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psHistory = conn.prepareStatement(queryHistory);
                 PreparedStatement psUpdate = conn.prepareStatement(queryUpdateStok)) {

                psHistory.setInt(1, bahan.getIdBahan());
                psHistory.setInt(2, idPengguna);
                psHistory.setInt(3, jumlah);

                LocalDateTime now = LocalDateTime.now();
                if (tanggal.equals(LocalDate.now())) {
                    psHistory.setTimestamp(4, Timestamp.valueOf(now));
                } else {
                    psHistory.setTimestamp(4, Timestamp.valueOf(tanggal.atTime(now.toLocalTime())));
                }

                psHistory.setString(5, keterangan);
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
            while (rs.next()) {
                list.add(new TransaksiHariIni(
                    rs.getString("jam"),
                    rs.getString("nama_bahan"),
                    rs.getInt("jumlah") + " " + rs.getString("satuan"),
                    rs.getString("username")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean simpanStokKeluar(Bahan bahan, int jumlah, LocalDate tanggal, String keterangan) {
        int idPengguna = 1;
        if (SessionManager.getCurrentUser() != null) {
            idPengguna = SessionManager.getCurrentUser().getIdPengguna();
        }

        String queryHistory = "INSERT INTO transaksi_stok (id_bahan, id_pengguna, jenis_transaksi, jumlah, tanggal, keterangan) VALUES (?, ?, 'Keluar', ?, ?, ?)";
        String queryUpdateStok = "UPDATE bahan SET stok_saat_ini = stok_saat_ini - ? WHERE id_bahan = ?";

        try (Connection conn = KonekDB.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psHistory = conn.prepareStatement(queryHistory);
                 PreparedStatement psUpdate = conn.prepareStatement(queryUpdateStok)) {

                psHistory.setInt(1, bahan.getIdBahan());
                psHistory.setInt(2, idPengguna);
                psHistory.setInt(3, jumlah);

                LocalDateTime now = LocalDateTime.now();
                if (tanggal.equals(LocalDate.now())) {
                    psHistory.setTimestamp(4, Timestamp.valueOf(now));
                } else {
                    psHistory.setTimestamp(4, Timestamp.valueOf(tanggal.atTime(now.toLocalTime())));
                }

                psHistory.setString(5, keterangan);
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

    public Map<String, String> getRingkasanKeluarHariIni() {
        Map<String, String> stats = new HashMap<>();

        String queryTransaksi = "SELECT COUNT(*) as total_transaksi FROM transaksi_stok WHERE jenis_transaksi = 'Keluar' AND DATE(tanggal) = CURDATE()";

        String queryTerakhir = """
            SELECT b.nama_bahan, t.jumlah, b.satuan 
            FROM transaksi_stok t 
            JOIN bahan b ON t.id_bahan = b.id_bahan 
            WHERE t.jenis_transaksi = 'Keluar' AND DATE(t.tanggal) = CURDATE()
            ORDER BY t.tanggal DESC LIMIT 1
            """;

        String queryPenginput = """
            SELECT COALESCE(p.username, 'Unknown') as username
            FROM transaksi_stok t 
            LEFT JOIN pengguna p ON t.id_pengguna = p.id_pengguna 
            WHERE t.jenis_transaksi = 'Keluar' AND DATE(t.tanggal) = CURDATE()
            ORDER BY t.tanggal DESC LIMIT 1
            """;

        try (Connection conn = KonekDB.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(queryTransaksi);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("total_transaksi", String.valueOf(rs.getInt("total_transaksi")));
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(queryTerakhir);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nama = rs.getString("nama_bahan");
                    int jumlah = rs.getInt("jumlah");
                    String satuan = rs.getString("satuan");
                    stats.put("terakhir_keluar", nama + " (" + jumlah + " " + satuan + ")");
                } else {
                    stats.put("terakhir_keluar", "-");
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(queryPenginput);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("penginput", rs.getString("username"));
                } else {
                    stats.put("penginput", "-");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stats;
    }

    public ObservableList<TransaksiHariIni> getTransaksiKeluarHariIni() {
        ObservableList<TransaksiHariIni> list = FXCollections.observableArrayList();

        String query = """
            SELECT COALESCE(DATE_FORMAT(t.tanggal, '%H:%i'), '-') as jam, 
                   b.nama_bahan, t.jumlah, b.satuan, 
                   COALESCE(p.username, 'Unknown') as username
            FROM transaksi_stok t
            JOIN bahan b ON t.id_bahan = b.id_bahan
            LEFT JOIN pengguna p ON t.id_pengguna = p.id_pengguna
            WHERE t.jenis_transaksi = 'Keluar'
            AND DATE(t.tanggal) = CURDATE()
            ORDER BY t.tanggal DESC
            """;

        try (Connection conn = KonekDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new TransaksiHariIni(
                    rs.getString("jam"),
                    rs.getString("nama_bahan"),
                    rs.getInt("jumlah") + " " + rs.getString("satuan"),
                    rs.getString("username")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}