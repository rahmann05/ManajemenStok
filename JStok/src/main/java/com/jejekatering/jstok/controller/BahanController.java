package com.jejekatering.jstok.controller;

import com.jejekatering.jstok.dao.BahanDAO;
import com.jejekatering.jstok.model.Bahan;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;

public class BahanController {

    @FXML private TableView<Bahan> tableBahan;
    @FXML private TableColumn<Bahan, Integer> colId;
    @FXML private TableColumn<Bahan, String> colNama;
    @FXML private TableColumn<Bahan, String> colSatuan;
    @FXML private TableColumn<Bahan, Integer> colStok;
    @FXML private TableColumn<Bahan, Integer> colMinStok;

    @FXML private TextField txtSearch;
    @FXML private TextField txtNama;
    @FXML private ComboBox<String> comboSatuan;
    @FXML private TextField txtStok;
    @FXML private TextField txtMinStok;

    @FXML private Button btnSimpan;
    @FXML private Button btnHapus;
    @FXML private Button btnReset;

    private BahanDAO bahanDAO;
    private Bahan selectedBahan;
    private FilteredList<Bahan> filteredData;

    @FXML
    public void initialize() {
        bahanDAO = new BahanDAO();
        setupTable();
        setupSatuanOptions();
        loadData();
        setupFormSelection();
        setupSearch();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idBahan"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaBahan"));
        colSatuan.setCellValueFactory(new PropertyValueFactory<>("satuan"));
        colStok.setCellValueFactory(new PropertyValueFactory<>("stokSaatIni"));
        colMinStok.setCellValueFactory(new PropertyValueFactory<>("stokMinimum"));
    }

    private void setupSatuanOptions() {
        comboSatuan.setItems(FXCollections.observableArrayList(
                "Kg", "Gram", "Liter", "Ml", "Pcs", "Pack", "Dus", "Sachet", "Box"
        ));
    }

    private void loadData() {
        ObservableList<Bahan> data = bahanDAO.getAllBahan();
        filteredData = new FilteredList<>(data, p -> true);
        tableBahan.setItems(filteredData);
    }

    private void setupSearch() {
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(bahan -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return bahan.getNamaBahan().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }

    private void setupFormSelection() {
        tableBahan.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedBahan = newSelection;
                txtNama.setText(newSelection.getNamaBahan());
                comboSatuan.setValue(newSelection.getSatuan());
                txtStok.setText(String.valueOf(newSelection.getStokSaatIni()));
                txtMinStok.setText(String.valueOf(newSelection.getStokMinimum()));

                btnSimpan.setText("Update Data");
                btnHapus.setDisable(false);
            }
        });
    }

    @FXML
    protected void onSimpanClick() {
        String nama = txtNama.getText();
        String satuan = comboSatuan.getValue();

        if (nama.isEmpty() || satuan == null || satuan.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Nama dan Satuan tidak boleh kosong.");
            return;
        }

        int stok;
        int min;
        try {
            stok = Integer.parseInt(txtStok.getText());
            min = Integer.parseInt(txtMinStok.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Stok dan Min. Stok harus berupa angka.");
            return;
        }

        if (selectedBahan == null) {
            Bahan baru = new Bahan(0, nama, satuan, stok, min);
            if (bahanDAO.tambahBahan(baru)) {
                loadData();
                onResetClick();
            }
        } else {
            selectedBahan.setNamaBahan(nama);
            selectedBahan.setSatuan(satuan);
            selectedBahan.setStokSaatIni(stok);
            selectedBahan.setStokMinimum(min);
            if (bahanDAO.updateBahan(selectedBahan)) {
                loadData();
                onResetClick();
            }
        }
    }

    @FXML
    protected void onHapusClick() {
        if (selectedBahan != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin menghapus data ini?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                if (bahanDAO.hapusBahan(selectedBahan.getIdBahan())) {
                    loadData();
                    onResetClick();
                }
            }
        }
    }

    @FXML
    protected void onResetClick() {
        selectedBahan = null;
        txtNama.clear();
        comboSatuan.setValue(null);
        txtStok.clear();
        txtMinStok.clear();
        btnSimpan.setText("Simpan Data");
        btnHapus.setDisable(true);
        tableBahan.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}