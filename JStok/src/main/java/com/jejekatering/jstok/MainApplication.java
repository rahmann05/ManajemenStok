package com.jejekatering.jstok;

import com.jejekatering.jstok.config.KonekDB; // Import nama baru
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("view/test-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("J Stok - Setup Check");
        stage.setScene(scene);
        stage.show();

        // Panggil tes koneksi dengan nama baru
        tesKoneksi();
    }

    private void tesKoneksi() {
        System.out.println("--- System Check ---");
        // Menggunakan KonekDB
        Connection conn = KonekDB.getConnection();

        if (conn != null) {
            System.out.println("GUI: OK (Jendela Muncul)");
            System.out.println("DATABASE: Siap digunakan.");
        } else {
            System.out.println("GUI: OK");
            System.err.println("DATABASE: Gagal (Cek XAMPP)");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}