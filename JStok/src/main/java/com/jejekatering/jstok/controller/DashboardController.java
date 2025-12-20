package com.jejekatering.jstok.controller;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML private StackPane rootStack;
    @FXML private BorderPane mainBorderPane;

    @FXML private Label lblTotalStok;
    @FXML private Label lblStokMasuk;
    @FXML private Label lblStokKeluar;
    @FXML private Label lblStokKritis;
    @FXML private BarChart<String, Number> barChart;

    private Node homeView;
    private boolean isDarkMode = false;

    // INITIALIZATION SECTION
    @FXML
    public void initialize() {
        setTheme(false);
        homeView = mainBorderPane.getCenter();
        loadDummyData();
    }

    // THEME HANDLING SECTION
    @FXML
    protected void onToggleTheme() {
        isDarkMode = !isDarkMode;
        setTheme(isDarkMode);
    }

    private void setTheme(boolean dark) {
        if (dark) {
            Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
            if (rootStack != null) rootStack.getStyleClass().add("dark-mode");
        } else {
            Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
            if (rootStack != null) rootStack.getStyleClass().remove("dark-mode");
        }
    }

    // NAVIGATION SECTION
    @FXML protected void onMenuDashboardClick() {
        if (homeView != null) mainBorderPane.setCenter(homeView);
    }

    private void loadPage(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/jejekatering/jstok/view/" + fxmlFileName + ".fxml"));
            Parent newPage = loader.load();
            mainBorderPane.setCenter(newPage);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML protected void onMenuBahanClick() { loadPage("BahanView"); }
    @FXML protected void onMenuStokMasukClick() { loadPage("StokMasukView"); }
    @FXML protected void onMenuStokKeluarClick() { loadPage("StokKeluarView"); }
    @FXML protected void onMenuLaporanClick() { loadPage("LaporanView"); }

    // DATA HANDLING SECTION
    private void loadDummyData() {
        if (lblTotalStok != null) {
            lblTotalStok.setText("1,240");
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.getData().add(new XYChart.Data<>("Sen", 10));
            series.getData().add(new XYChart.Data<>("Sel", 25));
            series.getData().add(new XYChart.Data<>("Rab", 15));
            series.getData().add(new XYChart.Data<>("Kam", 40));
            series.getData().add(new XYChart.Data<>("Jum", 20));
            barChart.getData().clear();
            barChart.getData().add(series);
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