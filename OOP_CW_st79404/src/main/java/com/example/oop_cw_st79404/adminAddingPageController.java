package com.example.oop_cw_st79404;

import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class adminAddingPageController {

    static currentUser userData = currentUser.getInstance();
    @FXML
    public Button cancelButton;
    @FXML
    public Button addItemsButton;
    @FXML
    private TextField availableAdd, desiredAdd, itemNameAdd, itemTypeAdd, priceAdd;
    @FXML
    private Label warningLabel;

    public void addItems() {
        if (!(itemNameAdd.getText().isEmpty()
                || itemTypeAdd.getText().isEmpty()
                || availableAdd.getText().isEmpty()
                || desiredAdd.getText().isEmpty()
                || priceAdd.getText().isEmpty())) {
            databaseConnection connectNow = new databaseConnection();
            Connection connectDB = connectNow.getConnection();
            String compare = "SELECT count(1) FROM StockItems WHERE StockItem_Name = '" + itemNameAdd.getText() + "' AND Shop_ID = '" + userData.getShopID() + "'";
            try {
                Statement queryStatement = connectDB.createStatement();
                ResultSet queryResult = queryStatement.executeQuery(compare);
                while (queryResult.next()) {
                    if (queryResult.getInt(1) == 1) {
                        warningLabel.setText("This item is already added to this shop.");
                    } else {
                        approvedAdd();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                e.getCause();
            }

        } else {
            warningLabel.setText("Please fill out all fields to add items.");
        }
    }

    public void goBack() {
        try {
            Main m = new Main();
            m.changeScene("adminTablePage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public String getNextID() {
        String trialID = null, compare;
        databaseConnection connectNow = new databaseConnection();
        Connection connectDB = connectNow.getConnection();
        for (int i = 1; i < 100000; i++) {
            trialID = String.format("IT%05d", i);
            compare = "SELECT count(*) FROM StockItems WHERE StockItem_ID = '" + trialID + "'";
            try {
                Statement queryStatement = connectDB.createStatement();
                ResultSet queryResult = queryStatement.executeQuery(compare);
                while (queryResult.next()) {
                    if (queryResult.getInt(1) == 0) {
                        return trialID;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                e.getCause();
            }
        }
        return trialID;
    }

    public void approvedAdd() {
        databaseConnection connection = new databaseConnection();
        Connection connectDB = connection.getConnection();
        String insertField = "INSERT INTO StockItems (StockItem_ID,StockItem_Name,StockItem_Type,StockItem_AvailableQuantity,StockItem_DesiredQuantity,StockItem_Price,Shop_ID) VALUES ";
        String insertValues = "('" + getNextID() + "','" + itemNameAdd.getText() + "','" + itemTypeAdd.getText() + "','" + availableAdd.getText() + "','" + desiredAdd.getText() + "','" + priceAdd.getText() + "','" + userData.getShopID() + "')";
        String toInsert = insertField + insertValues;
        try {
            Statement queryStatement = connectDB.createStatement();
            queryStatement.executeUpdate(toInsert);
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
        try {
            Main m = new Main();
            m.changeScene("adminTablePage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }
}
