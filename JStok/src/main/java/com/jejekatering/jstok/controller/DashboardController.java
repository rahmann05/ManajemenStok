package com.jejekatering.jstok.controller;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeInUp;
import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import com.jejekatering.jstok.dao.DashboardDAO;
import com.jejekatering.jstok.model.Bahan;
import com.jejekatering.jstok.model.Pengguna;
import com.jejekatering.jstok.util.SessionManager;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.List;

public class DashboardController {

    @FXML private StackPane rootStack;
    @FXML private BorderPane mainBorderPane;
    @FXML private ScrollPane dashboardContent;

    @FXML private VBox card1, card2, card3, card4;
    @FXML private VBox chartSection, restockSection, fastMovingSection;
    @FXML private HBox statusIndicatorSection;

    @FXML private CategoryAxis xAxis;

    @FXML private Label lblStokKritisValue, lblDeadStockValue, lblTransaksiHariIniValue, lblTotalSKUValue;
    @FXML private Label lblStokKritisDesc, lblDeadStockDesc, lblTransaksiHariIniDesc, lblTotalSKUDesc;

    @FXML private Label lblStokNormal, lblStokRendah, lblStokHabis;

    @FXML private LineChart<String, Number> lineChart;
    @FXML private TableView<Bahan> tblRestock;
    @FXML private TableColumn<Bahan, String> colNamaBahan, colSatuan;
    @FXML private TableColumn<Bahan, Integer> colSisaStok, colMinStok;
    @FXML private TableColumn<Bahan, Void> colStatus;
    @FXML private VBox fastMovingList;

    @FXML private Button btnDashboard;
    @FXML private Button btnBahan;
    @FXML private Button btnStokMasuk;
    @FXML private Button btnStokKeluar;
    @FXML private Button btnLaporan;
    @FXML private FontIcon themeToggleIcon;
    @FXML private Label themeLabel;

    private Node homeView;
    private boolean isDarkMode = false;
    private DashboardDAO dashboardDAO;

    @FXML
    public void initialize() {
        dashboardDAO = new DashboardDAO();
        setTheme(false);
        homeView = dashboardContent;

        setupTable();
        loadDashboardData();
        runAnimations();
        setActiveButton(btnDashboard);
        applyRoleRestrictions();
    }

    private void applyRoleRestrictions() {
        Pengguna currentUser = SessionManager.getCurrentUser();
        if (currentUser != null && "pegawai".equalsIgnoreCase(currentUser.getRole())) {
            if (chartSection != null) {
                chartSection.setVisible(false);
                chartSection.setManaged(false);
            }
        }
    }

    private void runAnimations() {
        new FadeIn(rootStack).play();
        new FadeInUp(card1).setDelay(Duration.millis(100)).play();
        new FadeInUp(card2).setDelay(Duration.millis(200)).play();
        new FadeInUp(card3).setDelay(Duration.millis(300)).play();
        new FadeInUp(card4).setDelay(Duration.millis(400)).play();
        new FadeInUp(chartSection).setDelay(Duration.millis(500)).play();
        new FadeInUp(restockSection).setDelay(Duration.millis(600)).play();
        new FadeInUp(fastMovingSection).setDelay(Duration.millis(700)).play();
    }

    private void setupTable() {
        colNamaBahan.setCellValueFactory(new PropertyValueFactory<>("namaBahan"));
        colSisaStok.setCellValueFactory(new PropertyValueFactory<>("stokSaatIni"));
        colMinStok.setCellValueFactory(new PropertyValueFactory<>("stokMinimum"));
        colSatuan.setCellValueFactory(new PropertyValueFactory<>("satuan"));

        if (colStatus != null) {
            colStatus.setCellFactory(param -> new TableCell<>() {
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                        setGraphic(null);
                    } else {
                        Bahan bahan = getTableRow().getItem();
                        HBox statusBox = new HBox(6);
                        statusBox.setAlignment(Pos.CENTER_LEFT);

                        Circle dot = new Circle(5);
                        dot.setFill(Color.web(bahan.getStatusColor()));

                        Label statusLabel = new Label(bahan.getStatusLabel());
                        statusLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: " + bahan.getStatusColor() + ";");

                        statusBox.getChildren().addAll(dot, statusLabel);
                        setGraphic(statusBox);
                    }
                }
            });
        }
    }

    private void loadDashboardData() {
        int stokKritis = dashboardDAO.getStokKritisCount();
        int deadStock = dashboardDAO.getDeadStockCount();
        int transaksiHariIni = dashboardDAO.getTodayTransactionCount();
        int totalSKU = dashboardDAO.getTotalSKU();

        lblStokKritisValue.setText(String.valueOf(stokKritis));
        lblDeadStockValue.setText(String.valueOf(deadStock));
        lblTransaksiHariIniValue.setText(String.valueOf(transaksiHariIni));
        lblTotalSKUValue.setText(String.valueOf(totalSKU));

        lblStokKritisDesc.setText(stokKritis > 0 ? "Perlu restock segera!" : "Semua stok aman");
        lblDeadStockDesc.setText(deadStock > 0 ? "Tidak ada keluar 90 hari" : "Semua barang bergerak");
        lblTransaksiHariIniDesc.setText(transaksiHariIni > 0 ? "Transaksi hari ini" : "Belum ada transaksi");
        lblTotalSKUDesc.setText("Jenis barang terdaftar");

        loadStatusIndicators();

        loadChartData();

        loadRestockData();

        loadFastMovingData();
    }

    private void loadStatusIndicators() {
        int stokNormal = dashboardDAO.getStokNormalCount();
        int stokRendah = dashboardDAO.getStokRendahCount();
        int stokHabis = dashboardDAO.getStokHabisCount();

        if (lblStokNormal != null) lblStokNormal.setText(String.valueOf(stokNormal));
        if (lblStokRendah != null) lblStokRendah.setText(String.valueOf(stokRendah));
        if (lblStokHabis != null) lblStokHabis.setText(String.valueOf(stokHabis));
    }

