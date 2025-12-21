package com.jejekatering.jstok.controller;

import animatefx.animation.FadeInUp;
import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import com.jejekatering.jstok.dao.BahanDAO;
import com.jejekatering.jstok.model.Bahan;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class BahanController {

    @FXML private StackPane rootStack;
    @FXML private BorderPane mainBorderPane;

    @FXML private Button btnDashboard, btnBahan, btnStokMasuk, btnStokKeluar, btnLaporan;

    @FXML private TableView<Bahan> tableBahan;
    @FXML private TableColumn<Bahan, Integer> colId;
    @FXML private TableColumn<Bahan, String> colNama;
    @FXML private TableColumn<Bahan, String> colSatuan;
    @FXML private TableColumn<Bahan, Integer> colStok;
    @FXML private TableColumn<Bahan, Integer> colMinStok;

    @FXML private TextField txtSearch;
    @FXML private TextField txtNama;
    @FXML private TextField txtSatuan;
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

        updateThemeState();
        setActiveButton(btnBahan);

        setupTable();
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
                txtSatuan.setText(newSelection.getSatuan());
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
        String satuan = txtSatuan.getText();

        if (nama.isEmpty() || satuan.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Nama dan Satuan tidak boleh kosong.");
            return;
        }

        int stok = 0;
        int min = 0;
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
        txtSatuan.clear();
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

    private void setActiveButton(Button activeButton) {
        resetButtonStyle(btnDashboard);
        resetButtonStyle(btnBahan);
        resetButtonStyle(btnStokMasuk);
        resetButtonStyle(btnStokKeluar);
        resetButtonStyle(btnLaporan);

        activeButton.getStyleClass().add("nav-button-active");
    }

    private void resetButtonStyle(Button btn) {
        if (btn != null) {
            btn.getStyleClass().remove("nav-button-active");
            if (!btn.getStyleClass().contains("nav-button")) {
                btn.getStyleClass().add("nav-button");
            }
        }
    }

    @FXML
    protected void onToggleTheme() {
        boolean isCurrentDark = rootStack.getStyleClass().contains("dark-mode");
        if (isCurrentDark) {
            Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
            rootStack.getStyleClass().remove("dark-mode");
        } else {
            Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
            rootStack.getStyleClass().add("dark-mode");
        }
    }

    private void updateThemeState() {
        // Default theme check, sesuaikan jika Anda menyimpan state tema di Session/Config
        Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
    }

    private void loadPage(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/jejekatering/jstok/view/" + fxmlFileName + ".fxml"));
            Parent newPage = loader.load();
            Scene scene = rootStack.getScene();
            scene.setRoot(newPage);

            new FadeInUp(newPage).setDelay(Duration.millis(100)).play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML protected void onMenuDashboardClick() { loadPage("DashboardView"); }
    @FXML protected void onMenuBahanClick() { loadPage("BahanView"); }
    @FXML protected void onMenuStokMasukClick() { loadPage("StokMasukView"); }
    @FXML protected void onMenuStokKeluarClick() { loadPage("StokKeluarView"); }
    @FXML protected void onMenuLaporanClick() { loadPage("LaporanView"); }

    @FXML
    protected void onLogoutClick() {
        try {
            Stage stage = (Stage) rootStack.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/com/jejekatering/jstok/view/LoginView.fxml"));
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }
}