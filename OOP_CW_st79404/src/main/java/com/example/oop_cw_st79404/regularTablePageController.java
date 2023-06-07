package com.example.oop_cw_st79404;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class regularTablePageController implements Initializable {

    @FXML
    public Label editInfoText;
    @FXML
    public Button addButton;
    @FXML
    public Button logoutButton;
    @FXML
    private TableView<items> StockedItemsTable;
    @FXML
    private Button saveItemChangeButton;
    @FXML
    private Label greetingLabel;
    @FXML
    private Label storeDataView;
    @FXML
    private Label warningTableLabel;
    @FXML
    private TableColumn<items, Integer> itemDesiredQuantity;
    @FXML
    private TableColumn<items, Integer> itemQuantity;
    @FXML
    private TableColumn<items, String> itemID;
    @FXML
    private TableColumn<items, String> itemName;
    @FXML
    private TableColumn<items, String> itemType;
    @FXML
    private TableColumn<items, Float> itemPrice;
    @FXML
    private TextField searchField;
    static currentUser userData = currentUser.getInstance();
    private final ArrayList<items> itemsChanges = new ArrayList<>();

    public static ObservableList<items> getDataItems() { // Function to pick up the data from the SQL Server Database
        String num;
        databaseConnection connectNow = new databaseConnection();
        Connection connectDB = connectNow.getConnection();
        ObservableList<items> itemsList = FXCollections.observableArrayList();
        String itemsQuery = "SELECT * FROM StockItems WHERE Shop_ID = '" + userData.getShopID() + "'";
        try {
            Statement queryStatement = connectDB.createStatement();
            ResultSet itemsQueryResult = queryStatement.executeQuery(itemsQuery);
            while (itemsQueryResult.next()) {
                num = String.format("%.2f", itemsQueryResult.getFloat("StockItem_Price"));
                itemsList.add(new items(
                        itemsQueryResult.getString("StockItem_ID"),
                        itemsQueryResult.getString("StockItem_Name"),
                        itemsQueryResult.getString("StockItem_Type"),
                        itemsQueryResult.getInt("StockItem_AvailableQuantity"),
                        itemsQueryResult.getInt("StockItem_DesiredQuantity"),
                        Float.parseFloat(num)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
        return itemsList;
    }

    public void loadDataItems() { // Function to load, filter and sort data from the Items table
        itemID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        itemName.setCellValueFactory(new PropertyValueFactory<>("name"));
        itemName.setCellFactory(TextFieldTableCell.forTableColumn());
        itemName.setOnEditCommit(itemsStringCellEditEvent -> {
            saveItemChangeButton.setDisable(false);
            itemsChanges.add(itemsStringCellEditEvent.getRowValue());
            itemsChanges.get(itemsChanges.size()-1).setName(itemsStringCellEditEvent.getNewValue());
        });
        itemType.setCellValueFactory(new PropertyValueFactory<>("type"));
        itemType.setCellFactory(TextFieldTableCell.forTableColumn());
        itemType.setOnEditCommit(itemsStringCellEditEvent -> {
            saveItemChangeButton.setDisable(false);
            itemsChanges.add(itemsStringCellEditEvent.getRowValue());
            itemsChanges.get(itemsChanges.size()-1).setType(itemsStringCellEditEvent.getNewValue());
        });
        itemQuantity.setCellValueFactory(new PropertyValueFactory<>("available"));
        itemQuantity.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        itemQuantity.setOnEditCommit(itemsIntegerCellEditEvent -> {
            saveItemChangeButton.setDisable(false);
            itemsChanges.add(itemsIntegerCellEditEvent.getRowValue());
            itemsChanges.get(itemsChanges.size() - 1).setAvailable(itemsIntegerCellEditEvent.getNewValue());
        });
        itemDesiredQuantity.setCellValueFactory(new PropertyValueFactory<>("desired"));
        itemDesiredQuantity.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        itemDesiredQuantity.setOnEditCommit(itemsIntegerCellEditEvent -> {
            saveItemChangeButton.setDisable(false);
            itemsChanges.add(itemsIntegerCellEditEvent.getRowValue());
            itemsChanges.get(itemsChanges.size()-1).setDesired(itemsIntegerCellEditEvent.getNewValue());
        });
        itemPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        itemPrice.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));
        itemPrice.setOnEditCommit(itemsFloatCellEditEvent -> {
            saveItemChangeButton.setDisable(false);
            itemsChanges.add(itemsFloatCellEditEvent.getRowValue());
            itemsChanges.get(itemsChanges.size()-1).setPrice(itemsFloatCellEditEvent.getNewValue());
        });
        ObservableList<items> itemsList = getDataItems();
        FilteredList<items> filteredList = new FilteredList<>(itemsList, b -> true);
        searchField.textProperty().addListener((observableValue, oldValue, newValue) -> filteredList.setPredicate(item->{
            String lowerCases = newValue.toLowerCase();
            if (newValue.isEmpty()) {
                return true;
            } else if (item.getID().contains(newValue)) {
                return true;
            } else if (item.getName().toLowerCase().contains(lowerCases)) {
                return true;
            } else if (item.getType().toLowerCase().contains(lowerCases)) {
                return true;
            } else return Float.toString(item.getPrice()).contains(newValue);
        }));
        SortedList<items> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(StockedItemsTable.comparatorProperty());
        StockedItemsTable.setItems(sortedList);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) { // Function that runs each time adminTablePage.fxml is open
        greetingLabel.setText("Hello " + userData.getName() + "!");
        storeDataView.setText("Viewing data for store " + userData.getShopID() + ".");

        loadDataItems();
        StockedItemsTable.setEditable(true);
    }

    public void moveToFirstPage() { // Log Out button
        try {
            Main m = new Main();
            m.changeScene("firstPage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void saveItemChanges() { // Function to save changes to the Stock Items table made by the user
        warningTableLabel.setText("");
        updateStockItems();
        loadDataItems();
        itemsChanges.clear();
        saveItemChangeButton.setDisable(true);
    }

    public void updateStockItems() { // Validates changes and applies them in the SQL Server Database
        boolean allowed = true;
        for (items itemsChange : itemsChanges) {
            if (itemsChange.getName().isEmpty()
                    || itemsChange.getType().isEmpty()
                    || itemsChange.getAvailable() == 0
                    || itemsChange.getDesired() == 0
                    || itemsChange.getPrice() == 0) {
                warningTableLabel.setText("Cannot save changes! Empty field detected.");
                allowed = false;
            } else {
                    databaseConnection connectNow = new databaseConnection();
                    Connection connectDB = connectNow.getConnection();
                    String compare = "SELECT count(1) FROM StockItems WHERE StockItem_Name = '" + itemsChange.getName() + "' AND Shop_ID = '" + userData.getShopID() + "'";
                    try {
                        Statement queryStatement = connectDB.createStatement();
                        ResultSet queryResult = queryStatement.executeQuery(compare);
                        while (queryResult.next()) {
                            if (queryResult.getInt(1) == 1) {
                                warningTableLabel.setText("This item is already added to this shop.");
                                allowed = false;
                            } else {
                                allowed = true;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        e.getCause();
                    }
                }
        }
        if (allowed) {
           for (items itemsChange : itemsChanges) {
               databaseConnection connection = new databaseConnection();
               Connection connectDB = connection.getConnection();
               String toUpdate = "UPDATE StockItems SET StockItem_Name = '" + itemsChange.getName() + "',StockItem_Type = '" + itemsChange.getType() + "',StockItem_AvailableQuantity = '" + itemsChange.getAvailable() + "',StockItem_DesiredQuantity = '" + itemsChange.getDesired() + "',StockItem_Price = '" + itemsChange.getPrice() + "' WHERE StockItem_ID = '" + itemsChange.getID() + "' AND Shop_ID = '" + userData.getShopID() + "'";
               try {
                   Statement queryStatement = connectDB.createStatement();
                   queryStatement.executeUpdate(toUpdate);
               } catch (Exception e) {
                   e.printStackTrace();
                   e.getCause();
               }
           }
        }
    }
    public void addData() { // Function for adding data entries
        try {
            Main m = new Main();
            m.changeScene("adminAddingPage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
        loadDataItems();
    }
}