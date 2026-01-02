package com.jejekatering.jstok.controller;

import com.jejekatering.jstok.dao.BahanDAO;
import com.jejekatering.jstok.dao.StokDAO;
import com.jejekatering.jstok.model.Bahan;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import java.time.LocalDate;
import java.util.Map;

public class StokMasukController {

    @FXML private ComboBox<Bahan> comboBahan;
    @FXML private TextField txtJumlah;
    @FXML private DatePicker datePicker;
    @FXML private TextField txtKeterangan;

    @FXML private Label lblInputHariIni;
    @FXML private Label lblTerakhirMasuk;
    @FXML private Label lblPenginput;

    @FXML private TableView<StokDAO.TransaksiHariIni> tblTransaksiHariIni;
    @FXML private TableColumn<StokDAO.TransaksiHariIni, String> colJam;
    @FXML private TableColumn<StokDAO.TransaksiHariIni, String> colNamaBahan;
    @FXML private TableColumn<StokDAO.TransaksiHariIni, String> colJumlah;
    @FXML private TableColumn<StokDAO.TransaksiHariIni, String> colOleh;

    private BahanDAO bahanDAO = new BahanDAO();
    private StokDAO stokDAO = new StokDAO();

    @FXML
    public void initialize() {
        datePicker.setValue(LocalDate.now());
        setupComboBox();
        setupTable();
        refreshData();
    }

    private void setupComboBox() {
        comboBahan.setConverter(new StringConverter<Bahan>() {
            @Override
            public String toString(Bahan b) { return b == null ? "" : b.getNamaBahan(); }
            @Override
            public Bahan fromString(String s) { return null; }
        });
    }

    private void setupTable() {
        if (tblTransaksiHariIni != null && colJam != null) {
            colJam.setCellValueFactory(new PropertyValueFactory<>("jam"));
            colNamaBahan.setCellValueFactory(new PropertyValueFactory<>("namaBahan"));
            colJumlah.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
            colOleh.setCellValueFactory(new PropertyValueFactory<>("oleh"));

            tblTransaksiHariIni.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        }
    }

    private void refreshData() {
        ObservableList<Bahan> list = bahanDAO.getAllBahan();
        comboBahan.setItems(list);

        Map<String, String> stats = stokDAO.getRingkasanHariIni();

        if (lblInputHariIni != null) {
            int totalTransaksi = Integer.parseInt(stats.getOrDefault("total_transaksi", "0"));
            lblInputHariIni.setText(totalTransaksi + " Transaksi");
        }

        if (lblTerakhirMasuk != null) {
            lblTerakhirMasuk.setText(stats.getOrDefault("terakhir_masuk", "-"));
        }

        if (lblPenginput != null) {
            lblPenginput.setText(stats.getOrDefault("penginput", "-"));
        }

        if (tblTransaksiHariIni != null) {
            tblTransaksiHariIni.getItems().clear();
            tblTransaksiHariIni.setItems(stokDAO.getTransaksiHariIni());
        }
    }

    @FXML
    private void handleSimpan() {
        Bahan selected = comboBahan.getValue();
        if (selected == null || txtJumlah.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Lengkapi data dulu!").show();
            return;
        }

        try {
            int jumlah = Integer.parseInt(txtJumlah.getText());
            boolean success = stokDAO.simpanStokMasuk(selected, jumlah, datePicker.getValue(), txtKeterangan.getText());

            if (success) {
                txtJumlah.clear();
                txtKeterangan.clear();
                refreshData();
                new Alert(Alert.AlertType.INFORMATION, "Stok berhasil masuk!").show();
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Jumlah harus angka!").show();
        }
    }
}