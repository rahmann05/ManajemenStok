package com.jejekatering.jstok.controller;

import animatefx.animation.FadeInUp;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Duration;

public class LaporanController {

    @FXML private TableView<LaporanItem> laporanTable;
    @FXML private TableColumn<LaporanItem, String> colTanggal;
    @FXML private TableColumn<LaporanItem, String> colJenis;
    @FXML private TableColumn<LaporanItem, String> colNama;
    @FXML private TableColumn<LaporanItem, String> colJumlah;
    @FXML private TableColumn<LaporanItem, String> colUser;
    @FXML private TableColumn<LaporanItem, String> colKeterangan;

    @FXML
    public void initialize() {
        colTanggal.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().tanggal));
        colJenis.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().jenis));
        colNama.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().nama));
        colJumlah.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().jumlah));
        colUser.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().user));
        colKeterangan.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().keterangan));

        laporanTable.setItems(getDummyData());
        playEntryAnimations();
    }

    private void playEntryAnimations() {
        if (laporanTable != null) {
            new FadeInUp(laporanTable).setDelay(Duration.millis(100)).play();
        }
    }

    private ObservableList<LaporanItem> getDummyData() {
        return FXCollections.observableArrayList(
                new LaporanItem("2025-12-08", "Masuk", "Beras Premium", "100 Kg", "Admin", "Stok awal bulan"),
                new LaporanItem("2025-12-09", "Keluar", "Minyak Goreng", "10 Liter", "Admin", "Produksi Katering A"),
                new LaporanItem("2025-12-10", "Masuk", "Tepung Terigu", "50 Kg", "Admin", "Restock Supplier")
        );
    }

    public static class LaporanItem {
        String tanggal, jenis, nama, jumlah, user, keterangan;
        public LaporanItem(String t, String j, String n, String jml, String u, String k) {
            this.tanggal = t; this.jenis = j; this.nama = n; this.jumlah = jml; this.user = u; this.keterangan = k;
        }
    }
}