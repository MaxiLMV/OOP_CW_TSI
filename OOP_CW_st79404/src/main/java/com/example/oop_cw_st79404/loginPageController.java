package com.example.oop_cw_st79404;

import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.Objects;

public class loginPageController {
    @FXML
    public Button cancelButton;
    @FXML
    public Button log_in;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Label wrongLogin;

    currentUser userData = currentUser.getInstance();

    public void returnToFirst() throws IOException {
        Main m = new Main();
        m.changeScene("firstPage.fxml");
    }

    public void login() {
        if (!(username.getText().isEmpty() && password.getText().isEmpty())) {
            validateLogin();
        } else {
            wrongLogin.setText("Please enter a username and password.");
        }
    }

    public void validateLogin() {
        databaseConnection connectNow = new databaseConnection();
        Connection connectDB = connectNow.getConnection();
        String compareLogin = "SELECT count(1) FROM Employee WHERE Employee_Username = '" + username.getText() + "' AND Employee_Password = '" + password.getText() + "'";
        try {
            Statement queryStatement = connectDB.createStatement();
            ResultSet queryResult = queryStatement.executeQuery(compareLogin);
            while (queryResult.next()) {
                if (queryResult.getInt(1) == 1) {
                    checkAccess();
                } else {
                    wrongLogin.setText("Wrong username or password!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void checkAccess() {
        Main m = new Main();
        databaseConnection connectNow = new databaseConnection();
        Connection connectDB = connectNow.getConnection();
        String checkAccess = "SELECT * FROM Employee WHERE Employee_Username = '" + username.getText() + "' AND Employee_Password = '" + password.getText() + "'";
        try {
            Statement queryStatement = connectDB.createStatement();
            ResultSet queryCheckResult = queryStatement.executeQuery(checkAccess);
            while (queryCheckResult.next()) {
                userData.setName(queryCheckResult.getString("Employee_Name"));
                userData.setShopID(queryCheckResult.getString("Shop_ID"));
                if (Objects.equals(queryCheckResult.getString("Employee_Username"), username.getText())
                        && Objects.equals(queryCheckResult.getString("Employee_Password"), password.getText())
                        && Objects.equals(queryCheckResult.getString("Employee_AccessLevel"), "Administrator")) {
                    m.changeScene("adminTablePage.fxml");
                } else {
                    m.changeScene("regularTablePage.fxml");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }
}
