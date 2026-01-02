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

public XYChart.Series<String, Number> getStockMovementSeries(int idBahan, String namaBahan, int currentStock) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(namaBahan);

        String query = """
            WITH RECURSIVE dates AS (
                SELECT CURDATE() - INTERVAL 6 DAY as date_val
                UNION ALL
                SELECT date_val + INTERVAL 1 DAY FROM dates WHERE date_val < CURDATE()
            ),
            daily_changes AS (
                SELECT DATE(t.tanggal) as tgl,
                       SUM(CASE WHEN t.jenis_transaksi = 'Masuk' THEN t.jumlah ELSE -t.jumlah END) as perubahan
                FROM transaksi_stok t
                WHERE t.id_bahan = ?
                AND DATE(t.tanggal) >= CURDATE() - INTERVAL 6 DAY
                GROUP BY DATE(t.tanggal)
            )
            SELECT DATE_FORMAT(d.date_val, '%d %b') as hari,
                   COALESCE(dc.perubahan, 0) as perubahan,
                   d.date_val
            FROM dates d
            LEFT JOIN daily_changes dc ON d.date_val = dc.tgl
            ORDER BY d.date_val ASC
            """;

        try (Connection conn = KonekDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idBahan);
            ResultSet rs = stmt.executeQuery();

            int runningStock = currentStock;
            java.util.List<int[]> changes = new java.util.ArrayList<>();

            while (rs.next()) {
                changes.add(new int[]{rs.getInt("perubahan")});
            }

            for (int i = changes.size() - 1; i >= 0; i--) {
                runningStock -= changes.get(i)[0];
            }

            rs = stmt.executeQuery();
            while (rs.next()) {
                runningStock += rs.getInt("perubahan");
                series.getData().add(new XYChart.Data<>(rs.getString("hari"), runningStock));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return series;
    }