private void loadChartData() {
        lineChart.getData().clear();

        if (xAxis != null) {
            java.util.List<String> weekLabels = dashboardDAO.getWeekLabels();
            xAxis.setCategories(FXCollections.observableArrayList(weekLabels));
            xAxis.setAutoRanging(false);
        }

        List<XYChart.Series<String, Number>> allSeries = dashboardDAO.getAllStockMovementSeries();

        for (XYChart.Series<String, Number> series : allSeries) {
            lineChart.getData().add(series);
        }

        lineChart.setCreateSymbols(true);
        lineChart.setAnimated(false);
    }

    private void loadRestockData() {
        List<Bahan> restockItems = dashboardDAO.getRestockItems();
        ObservableList<Bahan> data = FXCollections.observableArrayList(restockItems);
        tblRestock.setItems(data);
    }

    private void loadFastMovingData() {
        fastMovingList.getChildren().clear();
        List<DashboardDAO.FastMovingItem> fastMovingItems = dashboardDAO.getFastMovingItems();

        if (fastMovingItems.isEmpty()) {
            Label emptyLabel = new Label("Belum ada data pergerakan");
            emptyLabel.getStyleClass().add("text-secondary");
            fastMovingList.getChildren().add(emptyLabel);
            return;
        }

        int rank = 1;
        for (DashboardDAO.FastMovingItem item : fastMovingItems) {
            HBox row = createFastMovingRow(rank, item);
            fastMovingList.getChildren().add(row);
            rank++;
        }
    }

    private HBox createFastMovingRow(int rank, DashboardDAO.FastMovingItem item) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("fast-moving-item");
        row.setPadding(new Insets(10, 12, 10, 12));

        Label rankLabel = new Label(String.valueOf(rank));
        rankLabel.getStyleClass().add("fast-moving-rank");
        rankLabel.setMinWidth(28);
        rankLabel.setAlignment(Pos.CENTER);

        VBox infoBox = new VBox(2);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        Label nameLabel = new Label(item.getNamaBahan());
        nameLabel.getStyleClass().add("fast-moving-name");
        Label qtyLabel = new Label("Keluar: " + item.getTotalKeluar() + " " + item.getSatuan());
        qtyLabel.getStyleClass().add("fast-moving-qty");
        infoBox.getChildren().addAll(nameLabel, qtyLabel);

        FontIcon trendIcon = new FontIcon("fth-trending-up");
        trendIcon.setIconSize(16);
        trendIcon.setIconColor(Color.web("#34C759"));

        row.getChildren().addAll(rankLabel, infoBox, trendIcon);
        return row;
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
        updateToggleIconColor(dark);
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
            loadDashboardData(); // Refresh data
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

    @FXML
    protected void onInputMasukClick() {
        loadPage("StokMasukView");
        setActiveButton(btnStokMasuk);
    }

    @FXML
    protected void onCekStokMinimumClick() {
        // Scroll ke bagian restock section di bawah dashboard
        scrollToRestockSection();
    }

    private void scrollToRestockSection() {
        if (dashboardContent != null && restockSection != null) {
            // Animate scroll to bottom where restock table is located
            javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                    Duration.millis(500),
                    new javafx.animation.KeyValue(
                        dashboardContent.vvalueProperty(),
                        1.0, // Scroll to bottom
                        javafx.animation.Interpolator.EASE_BOTH
                    )
                )
            );
            timeline.play();

            // Highlight the restock section briefly
            restockSection.setStyle("-fx-effect: dropshadow(gaussian, rgba(255, 149, 0, 0.4), 20, 0.3, 0, 0);");
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(e -> restockSection.setStyle(""));
            pause.play();
        }
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