package com.jejekatering.jstok.controller;

import com.jejekatering.jstok.dao.StokDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public class LaporanController {

    @FXML private TableView<LaporanItem> laporanTable;
    @FXML private TableColumn<LaporanItem, String> colTanggal, colJenis, colNama, colJumlah, colUser, colKeterangan;

    @FXML private DatePicker dpDari, dpSampai;
    @FXML private ComboBox<String> comboJenis;
    @FXML private Label lblTotalMasuk, lblTotalKeluar, lblTotalTransaksi;

    private final StokDAO stokDAO = new StokDAO();

    @FXML
    public void initialize() {
        // 1. Mapping Kolom ke Model
        colTanggal.setCellValueFactory(d -> d.getValue().tanggalProperty());
        colJenis.setCellValueFactory(d -> d.getValue().jenisProperty());
        colNama.setCellValueFactory(d -> d.getValue().namaProperty());
        colJumlah.setCellValueFactory(d -> d.getValue().jumlahProperty());
        colUser.setCellValueFactory(d -> d.getValue().userProperty());
        colKeterangan.setCellValueFactory(d -> d.getValue().keteranganProperty());

        // 2. Inisialisasi ComboBox
        comboJenis.setItems(FXCollections.observableArrayList("Semua", "Masuk", "Keluar"));
        comboJenis.setValue("Semua");

        // 3. Load Data Awal (Tampilkan semua)
        loadData();
    }

    @FXML
    private void handleFilter() {
        loadData();
    }

    private void loadData() {
        // Ambil data dari database melalui DAO
        ObservableList<LaporanItem> data = stokDAO.getLaporanFiltered(
                dpDari.getValue(),
                dpSampai.getValue(),
                comboJenis.getValue()
        );
        laporanTable.setItems(data);
        updateStatistik(data);
    }

    private void updateStatistik(ObservableList<LaporanItem> data) {
        int masuk = 0;
        int keluar = 0;

        for (LaporanItem item : data) {
            if (item.getJenis().equalsIgnoreCase("Masuk")) {
                masuk++;
            } else if (item.getJenis().equalsIgnoreCase("Keluar")) {
                keluar++;
            }
        }

        lblTotalMasuk.setText(masuk + " Item");
        lblTotalKeluar.setText(keluar + " Item");
        lblTotalTransaksi.setText(String.valueOf(data.size()));
    }

    @FXML
    private void handleExport() {
        if (laporanTable.getItems().isEmpty()) {
            showAlert("Export Gagal", "Tidak ada data yang ditampilkan di tabel.");
            return;
        }

        try {
            // Penamaan file dinamis berdasarkan rentang tanggal
            String tglStr = (dpDari.getValue() != null) ? dpDari.getValue() + "_sd_" + dpSampai.getValue() : "Semua_Waktu";
            String fileName = "Laporan_Stok_" + tglStr + ".csv";

            // Simpan ke folder Downloads user
            File file = new File(System.getProperty("user.home") + "/Downloads/" + fileName);

            PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8);
            // Header CSV (Dapat dibuka di Excel)
            pw.println("Tanggal,Jenis,Nama Bahan,Jumlah,User,Keterangan");

            for (LaporanItem item : laporanTable.getItems()) {
                pw.println(String.format("%s,%s,%s,%s,%s,%s",
                        item.getTanggal(), item.getJenis(), item.getNama(),
                        item.getJumlah(), item.getUser(), item.getKeterangan()
                ));
            }
            pw.close();
            showAlert("Export Berhasil", "File disimpan di folder Downloads:\n" + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Gagal export: " + e.getMessage());
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static class LaporanItem {
        private final StringProperty tanggal, jenis, nama, jumlah, user, keterangan;

        public LaporanItem(String t, String j, String n, String jml, String u, String k) {
            this.tanggal = new SimpleStringProperty(t);
            this.jenis = new SimpleStringProperty(j);
            this.nama = new SimpleStringProperty(n);
            this.jumlah = new SimpleStringProperty(jml);
            this.user = new SimpleStringProperty(u);
            this.keterangan = new SimpleStringProperty(k);
        }

        public String getTanggal() { return tanggal.get(); }
        public String getJenis() { return jenis.get(); }
        public String getNama() { return nama.get(); }
        public String getJumlah() { return jumlah.get(); }
        public String getUser() { return user.get(); }
        public String getKeterangan() { return keterangan.get(); }

        public StringProperty tanggalProperty() { return tanggal; }
        public StringProperty jenisProperty() { return jenis; }
        public StringProperty namaProperty() { return nama; }
        public StringProperty jumlahProperty() { return jumlah; }
        public StringProperty userProperty() { return user; }
        public StringProperty keteranganProperty() { return keterangan; }
    }
}