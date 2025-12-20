package com.jejekatering.jstok;

import atlantafx.base.theme.CupertinoLight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("view/LoginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 800);

        stage.setTitle("J Stok - Apple Spatial");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}