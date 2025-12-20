package com.jejekatering.jstok.controller;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import eu.hansolo.tilesfx.Tile;
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
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML private StackPane rootStack;
    @FXML private BorderPane mainBorderPane;

    @FXML private Tile tileTotalStok;
    @FXML private Tile tileStokMasuk;
    @FXML private Tile tileStokKeluar;
    @FXML private Tile tileKritis;

    @FXML private BarChart<String, Number> barChart;

    private Node homeView;
    private boolean isDarkMode = false;

    @FXML
    public void initialize() {
        setTheme(false);

        homeView = mainBorderPane.getCenter();
        loadDummyData();
    }

    private void setupTiles() {
        // Inisialisasi properti dasar Tile (yang tidak berubah antar tema)

        tileTotalStok.setSkinType(Tile.SkinType.NUMBER);
        tileTotalStok.setTitle("Total Stok");
        tileTotalStok.setUnit("Unit");
        tileTotalStok.setValue(1240);
        tileTotalStok.setDescription("Aset Aktif");
        tileTotalStok.setBackgroundColor(Color.web("#8B5CF6")); // Aksen Ungu
        tileTotalStok.setTitleColor(Color.WHITE);
        tileTotalStok.setValueColor(Color.WHITE);
        tileTotalStok.setUnitColor(Color.WHITE);
        tileTotalStok.setDescriptionColor(Color.WHITE);
        tileTotalStok.setRoundedCorners(true);

        tileStokMasuk.setSkinType(Tile.SkinType.SPARK_LINE);
        tileStokMasuk.setTitle("Stok Masuk");
        tileStokMasuk.setUnit("Item");
        tileStokMasuk.setValue(254);
        tileStokMasuk.setBarColor(Color.web("#10B981"));
        tileStokMasuk.setStrokeWithGradient(true);
        tileStokMasuk.setRoundedCorners(true);

        tileStokKeluar.setSkinType(Tile.SkinType.SPARK_LINE);
        tileStokKeluar.setTitle("Stok Keluar");
        tileStokKeluar.setUnit("Item");
        tileStokKeluar.setValue(85);
        tileStokKeluar.setBarColor(Color.web("#EF4444"));
        tileStokKeluar.setStrokeWithGradient(true);
        tileStokKeluar.setRoundedCorners(true);

        tileKritis.setSkinType(Tile.SkinType.GAUGE);
        tileKritis.setTitle("Kapasitas");
        tileKritis.setUnit("%");
        tileKritis.setValue(75);
        tileKritis.setThreshold(80);
        tileKritis.setBarColor(Color.web("#8B5CF6"));
        tileKritis.setRoundedCorners(true);
    }

    private void updateTileColors(boolean dark) {
        Color bgColor = dark ? Color.web("#1E293B") : Color.WHITE;
        Color textColor = dark ? Color.web("#F1F5F9") : Color.web("#1E293B");

        tileStokMasuk.setBackgroundColor(bgColor);
        tileStokMasuk.setTitleColor(textColor);
        tileStokMasuk.setValueColor(textColor);
        tileStokMasuk.setUnitColor(textColor);

        tileStokKeluar.setBackgroundColor(bgColor);
        tileStokKeluar.setTitleColor(textColor);
        tileStokKeluar.setValueColor(textColor);
        tileStokKeluar.setUnitColor(textColor);

        tileKritis.setBackgroundColor(bgColor);
        tileKritis.setTitleColor(textColor);
        tileKritis.setValueColor(textColor);
        tileKritis.setUnitColor(textColor);
        tileKritis.setNeedleColor(textColor);
    }

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

        if (tileTotalStok.getSkinType() == null) {
            setupTiles();
        }
        updateTileColors(dark);
    }

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

    private void loadDummyData() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Sen", 10));
        series.getData().add(new XYChart.Data<>("Sel", 25));
        series.getData().add(new XYChart.Data<>("Rab", 15));
        series.getData().add(new XYChart.Data<>("Kam", 40));
        series.getData().add(new XYChart.Data<>("Jum", 20));
        barChart.getData().clear();
        barChart.getData().add(series);
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