package com.jejekatering.jstok.dao;

import com.jejekatering.jstok.config.KonekDB;
import com.jejekatering.jstok.model.Bahan;
import com.jejekatering.jstok.util.SessionManager;
import com.jejekatering.jstok.controller.LaporanController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class StokDAO {

    // ==========================================
    // BAGIAN 1: SIMPAN TRANSAKSI (MASUK & KELUAR)
    // ==========================================

    public boolean simpanStokMasuk(Bahan bahan, int jumlah, LocalDate tanggal, String keterangan) {
        return eksekusiSimpanStok(bahan, jumlah, tanggal, keterangan, "Masuk");
    }

    public boolean simpanStokKeluar(Bahan bahan, int jumlah, LocalDate tanggal, String keterangan) {
        return eksekusiSimpanStok(bahan, jumlah, tanggal, keterangan, "Keluar");
    }

    private boolean eksekusiSimpanStok(Bahan bahan, int jumlah, LocalDate tanggal, String keterangan, String jenis) {
        int idPengguna = 1;
        if (SessionManager.getCurrentUser() != null) {
            idPengguna = SessionManager.getCurrentUser().getIdPengguna();
        }

        String queryHistory = "INSERT INTO transaksi_stok (id_bahan, id_pengguna, jenis_transaksi, jumlah, tanggal, keterangan) VALUES (?, ?, ?, ?, ?, ?)";
        String queryUpdateStok = jenis.equals("Masuk")
                ? "UPDATE bahan SET stok_saat_ini = stok_saat_ini + ? WHERE id_bahan = ?"
                : "UPDATE bahan SET stok_saat_ini = stok_saat_ini - ? WHERE id_bahan = ?";

        try (Connection conn = KonekDB.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psHistory = conn.prepareStatement(queryHistory);
                 PreparedStatement psUpdate = conn.prepareStatement(queryUpdateStok)) {

                psHistory.setInt(1, bahan.getIdBahan());
                psHistory.setInt(2, idPengguna);
                psHistory.setString(3, jenis);
                psHistory.setInt(4, jumlah);

                LocalDateTime now = LocalDateTime.now();
                if (tanggal.equals(LocalDate.now())) {
                    psHistory.setTimestamp(5, Timestamp.valueOf(now));
                } else {
                    psHistory.setTimestamp(5, Timestamp.valueOf(tanggal.atTime(now.toLocalTime())));
                }

                psHistory.setString(6, keterangan);
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

    // ==========================================
    // BAGIAN 2: RINGKASAN DATA (UNTUK DASHBOARD)
    // ==========================================

    public Map<String, String> getRingkasanHariIni() {
        return getRingkasanBerdasarkanJenis("Masuk");
    }

    public Map<String, String> getRingkasanKeluarHariIni() {
        return getRingkasanBerdasarkanJenis("Keluar");
    }

    private Map<String, String> getRingkasanBerdasarkanJenis(String jenis) {
        Map<String, String> stats = new HashMap<>();
        String queryTransaksi = "SELECT COUNT(*) as total FROM transaksi_stok WHERE jenis_transaksi = ? AND DATE(tanggal) = CURDATE()";
        String queryTerakhir = "SELECT b.nama_bahan, t.jumlah, b.satuan FROM transaksi_stok t JOIN bahan b ON t.id_bahan = b.id_bahan WHERE t.jenis_transaksi = ? AND DATE(t.tanggal) = CURDATE() ORDER BY t.tanggal DESC LIMIT 1";
        String queryPenginput = "SELECT COALESCE(p.username, 'Unknown') as username FROM transaksi_stok t LEFT JOIN pengguna p ON t.id_pengguna = p.id_pengguna WHERE t.jenis_transaksi = ? AND DATE(t.tanggal) = CURDATE() ORDER BY t.tanggal DESC LIMIT 1";

        try (Connection conn = KonekDB.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(queryTransaksi)) {
                ps.setString(1, jenis);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) stats.put("total_transaksi", String.valueOf(rs.getInt("total")));
            }
            try (PreparedStatement ps = conn.prepareStatement(queryTerakhir)) {
                ps.setString(1, jenis);
                ResultSet rs = ps.executeQuery();
                String key = jenis.equals("Masuk") ? "terakhir_masuk" : "terakhir_keluar";
                if (rs.next()) stats.put(key, rs.getString("nama_bahan") + " (" + rs.getInt("jumlah") + " " + rs.getString("satuan") + ")");
                else stats.put(key, "-");
            }
            try (PreparedStatement ps = conn.prepareStatement(queryPenginput)) {
                ps.setString(1, jenis);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) stats.put("penginput", rs.getString("username"));
                else stats.put("penginput", "-");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return stats;
    }

    // ==========================================
    // BAGIAN 3: LAPORAN (FILTERED DATA)
    // ==========================================

    public ObservableList<LaporanController.LaporanItem> getLaporanFiltered(LocalDate dari, LocalDate sampai, String jenis) {
        ObservableList<LaporanController.LaporanItem> list = FXCollections.observableArrayList();
        StringBuilder query = new StringBuilder("""
            SELECT t.tanggal, t.jenis_transaksi, b.nama_bahan, t.jumlah, b.satuan, p.username, t.keterangan 
            FROM transaksi_stok t 
            JOIN bahan b ON t.id_bahan = b.id_bahan 
            JOIN pengguna p ON t.id_pengguna = p.id_pengguna 
            WHERE 1=1
        """);

        if (dari != null) query.append(" AND DATE(t.tanggal) >= '").append(dari).append("'");
        if (sampai != null) query.append(" AND DATE(t.tanggal) <= '").append(sampai).append("'");
        if (jenis != null && !jenis.equals("Semua")) query.append(" AND t.jenis_transaksi = '").append(jenis).append("'");

        query.append(" ORDER BY t.tanggal DESC");

        try (Connection conn = KonekDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query.toString())) {
            while (rs.next()) {
                list.add(new LaporanController.LaporanItem(
                        rs.getTimestamp("tanggal").toString().substring(0, 16),
                        rs.getString("jenis_transaksi"),
                        rs.getString("nama_bahan"),
                        rs.getInt("jumlah") + " " + rs.getString("satuan"),
                        rs.getString("username"),
                        rs.getString("keterangan")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ==========================================
    // BAGIAN 4: TRANSAKSI HARI INI (MODELS)
    // ==========================================

    public ObservableList<TransaksiHariIni> getTransaksiHariIni() {
        return fetchTransaksiHariIni("Masuk");
    }

    public ObservableList<TransaksiHariIni> getTransaksiKeluarHariIni() {
        return fetchTransaksiHariIni("Keluar");
    }

    private ObservableList<TransaksiHariIni> fetchTransaksiHariIni(String jenis) {
        ObservableList<TransaksiHariIni> list = FXCollections.observableArrayList();
        String query = "SELECT DATE_FORMAT(t.tanggal, '%H:%i') as jam, b.nama_bahan, t.jumlah, b.satuan, p.username FROM transaksi_stok t JOIN bahan b ON t.id_bahan = b.id_bahan LEFT JOIN pengguna p ON t.id_pengguna = p.id_pengguna WHERE t.jenis_transaksi = ? AND DATE(t.tanggal) = CURDATE() ORDER BY t.tanggal DESC";
        try (Connection conn = KonekDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, jenis);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new TransaksiHariIni(rs.getString("jam"), rs.getString("nama_bahan"), rs.getInt("jumlah") + " " + rs.getString("satuan"), rs.getString("username")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static class TransaksiHariIni {
        private final javafx.beans.property.SimpleStringProperty jam, namaBahan, jumlah, oleh;
        public TransaksiHariIni(String jam, String namaBahan, String jumlah, String oleh) {
            this.jam = new javafx.beans.property.SimpleStringProperty(jam);
            this.namaBahan = new javafx.beans.property.SimpleStringProperty(namaBahan);
            this.jumlah = new javafx.beans.property.SimpleStringProperty(jumlah);
            this.oleh = new javafx.beans.property.SimpleStringProperty(oleh);
        }
        public String getJam() { return jam.get(); }
        public String getNamaBahan() { return namaBahan.get(); }
        public String getJumlah() { return jumlah.get(); }
        public String getOleh() { return oleh.get(); }
        public javafx.beans.property.SimpleStringProperty jamProperty() { return jam; }
        public javafx.beans.property.SimpleStringProperty namaBahanProperty() { return namaBahan; }
        public javafx.beans.property.SimpleStringProperty jumlahProperty() { return jumlah; }
        public javafx.beans.property.SimpleStringProperty olehProperty() { return oleh; }
    }
}