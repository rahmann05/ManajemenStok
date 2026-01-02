package com.jejekatering.jstok.controller;

import com.jejekatering.jstok.dao.BahanDAO;
import com.jejekatering.jstok.dao.StokDAO;
import com.jejekatering.jstok.model.Bahan;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import java.time.LocalDate;
import java.util.Map;

public class StokMasukController {

    @FXML private ComboBox<Bahan> comboBahan;
    @FXML private TextField txtJumlah;
    @FXML private DatePicker datePicker;
    @FXML private TextField txtKeterangan;

    @FXML private Label lblTotalMasuk;
    @FXML private Label lblJenisBahan;
    @FXML private Label lblWaktuTerakhir;

    private BahanDAO bahanDAO = new BahanDAO();
    private StokDAO stokDAO = new StokDAO();

    @FXML
    public void initialize() {
        datePicker.setValue(LocalDate.now());
        setupComboBox();
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

    private void refreshData() {
        ObservableList<Bahan> list = bahanDAO.getAllBahan();
        comboBahan.setItems(list);

        // Ambil data statistik hari ini
        Map<String, String> stats = stokDAO.getRingkasanHariIni();
        lblTotalMasuk.setText(stats.getOrDefault("total_item", "0") + " Item");
        lblJenisBahan.setText(stats.getOrDefault("total_jenis", "0") + " Jenis");
        lblWaktuTerakhir.setText(stats.getOrDefault("jam_terakhir", "-") + " WIB");
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