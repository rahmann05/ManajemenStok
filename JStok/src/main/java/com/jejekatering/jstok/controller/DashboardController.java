package com.jejekatering.jstok.controller;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node; // Import Node
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

import java.io.IOException;

public class DashboardController {

    @FXML private StackPane rootStack;
    @FXML private BorderPane mainBorderPane;

    // Widgets Dashboard
    @FXML private Label lblTotalStok;
    @FXML private Label lblStokMasuk;
    @FXML private Label lblStokKeluar;
    @FXML private Label lblStokKritis;
    @FXML private BarChart<String, Number> barChart;

    // Variabel untuk menyimpan halaman HOME (Dashboard awal)
    private Node homeView;
    private boolean isDarkMode = false;

    @FXML
    public void initialize() {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        // 1. SIMPAN TAMPILAN AWAL (DASHBOARD) KE VARIABEL
        // Ini penting agar saat pindah halaman, kita bisa kembali ke sini.
        homeView = mainBorderPane.getCenter();

        loadDummyData();
        showNotification("Sistem Siap", "Dashboard berhasil dimuat.");
    }

    // --- NAVIGASI ---

    // Fungsi untuk kembali ke Dashboard
    @FXML
    protected void onMenuDashboardClick() {
        // Kembalikan area tengah ke homeView yang sudah kita simpan di awal
        if (homeView != null) {
            mainBorderPane.setCenter(homeView);
        }
    }

    // Fungsi ganti halaman generik
    private void loadPage(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/jejekatering/jstok/view/" + fxmlFileName + ".fxml"));
            Parent newPage = loader.load();
            mainBorderPane.setCenter(newPage); // Ganti area tengah dengan halaman baru
        } catch (IOException e) {
            e.printStackTrace();
            showNotification("Error Navigasi", "Gagal memuat halaman: " + fxmlFileName);
        }
    }

    @FXML protected void onMenuBahanClick() { loadPage("BahanView"); }
    @FXML protected void onMenuStokMasukClick() { loadPage("StokMasukView"); }
    @FXML protected void onMenuStokKeluarClick() { loadPage("StokKeluarView"); }
    @FXML protected void onMenuLaporanClick() { loadPage("LaporanView"); }

    // --- DATA & LOGIKA LAIN ---

    private void loadDummyData() {
        if (lblTotalStok != null) {
            lblTotalStok.setText("1,240");
            lblStokMasuk.setText("35");
            lblStokKeluar.setText("12");
            lblStokKritis.setText("5");

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Aktivitas");
            series.getData().add(new XYChart.Data<>("Sen", 10));
            series.getData().add(new XYChart.Data<>("Sel", 20));
            series.getData().add(new XYChart.Data<>("Rab", 15));
            series.getData().add(new XYChart.Data<>("Kam", 45));
            series.getData().add(new XYChart.Data<>("Jum", 25));

            barChart.getData().clear();
            barChart.getData().add(series);
        }
    }

    private void showNotification(String title, String text) {
        Notifications.create()
                .title(title)
                .text(text)
                .darkStyle()
                .showInformation();
    }

    @FXML
    protected void onToggleTheme() {
        isDarkMode = !isDarkMode;
        if (isDarkMode) {
            Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        } else {
            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        }
    }

    @FXML
    protected void onLogoutClick() {
        try {
            Stage stage = (Stage) rootStack.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/com/jejekatering/jstok/view/LoginView.fxml"));
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }
}