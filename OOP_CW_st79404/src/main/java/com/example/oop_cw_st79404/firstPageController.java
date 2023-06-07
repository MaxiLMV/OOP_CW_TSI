package com.example.oop_cw_st79404;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class firstPageController {
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;

    public void moveToLogin() {
        try {
            Main m = new Main();
            m.changeScene("loginPage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void moveToRegister() {
        try {
            Main m = new Main();
            m.changeScene("registerPage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }
}