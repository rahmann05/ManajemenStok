package com.jejekatering.jstok.controller;

import com.jejekatering.jstok.model.Pengguna;
import com.jejekatering.jstok.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private BorderPane mainBorderPane; // Wajib ada fx:id ini di FXML

    @FXML
    public void initialize() {
        Pengguna user = SessionManager.getCurrentUser();

        if (user != null) {
            welcomeLabel.setText("Halo, " + user.getUsername());
            roleLabel.setText(user.getRole());
        } else {
            welcomeLabel.setText("Halo, Tamu");
        }
    }


    @FXML
    protected void onMenuBahanClick() {
        System.out.println("Menu Bahan Diklik");
        loadPage("BahanView");
    }
    @FXML
    protected void onMenuStokMasukClick() {
        loadPage("StokMasukView");
    }

    @FXML
    protected void onMenuStokKeluarClick() {
        loadPage("StokKeluarView");
    }

    @FXML
    protected void onMenuLaporanClick() {
        loadPage("LaporanView");
    }
    private void loadPage(String pageName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/jejekatering/jstok/view/" + pageName + ".fxml"));
            Parent root = loader.load();
            mainBorderPane.setCenter(root); // Ganti area tengah BorderPane
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Gagal memuat halaman: " + pageName);
        }
    }


    @FXML
    protected void onLogoutClick() {
        SessionManager.logout();

        try {
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/jejekatering/jstok/view/LoginView.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);

            stage.setTitle("Login - J Stok");
            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}