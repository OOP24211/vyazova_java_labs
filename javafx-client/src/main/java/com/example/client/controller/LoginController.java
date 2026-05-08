package com.example.client.controller;

import com.example.client.service.ApiService;
import com.example.client.session.AppSession;
import com.example.client.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private TextField passwordField;

    private final ApiService api = new ApiService();

    @FXML
    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        boolean success = api.login(username, password);

        if (success) {
            AppSession.setUsername(username);
            SceneManager.switchScene("/view/rooms.fxml");
        } else {
            System.out.println("Login failed");
        }
    }

    @FXML
    private void register() {
        boolean ok = api.register(
                usernameField.getText(),
                passwordField.getText()
        );

        System.out.println(ok ? "Registered" : "Register failed");
    }
}
