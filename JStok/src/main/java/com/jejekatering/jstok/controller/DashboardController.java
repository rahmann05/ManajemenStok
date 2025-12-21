package com.jejekatering.jstok.controller;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeInUp;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

public class DashboardController {

    @FXML private StackPane rootStack;
    @FXML private BorderPane mainBorderPane;
    @FXML private ScrollPane dashboardContent;

    @FXML private StackPane card1, card2, card3, card4;
    @FXML private VBox chartSection, activitySection;

    @FXML private Tile tileTotalStok, tileStokMasuk, tileStokKeluar, tileKritis;
    @FXML private BarChart<String, Number> barChart;

    @FXML private Button btnDashboard;
    @FXML private Button btnBahan;
    @FXML private Button btnStokMasuk;
    @FXML private Button btnStokKeluar;
    @FXML private Button btnLaporan;
    @FXML private FontIcon themeToggleIcon;
    @FXML private Label themeLabel;

    private Node homeView;
    private boolean isDarkMode = false;

    @FXML
    public void initialize() {
        setTheme(false);
        homeView = dashboardContent;

        setupTiles();
        loadDummyData();
        runAnimations();
        setActiveButton(btnDashboard);
    }

    private void runAnimations() {
        new FadeIn(rootStack).play();
        new FadeInUp(card1).setDelay(Duration.millis(100)).play();
        new FadeInUp(card2).setDelay(Duration.millis(200)).play();
        new FadeInUp(card3).setDelay(Duration.millis(300)).play();
        new FadeInUp(card4).setDelay(Duration.millis(400)).play();
        new FadeInUp(chartSection).setDelay(Duration.millis(600)).play();
        new FadeInUp(activitySection).setDelay(Duration.millis(700)).play();
    }

    private void setupTiles() {
        Color accentPurple = Color.web("#8B5CF6");

        tileTotalStok.setSkinType(Tile.SkinType.NUMBER);
        tileTotalStok.setTitle("Total Stok");
        tileTotalStok.setUnit("Unit");
        tileTotalStok.setValue(1240);
        tileTotalStok.setDescription("Aset Fisik");
        tileTotalStok.setBackgroundColor(accentPurple);
        tileTotalStok.setTitleColor(Color.WHITE);
        tileTotalStok.setValueColor(Color.WHITE);
        tileTotalStok.setUnitColor(Color.WHITE);
        tileTotalStok.setDescriptionColor(Color.WHITE);
        tileTotalStok.setRoundedCorners(true);

        tileStokMasuk.setSkinType(Tile.SkinType.SPARK_LINE);
        tileStokMasuk.setTitle("Stok Masuk");
        tileStokMasuk.setUnit("Item");
        tileStokMasuk.setValue(254);
        tileStokMasuk.setBarColor(Color.web("#34C759"));
        tileStokMasuk.setRoundedCorners(true);
        tileStokMasuk.setShadowsEnabled(false);

        tileStokKeluar.setSkinType(Tile.SkinType.SPARK_LINE);
        tileStokKeluar.setTitle("Stok Keluar");
        tileStokKeluar.setUnit("Item");
        tileStokKeluar.setValue(85);
        tileStokKeluar.setBarColor(Color.web("#FF3B30"));
        tileStokKeluar.setRoundedCorners(true);
        tileStokKeluar.setShadowsEnabled(false);

        tileKritis.setSkinType(Tile.SkinType.GAUGE);
        tileKritis.setTitle("Kapasitas");
        tileKritis.setUnit("%");
        tileKritis.setValue(75);
        tileKritis.setThreshold(80);
        tileKritis.setBarColor(accentPurple);
        tileKritis.setRoundedCorners(true);
        tileKritis.setShadowsEnabled(false);

        updateTileColors(false);
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
        btn.getStyleClass().remove("nav-button-active");
        if (!btn.getStyleClass().contains("nav-button")) {
            btn.getStyleClass().add("nav-button");
        }
    }

    @FXML
    protected void onToggleTheme() {
        isDarkMode = !isDarkMode;
        setTheme(isDarkMode);
    }

    private void setTheme(boolean dark) {
        if (dark) {
            Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
            if (rootStack != null && !rootStack.getStyleClass().contains("dark-mode")) {
                rootStack.getStyleClass().add("dark-mode");
            }
        } else {
            Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
            if (rootStack != null) rootStack.getStyleClass().remove("dark-mode");
        }
        updateTileColors(dark);
        updateToggleIconColor(dark);
    }

    private void updateTileColors(boolean dark) {
        Color textColor = dark ? Color.WHITE : Color.web("#1C1C1E");
        Color glassBg = Color.TRANSPARENT;

        tileStokMasuk.setBackgroundColor(glassBg);
        tileStokMasuk.setTitleColor(textColor);
        tileStokMasuk.setValueColor(textColor);
        tileStokMasuk.setUnitColor(textColor);

        tileStokKeluar.setBackgroundColor(glassBg);
        tileStokKeluar.setTitleColor(textColor);
        tileStokKeluar.setValueColor(textColor);
        tileStokKeluar.setUnitColor(textColor);

        tileKritis.setBackgroundColor(glassBg);
        tileKritis.setTitleColor(textColor);
        tileKritis.setValueColor(textColor);
        tileKritis.setUnitColor(textColor);
        tileKritis.setNeedleColor(textColor);
    }

    private void updateToggleIconColor(boolean dark) {
        if (themeToggleIcon != null) {
            themeToggleIcon.setIconLiteral(dark ? "fth-sun" : "fth-moon");
            themeToggleIcon.setIconColor(Color.web(dark ? "#F5F5F7" : "#1D1D1F"));
        }
        if (themeLabel != null) {
            themeLabel.setText(dark ? "Light Mode" : "Dark Mode");
        }
    }

    @FXML
    protected void onMenuDashboardClick() {
        if (homeView != null) {
            mainBorderPane.setCenter(homeView);
            runAnimations();
        }
        setActiveButton(btnDashboard);
    }

    private void loadPage(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/jejekatering/jstok/view/" + fxmlFileName + ".fxml"));
            Parent newPage = loader.load();
            mainBorderPane.setCenter(newPage);
            new FadeInUp(newPage).setDelay(Duration.millis(100)).play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onMenuBahanClick() {
        loadPage("BahanView");
        setActiveButton(btnBahan);
    }

    @FXML
    protected void onMenuStokMasukClick() {
        loadPage("StokMasukView");
        setActiveButton(btnStokMasuk);
    }

    @FXML
    protected void onMenuStokKeluarClick() {
        loadPage("StokKeluarView");
        setActiveButton(btnStokKeluar);
    }

    @FXML
    protected void onMenuLaporanClick() {
        loadPage("LaporanView");
        setActiveButton(btnLaporan);
    }

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}