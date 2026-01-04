package com.jejekatering.jstok.controller;

import com.jejekatering.jstok.dao.BahanDAO;
import com.jejekatering.jstok.model.Bahan;
import com.jejekatering.jstok.model.Pengguna;
import com.jejekatering.jstok.util.SessionManager;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class BahanController {

    @FXML private TableView<Bahan> tableBahan;
    @FXML private TableColumn<Bahan, Integer> colId;
    @FXML private TableColumn<Bahan, String> colNama;
    @FXML private TableColumn<Bahan, String> colSatuan;
    @FXML private TableColumn<Bahan, Integer> colStok;
    @FXML private TableColumn<Bahan, Integer> colMinStok;
    @FXML private TableColumn<Bahan, Void> colStatus;

    @FXML private TextField txtSearch;
    @FXML private TextField txtNama;
    @FXML private ComboBox<String> comboSatuan;
    @FXML private TextField txtStok;
    @FXML private TextField txtMinStok;

    @FXML private Button btnSimpan;
    @FXML private Button btnHapus;
    @FXML private Button btnReset;
    @FXML private VBox detailBahanCard;

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
        applyRoleRestrictions();
    }

    private void applyRoleRestrictions() {
        Pengguna currentUser = SessionManager.getCurrentUser();
        if (currentUser != null && "pegawai".equalsIgnoreCase(currentUser.getRole())) {
            if (detailBahanCard != null) {
                detailBahanCard.setVisible(false);
                detailBahanCard.setManaged(false);
            }
        }
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idBahan"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaBahan"));
        colSatuan.setCellValueFactory(new PropertyValueFactory<>("satuan"));
        colStok.setCellValueFactory(new PropertyValueFactory<>("stokSaatIni"));
        colMinStok.setCellValueFactory(new PropertyValueFactory<>("stokMinimum"));

        if (colStatus != null) {
            colStatus.setCellFactory(param -> new TableCell<>() {
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                        setGraphic(null);
                    } else {
                        Bahan bahan = getTableRow().getItem();
                        HBox statusBox = new HBox();
                        statusBox.setAlignment(Pos.CENTER);

                        Circle dot = new Circle(6);
                        dot.setFill(Color.web(bahan.getStatusColor()));

                        // Add tooltip for status label
                        Tooltip tooltip = new Tooltip(bahan.getStatusLabel());
                        Tooltip.install(dot, tooltip);

                        statusBox.getChildren().add(dot);
                        setGraphic(statusBox);
                    }
                }
            });
        }
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