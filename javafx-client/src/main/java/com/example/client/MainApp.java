package com.example.client;

import com.example.client.controller.ChatController;
import com.example.client.util.SceneManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        System.out.println("APP START");

        var url = getClass().getResource("/view/login.fxml");
        System.out.println("FXML = " + url);

        SceneManager.setStage(stage);

        Parent root = FXMLLoader.load(url);

        stage.setScene(new Scene(root, 800, 600));
        stage.setTitle("Chat");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}