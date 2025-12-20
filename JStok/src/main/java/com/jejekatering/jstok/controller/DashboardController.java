package com.jejekatering.jstok.controller;

package com.jejekatering.jstok.controller;

import com.jejekatering.jstok.util.SessionManager;
import com.jejekatering.jstok.model.Pengguna;
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
    private BorderPane mainBorderPane;

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
        loadPage("BahanView");
    }

    private void loadPage(String pageName) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/jejekatering/jstok/view/" + pageName + ".fxml"));
            mainBorderPane.setCenter(root); // Ganti bagian tengah saja
        } catch (IOException e) {
            e.printStackTrace();
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