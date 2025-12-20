package com.jejekatering.jstok.controller;

import com.jejekatering.jstok.util.SessionManager;
import com.jejekatering.jstok.model.Pengguna;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import java.io.IOException;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label roleLabel;

    @FXML
    public void initialize() {
        // Ambil user dari SessionManager saat dashboard dibuka
        Pengguna user = SessionManager.getCurrentUser();

        if (user != null) {
            welcomeLabel.setText("Halo, " + user.getUsername());
            roleLabel.setText(user.getRole());
        } else {
            welcomeLabel.setText("Halo, Tamu");
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