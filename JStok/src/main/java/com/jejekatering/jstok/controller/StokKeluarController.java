package com.jejekatering.jstok.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class StokKeluarController {

    @FXML
    private ComboBox<String> comboBahan;

    @FXML
    private TextField txtJumlah;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField txtKeterangan;

    @FXML
    public void initialize() {
        System.out.println("Halaman Stok Keluar dimuat");
        loadBahanData();
    }

    private void loadBahanData() {
        // TODO: Load bahan from database
    }
}