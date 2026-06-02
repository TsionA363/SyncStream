package com.syncstream.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("SyncStream — Watch Together");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        showLogin();
        primaryStage.show();
    }

    public static void showLogin() throws Exception {
        Parent root = FXMLLoader.load(MainApp.class.getResource("/fxml/login.fxml"));
        primaryStage.setScene(new Scene(root, 500, 400));
        primaryStage.setTitle("SyncStream — Login");
    }

    public static void showLobby() throws Exception {
        Parent root = FXMLLoader.load(MainApp.class.getResource("/fxml/lobby.fxml"));
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.setTitle("SyncStream — Lobby");
    }

    public static void showRoom() throws Exception {
        Parent root = FXMLLoader.load(MainApp.class.getResource("/fxml/room.fxml"));
        primaryStage.setScene(new Scene(root, 1100, 700));
        primaryStage.setTitle("SyncStream — Watch Room");
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}