public List<XYChart.Series<String, Number>> getAllStockMovementSeries() {
        List<XYChart.Series<String, Number>> allSeries = new ArrayList<>();

        String queryBahan = """
            SELECT b.id_bahan, b.nama_bahan, b.stok_saat_ini, b.stok_minimum
            FROM bahan b
            ORDER BY b.nama_bahan
            """;

        try (Connection conn = KonekDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(queryBahan);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int idBahan = rs.getInt("id_bahan");
                String namaBahan = rs.getString("nama_bahan");
                int currentStock = rs.getInt("stok_saat_ini");
                int minStock = rs.getInt("stok_minimum");
                XYChart.Series<String, Number> series = getStockLevelSeries(idBahan, namaBahan, currentStock, minStock);
                allSeries.add(series);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allSeries;
    }

    private String getIndonesianDayName(int dayOfWeek) {
        return switch (dayOfWeek) {
            case 1 -> "Min";
            case 2 -> "Sen";
            case 3 -> "Sel";
            case 4 -> "Rab";
            case 5 -> "Kam";
            case 6 -> "Jum";
            case 7 -> "Sab";
            default -> "";
        };
    }

    public java.util.List<String> getWeekLabels() {
        java.util.List<String> labels = new java.util.ArrayList<>();
        String query = """
            SELECT DATE_FORMAT(dates.dt, '%d') as tanggal,
                   DAYOFWEEK(dates.dt) as day_of_week
            FROM (
                SELECT DATE_SUB(CURDATE(), INTERVAL (DAYOFWEEK(CURDATE()) - 2 + 7) % 7 DAY) + INTERVAL seq DAY as dt
                FROM (
                    SELECT 0 as seq UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 
                    UNION SELECT 4 UNION SELECT 5 UNION SELECT 6
                ) seqs
            ) dates
            ORDER BY dates.dt ASC
            """;

        try (Connection conn = KonekDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int dayOfWeek = rs.getInt("day_of_week");
                String tanggal = rs.getString("tanggal");
                String label = getIndonesianDayName(dayOfWeek) + " " + tanggal;
                labels.add(label);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return labels;
    }

    public XYChart.Series<String, Number> getStockLevelSeries(int idBahan, String namaBahan, int currentStock, int minStock) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(namaBahan);

        if (minStock <= 0) {
            minStock = 1;
        }

        String queryFirstTransaction = """
            SELECT MIN(DATE(tanggal)) as first_date
            FROM transaksi_stok
            WHERE id_bahan = ?
            """;

        String queryWeekDates = """
            SELECT DATE_FORMAT(dates.dt, '%d') as tanggal,
                   dates.dt,
                   DAYOFWEEK(dates.dt) as day_of_week
            FROM (
                SELECT DATE_SUB(CURDATE(), INTERVAL (DAYOFWEEK(CURDATE()) - 2 + 7) % 7 DAY) + INTERVAL seq DAY as dt
                FROM (
                    SELECT 0 as seq UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 
                    UNION SELECT 4 UNION SELECT 5 UNION SELECT 6
                ) seqs
            ) dates
            ORDER BY dates.dt ASC
            """;

        String queryChanges = """
            SELECT DATE(t.tanggal) as tgl,
                   SUM(CASE WHEN t.jenis_transaksi = 'Masuk' THEN t.jumlah ELSE -t.jumlah END) as perubahan
            FROM transaksi_stok t
            WHERE t.id_bahan = ?
            AND DATE(t.tanggal) >= ?
            AND DATE(t.tanggal) <= CURDATE()
            GROUP BY DATE(t.tanggal)
            """;

        try (Connection conn = KonekDB.getConnection()) {
            String firstTransactionDate = null;
            try (PreparedStatement stmt = conn.prepareStatement(queryFirstTransaction)) {
                stmt.setInt(1, idBahan);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    firstTransactionDate = rs.getString("first_date");
                }
            }

            java.util.Map<String, Integer> dailyChanges = new java.util.LinkedHashMap<>();
            java.util.List<String> dates = new java.util.ArrayList<>();
            java.util.List<String> dateLabels = new java.util.ArrayList<>();
            java.util.List<Integer> dayOfWeeks = new java.util.ArrayList<>();
            String mondayDate = null;

            try (PreparedStatement stmt = conn.prepareStatement(queryWeekDates);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String dateStr = rs.getString("dt");
                    int dayOfWeek = rs.getInt("day_of_week");
                    String tanggal = rs.getString("tanggal");
                    String label = getIndonesianDayName(dayOfWeek) + " " + tanggal;

                    dates.add(dateStr);
                    dateLabels.add(label);
                    dayOfWeeks.add(dayOfWeek);
                    dailyChanges.put(dateStr, 0);
                    if (mondayDate == null) {
                        mondayDate = dateStr;
                    }
                }
            }

            if (dates.isEmpty() || mondayDate == null) {
                return series;
            }

            try (PreparedStatement stmt = conn.prepareStatement(queryChanges)) {
                stmt.setInt(1, idBahan);
                stmt.setString(2, mondayDate);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String tgl = rs.getString("tgl");
                    int change = rs.getInt("perubahan");
                    dailyChanges.put(tgl, change);
                }
            }

            int totalChangesThisWeek = 0;
            for (int change : dailyChanges.values()) {
                totalChangesThisWeek += change;
            }

            int stockAtMonday;
            if (firstTransactionDate == null) {
                stockAtMonday = currentStock;
            } else if (firstTransactionDate.compareTo(mondayDate) >= 0) {
                stockAtMonday = 0;
            } else {
                stockAtMonday = currentStock - totalChangesThisWeek;
            }

            int runningStock = stockAtMonday;
            final int finalMinStock = minStock;

            for (int i = 0; i < dates.size(); i++) {
                String dateKey = dates.get(i);
                int dayOfWeek = dayOfWeeks.get(i);
                int change = dailyChanges.getOrDefault(dateKey, 0);

                boolean isMonday = (dayOfWeek == 2);
                boolean isSunday = (dayOfWeek == 1);
                boolean hasChange = (change != 0);
                boolean isFirstTransaction = (firstTransactionDate != null && dateKey.equals(firstTransactionDate));

                if (firstTransactionDate != null && dateKey.compareTo(firstTransactionDate) < 0) {
                    if (isMonday) {
                        series.getData().add(new XYChart.Data<>(dateLabels.get(i), 0.0));
                    }
                } else {
                    runningStock += change;
                    if (isMonday || isSunday || hasChange || isFirstTransaction) {
                        double ratio = (double) Math.max(0, runningStock) / finalMinStock * 100;
                        series.getData().add(new XYChart.Data<>(dateLabels.get(i), Math.round(ratio)));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return series;
    }

    public XYChart.Series<String, Number> getTotalMasukPerDay() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Masuk");

        String query = """
            SELECT DATE_FORMAT(tanggal, '%d %b') as hari, SUM(jumlah) as total 
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

    public XYChart.Series<String, Number> getTotalKeluarPerDay() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Keluar");

        String query = """
            SELECT DATE_FORMAT(tanggal, '%d %b') as hari, SUM(jumlah) as total 
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
        String query = "SELECT id_bahan, nama_bahan, satuan, stok_saat_ini, stok_minimum FROM bahan WHERE stok_saat_ini <= stok_minimum ORDER BY stok_saat_ini ASC, (stok_minimum - stok_saat_ini) DESC";

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
