package com.jejekatering.jstok.controller;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.colors.Bright;
import eu.hansolo.tilesfx.tools.Helper;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;

public class DashboardController {

    @FXML private StackPane rootStack;
    @FXML private BorderPane mainBorderPane;

    // TilesFX Components
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
        setupTiles();
        homeView = mainBorderPane.getCenter();
        loadDummyData();
    }

    private void setupTiles() {
        // Tile 1: Total Stok (Numeric)
        tileTotalStok.setSkinType(Tile.SkinType.NUMBER);
        tileTotalStok.setTitle("Total Stok");
        tileTotalStok.setValue(1240);
        tileTotalStok.setUnit("Unit");
        tileTotalStok.setDescription("Total Aset Fisik");
        tileTotalStok.setTextVisible(true);
        tileTotalStok.setBackgroundColor(Color.web("#4361EE")); // Warna Utama
        tileTotalStok.setRoundedCorners(true);

        // Tile 2: Stok Masuk (Sparkline / Graph)
        tileStokMasuk.setSkinType(Tile.SkinType.SPARK_LINE);
        tileStokMasuk.setTitle("Stok Masuk");
        tileStokMasuk.setUnit("Item");
        tileStokMasuk.setBarColor(Color.web("#4CC9F0"));
        tileStokMasuk.setBackgroundColor(Color.web("#ffffff80")); // Semi transparan glass
        tileStokMasuk.setValue(254);

        // Tile 3: Stok Keluar (Sparkline / Graph)
        tileStokKeluar.setSkinType(Tile.SkinType.SPARK_LINE);
        tileStokKeluar.setTitle("Stok Keluar");
        tileStokKeluar.setUnit("Item");
        tileStokKeluar.setBarColor(Color.web("#F72585"));
        tileStokKeluar.setBackgroundColor(Color.web("#ffffff80"));
        tileStokKeluar.setValue(85);

        // Tile 4: Stok Kritis (Gauge / Percentage)
        tileKritis.setSkinType(Tile.SkinType.GAUGE);
        tileKritis.setTitle("Kapasitas Gudang");
        tileKritis.setUnit("%");
        tileKritis.setThreshold(80);
        tileKritis.setValue(65);
        tileKritis.setBackgroundColor(Color.web("#ffffff80"));
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

            // Adjust Tiles for Dark Mode
            tileStokMasuk.setBackgroundColor(Color.web("#1e293b80"));
            tileStokKeluar.setBackgroundColor(Color.web("#1e293b80"));
            tileKritis.setBackgroundColor(Color.web("#1e293b80"));
        } else {
            Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
            if (rootStack != null) rootStack.getStyleClass().remove("dark-mode");

            // Adjust Tiles for Light Mode
            tileStokMasuk.setBackgroundColor(Color.web("#ffffff80"));
            tileStokKeluar.setBackgroundColor(Color.web("#ffffff80"));
            tileKritis.setBackgroundColor(Color.web("#ffffff80"));
        }
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