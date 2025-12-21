package com.jejekatering.jstok.controller;

import animatefx.animation.FadeInLeft;
import animatefx.animation.FadeInRight;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class StokMasukController {

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
        System.out.println("Halaman Stok Masuk dimuat");
        loadBahanData();
    }

    private void loadBahanData() {
        // TODO: Load bahan from database
    }
}