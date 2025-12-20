package com.jejekatering.jstok.controller;

import com.jejekatering.jstok.util.SessionManager;
import com.jejekatering.jstok.dao.PenggunaDAO;
import com.jejekatering.jstok.model.Pengguna;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Button; // Import Button
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            tampilkanAlert(Alert.AlertType.WARNING, "Peringatan", "Username dan Password tidak boleh kosong!");
            return;
        }

        PenggunaDAO dao = new PenggunaDAO();
        Pengguna user = dao.validasiLogin(username, password);

        if (user != null) {
            SessionManager.setCurrentUser(user);
            System.out.println("Login Berhasil! Role: " + user.getRole());
            pindahKeDashboard();
        } else {
            tampilkanAlert(Alert.AlertType.ERROR, "Login Gagal", "Username atau Password salah.");
        }
    }

    private void pindahKeDashboard() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/jejekatering/jstok/view/DashboardView.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1280, 800);

            stage.setTitle("Dashboard - J Stok");
            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            tampilkanAlert(Alert.AlertType.ERROR, "System Error", "Gagal memuat halaman Dashboard.");
        }
    }

    private void tampilkanAlert(Alert.AlertType tipe, String judul, String pesan) {
        Alert alert = new Alert(tipe);
        alert.setTitle(judul);
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }
}