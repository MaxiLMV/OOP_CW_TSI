package com.example.oop_cw_st79404;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class registerPageController implements Initializable {
    @FXML
    private Button backButton;
    @FXML
    private Button registerButton;
    @FXML
    private TextField usernameReg;
    @FXML
    private TextField nameReg;
    @FXML
    private TextField surnameReg;
    @FXML
    private PasswordField passwordReg;
    @FXML
    private PasswordField passwordRegConfirm;
    @FXML
    private PasswordField adminCode;
    @FXML
    private Label registerResult;
    @FXML
    private Label storeChoiceLabel;
    @FXML
    private ChoiceBox<String> storeChoice;

    private final String[] st = {"ST001", "ST002", "ST003", "ST004"};

    public void returnToFirst() throws IOException {
        Main m = new Main();
        m.changeScene("firstPage.fxml");
    }

    public void getStore(ActionEvent event) {
        String chosenStore = storeChoice.getValue();
        storeChoiceLabel.setText("You are registering to store " + chosenStore);
    }

    public void registerNewUser() {
        Main m = new Main();
        databaseConnection connectNow = new databaseConnection();
        Connection connectDB = connectNow.getConnection();
        String checkUsername = "SELECT count(1) FROM [dbo].[Employee] WHERE Employee_Username = '" + usernameReg.getText() + "'";
        try {
            Statement queryStatement = connectDB.createStatement();
            ResultSet queryResult = queryStatement.executeQuery(checkUsername);
            while (queryResult.next()) {
                if (queryResult.getInt(1) == 1) {
                    registerResult.setText("This username already exists!");
                } else {
                    if (!(usernameReg.getText().isEmpty()
                            || passwordReg.getText().isEmpty()
                            || passwordRegConfirm.getText().isEmpty()
                            || nameReg.getText().isEmpty()
                            || surnameReg.getText().isEmpty()
                            || storeChoice.getValue().isEmpty())) {
                        if (usernameReg.getText().length() < 3) {
                            registerResult.setText("Username too short!");
                        } else if (passwordReg.getText().length() < 6) {
                            registerResult.setText("Password too short!");
                        } else if (!(passwordReg.getText().equals(passwordRegConfirm.getText()))) {
                            registerResult.setText("Passwords do not match!");
                        } else {
                            if (adminCode.getText().equals("Administrator")) {
                                registerUser("Administrator");
                                m.changeScene("registerSuccessPage.fxml");
                            } else if (!(adminCode.getText().equals("Administrator")) && !(adminCode.getText().isEmpty())) {
                                registerResult.setText("Registration unsuccessful! Admin Code is incorrect!");
                            } else {
                                registerUser("Regular");
                                m.changeScene("registerSuccessPage.fxml");
                            }
                        }
                    } else {
                        registerResult.setText("Please fill out all fields. Admin Code can be empty.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void registerUser(String accessLevel) {
        databaseConnection connection = new databaseConnection();
        Connection connectDB = connection.getConnection();
        String insertField = "INSERT INTO Employee (Employee_Username,Employee_Password,Employee_Name,Employee_Surname,Employee_AccessLevel,Shop_ID) VALUES ";
        String insertValues = "('" + usernameReg.getText() + "','" + passwordReg.getText() + "','" + nameReg.getText() + "','" + surnameReg.getText() + "','" + accessLevel + "','" + storeChoice.getValue() + "')";
        String toInsert = insertField + insertValues;
        try {
            Statement queryStatement = connectDB.createStatement();
            queryStatement.executeUpdate(toInsert);
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        storeChoice.getItems().addAll(st);
        storeChoice.setOnAction(this::getStore);
    }
}

