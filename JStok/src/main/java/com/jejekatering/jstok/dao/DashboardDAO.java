package com.jejekatering.jstok.dao;

import com.jejekatering.jstok.config.KonekDB;
import com.jejekatering.jstok.model.Bahan;
import javafx.scene.chart.XYChart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {

    public int getStokNormalCount() {
        String query = "SELECT COUNT(*) as total FROM bahan WHERE stok_saat_ini > stok_minimum";
        try (Connection conn = KonekDB.getConnection();
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

    public int getStokRendahCount() {
        String query = "SELECT COUNT(*) as total FROM bahan WHERE stok_saat_ini <= stok_minimum AND stok_saat_ini > 0";
        try (Connection conn = KonekDB.getConnection();
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

    public int getStokHabisCount() {
        String query = "SELECT COUNT(*) as total FROM bahan WHERE stok_saat_ini = 0";
        try (Connection conn = KonekDB.getConnection();
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

    public List<Bahan> getAllBahanWithStatus() {
        List<Bahan> items = new ArrayList<>();
        String query = "SELECT id_bahan, nama_bahan, satuan, stok_saat_ini, stok_minimum FROM bahan ORDER BY " +
                       "CASE WHEN stok_saat_ini = 0 THEN 0 " +
                       "WHEN stok_saat_ini <= stok_minimum THEN 1 " +
                       "ELSE 2 END, nama_bahan ASC";
        try (Connection conn = KonekDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                items.add(new Bahan(
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
        return items;
    }

    public List<Bahan> getBahanDibawahMinimum() {
        List<Bahan> items = new ArrayList<>();
        String query = "SELECT id_bahan, nama_bahan, satuan, stok_saat_ini, stok_minimum FROM bahan " +
                       "WHERE stok_saat_ini <= stok_minimum " +
                       "ORDER BY CASE WHEN stok_saat_ini = 0 THEN 0 ELSE 1 END, " +
                       "(stok_minimum - stok_saat_ini) DESC";
        try (Connection conn = KonekDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                items.add(new Bahan(
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
        return items;
    }

    public int getStokKritisCount() {
        String query = "SELECT COUNT(*) as total FROM bahan WHERE stok_saat_ini <= stok_minimum";
        try (Connection conn = KonekDB.getConnection();
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

    public int getDeadStockCount() {
        String query = """
            SELECT COUNT(*) as total FROM bahan b 
            WHERE b.id_bahan NOT IN (
                SELECT DISTINCT t.id_bahan FROM transaksi_stok t 
                WHERE t.jenis_transaksi = 'Keluar' 
                AND t.tanggal >= DATE_SUB(CURDATE(), INTERVAL 90 DAY)
            )
            """;
        try (Connection conn = KonekDB.getConnection();
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

    public int getTodayTransactionCount() {
        String query = "SELECT COUNT(*) as total FROM transaksi_stok WHERE DATE(tanggal) = CURDATE()";
        try (Connection conn = KonekDB.getConnection();
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

    public int getTotalSKU() {
        String query = "SELECT COUNT(*) as total FROM bahan";
        try (Connection conn = KonekDB.getConnection();
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

    public XYChart.Series<String, Number> getChartDataMasukFrequency() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Masuk");

        String query = """
            SELECT DATE_FORMAT(tanggal, '%d %b') as hari, COUNT(*) as total 
            FROM transaksi_stok 
            WHERE tanggal >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) 
            AND jenis_transaksi = 'Masuk'
            GROUP BY DATE(tanggal) 
            ORDER BY tanggal ASC
            """;

        try (Connection conn = KonekDB.getConnection();
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

    public XYChart.Series<String, Number> getChartDataKeluarFrequency() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Keluar");

        String query = """
            SELECT DATE_FORMAT(tanggal, '%d %b') as hari, COUNT(*) as total 
            FROM transaksi_stok 
            WHERE tanggal >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) 
            AND jenis_transaksi = 'Keluar'
            GROUP BY DATE(tanggal) 
            ORDER BY tanggal ASC
            """;

        try (Connection conn = KonekDB.getConnection();
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

    public List<Bahan> getRestockItems() {
        List<Bahan> items = new ArrayList<>();
        String query = "SELECT id_bahan, nama_bahan, satuan, stok_saat_ini, stok_minimum FROM bahan WHERE stok_saat_ini < stok_minimum ORDER BY (stok_minimum - stok_saat_ini) DESC";

        try (Connection conn = KonekDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                items.add(new Bahan(
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
        return items;
    }

    public List<FastMovingItem> getFastMovingItems() {
        List<FastMovingItem> items = new ArrayList<>();
        String query = """
            SELECT b.nama_bahan, b.satuan, SUM(t.jumlah) as total_keluar
            FROM transaksi_stok t
            JOIN bahan b ON t.id_bahan = b.id_bahan
            WHERE t.jenis_transaksi = 'Keluar'
            AND t.tanggal >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
            GROUP BY t.id_bahan
            ORDER BY total_keluar DESC
            LIMIT 5
            """;

        try (Connection conn = KonekDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                items.add(new FastMovingItem(
                    rs.getString("nama_bahan"),
                    rs.getInt("total_keluar"),
                    rs.getString("satuan")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public static class FastMovingItem {
        private String namaBahan;
        private int totalKeluar;
        private String satuan;

        public FastMovingItem(String namaBahan, int totalKeluar, String satuan) {
            this.namaBahan = namaBahan;
            this.totalKeluar = totalKeluar;
            this.satuan = satuan;
        }

        public String getNamaBahan() { return namaBahan; }
        public int getTotalKeluar() { return totalKeluar; }
        public String getSatuan() { return satuan; }
    }

    public int getTotalStok() {
        String query = "SELECT SUM(stok_saat_ini) as total FROM bahan";
        try (Connection conn = KonekDB.getConnection();
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
        try (Connection conn = KonekDB.getConnection();
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
        try (Connection conn = KonekDB.getConnection();
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
        return getStokKritisCount();
    }

    public XYChart.Series<String, Number> getChartData() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Aktivitas");

        String query = """
            SELECT DATE_FORMAT(tanggal, '%d %b') as hari, COUNT(*) as total 
            FROM transaksi_stok 
            WHERE tanggal >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
            GROUP BY DATE(tanggal) 
            ORDER BY tanggal ASC
            """;

        try (Connection conn = KonekDB.getConnection();
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
