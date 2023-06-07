package com.example.oop_cw_st79404;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

import java.net.URL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class adminTablePageController implements Initializable {

    @FXML
    public Button logoutButton;
    @FXML
    public Button stockedItemsButton;
    @FXML
    public Button ordersButton;
    @FXML
    public Button itemsToOrderButton;
    @FXML
    private TableView<items> StockedItemsTable;
    @FXML
    private TableView<orders> ordersTable;
    @FXML
    private TableView<itemsToOrder> itemsToOrderTable;
    @FXML
    private TableColumn<itemsToOrder, String> orderItemID;
    @FXML
    private TableColumn<itemsToOrder, String> orderItemName;
    @FXML
    private TableColumn<itemsToOrder, Integer> orderItemQuantity;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button submitOrdersButton;
    @FXML
    private Button saveItemChangeButton;
    @FXML
    private Button saveOrderChangeButton;
    @FXML
    private Label greetingLabel;
    @FXML
    private Label storeDataView;
    @FXML
    private Label editInfoText;
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
    private TableColumn<orders, String> orderID;
    @FXML
    private TableColumn<orders, String> orderIssueDate;
    @FXML
    private TableColumn<orders, String> orderDeliveryDate;
    @FXML
    private TableColumn<orders, String> orderStatus;
    @FXML
    private TableColumn<orders, String> supplierName;
    @FXML
    private TableColumn<orders, String> supplierID;
    @FXML
    private TableColumn<orders, String> itemOrderID;
    @FXML
    private TableColumn<orders, String> itemOrderName;
    @FXML
    private TextField searchField;
    private int currentTable;
    static currentUser userData = currentUser.getInstance();
    private final String[] supplierIDs = {"SP001", "SP002", "SP003", "SP004"};
    private final ArrayList<items> itemsChanges = new ArrayList<>();
    private final ArrayList<orders> ordersChanges = new ArrayList<>();
    private final String[] statusChoices = {"Pending Approval", "Approved", "Completed", "Denied", "Cancelled"};

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

    public static ObservableList<itemsToOrder> getDataItemsToOrder() { // Function to pick up the data from the SQL Server Database
        int tempQuantity;
        databaseConnection connectNow = new databaseConnection();
        Connection connectDB = connectNow.getConnection();
        ObservableList<itemsToOrder> itemsToOrderList = FXCollections.observableArrayList();
        String itemsToOrderQuery = "SELECT StockItem_ID,StockItem_Name,StockItem_AvailableQuantity,StockItem_DesiredQuantity,(StockItem_DesiredQuantity - StockItem_AvailableQuantity) as 'Quantity Delta' FROM StockItems WHERE Shop_ID = '" + userData.getShopID() + "'";
        try {
            Statement queryStatement = connectDB.createStatement();
            ResultSet itemsToOrderQueryResult = queryStatement.executeQuery(itemsToOrderQuery);
            while (itemsToOrderQueryResult.next()) {
                if (!(itemOrdered(itemsToOrderQueryResult.getString("StockItem_ID")))) {
                    tempQuantity = Integer.parseInt(itemsToOrderQueryResult.getString("Quantity Delta"));
                    if (tempQuantity >= (Integer.parseInt(itemsToOrderQueryResult.getString("StockItem_DesiredQuantity"))/2)) {
                        itemsToOrderList.add(new itemsToOrder(
                                itemsToOrderQueryResult.getString("StockItem_ID"),
                                itemsToOrderQueryResult.getString("StockItem_Name"),
                                (int) Math.floor(tempQuantity*1.5)));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
        return itemsToOrderList;
    }

    public static Boolean itemOrdered(String itemStockID) { // Function to check if an item has already been Ordered
        databaseConnection connectNow = new databaseConnection();
        Connection connectDB = connectNow.getConnection();
        String compare = "SELECT count(1) FROM CreatedOrders WHERE StockItem_ID = '" + itemStockID + "'";
        try {
            Statement queryStatement = connectDB.createStatement();
            ResultSet queryResult = queryStatement.executeQuery(compare);
            while (queryResult.next()) {
                return queryResult.getInt(1) == 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
        return true;
    }

    public static ObservableList<orders> getDataOrders() { // Function to pick up the data from the SQL Server Database
        databaseConnection connectNow = new databaseConnection();
        Connection connectDB = connectNow.getConnection();
        ObservableList<orders> ordersList = FXCollections.observableArrayList();
        String ordersQuery = "SELECT o.Orders_ID,o.Orders_IssueDate,o.Orders_DeliveryDate,o.Status,s.Supplier_Name,o.Supplier_ID, si.StockItem_ID, si.StockItem_Name FROM Orders o INNER JOIN Supplier s ON s.Supplier_ID = o.Supplier_ID INNER JOIN CreatedOrders c ON c.Orders_ID = o.Orders_ID INNER JOIN StockItems si ON si.StockItem_ID = c.StockItem_ID WHERE o.Shop_ID = '" + userData.getShopID() + "'";
        try {
            Statement queryStatement = connectDB.createStatement();
            ResultSet ordersQueryResult = queryStatement.executeQuery(ordersQuery);
            while (ordersQueryResult.next()) {
                ordersList.add(new orders(
                        ordersQueryResult.getString("Orders_ID"),
                        ordersQueryResult.getString("Orders_IssueDate"),
                        ordersQueryResult.getString("Orders_DeliveryDate"),
                        ordersQueryResult.getString("Status"),
                        ordersQueryResult.getString("Supplier_Name"),
                        ordersQueryResult.getString("Supplier_ID"),
                        ordersQueryResult.getString("StockItem_ID"),
                        ordersQueryResult.getString("StockItem_Name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
        return ordersList;
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

    public void loadDataItemsToOrder() { // Function to load, filter and sort data from the Orders and Items bridge table
        orderItemID.setCellValueFactory(new PropertyValueFactory<>("orderItemID"));
        orderItemName.setCellValueFactory(new PropertyValueFactory<>("orderItemName"));
        orderItemQuantity.setCellValueFactory(new PropertyValueFactory<>("orderQuantity"));

        ObservableList<itemsToOrder> itemsToOrderList = getDataItemsToOrder();
        FilteredList<itemsToOrder> filteredList = new FilteredList<>(itemsToOrderList, b -> true);
        searchField.textProperty().addListener((observableValue, oldValue, newValue) -> filteredList.setPredicate(itemToOrder->{
            String lowerCases = newValue.toLowerCase();
            if (newValue.isEmpty()) {
                return true;
            } else if (itemToOrder.getOrderItemID().contains(newValue)) {
                return true;
            } else return itemToOrder.getOrderItemName().toLowerCase().contains(lowerCases);
        }));
        SortedList<itemsToOrder> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(itemsToOrderTable.comparatorProperty());
        itemsToOrderTable.setItems(sortedList);
    }

    public void loadDataOrders() { // Function to load, filter and sort data from the Orders table
        orderID.setCellValueFactory(new PropertyValueFactory<>("orderID"));
        orderIssueDate.setCellValueFactory(new PropertyValueFactory<>("orderIssue"));
        orderDeliveryDate.setCellValueFactory(new PropertyValueFactory<>("orderDelivery"));
        orderDeliveryDate.setCellFactory(TextFieldTableCell.forTableColumn());
        orderDeliveryDate.setOnEditCommit(ordersStringCellEditEvent -> {
            saveOrderChangeButton.setDisable(false);
            ordersChanges.add(ordersStringCellEditEvent.getRowValue());
            ordersChanges.get(ordersChanges.size()-1).setOrderDelivery(ordersStringCellEditEvent.getNewValue());
        });
        orderStatus.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));
        orderStatus.setCellFactory(ChoiceBoxTableCell.forTableColumn(statusChoices));
        orderStatus.setOnEditCommit(ordersStringCellEditEvent -> {
            saveOrderChangeButton.setDisable(false);
            ordersChanges.add(ordersStringCellEditEvent.getRowValue());
            ordersChanges.get(ordersChanges.size()-1).setOrderStatus(ordersStringCellEditEvent.getNewValue());
        });
        supplierName.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        supplierID.setCellValueFactory(new PropertyValueFactory<>("supplierID"));
        supplierID.setCellFactory(ChoiceBoxTableCell.forTableColumn(supplierIDs));
        supplierID.setOnEditCommit(ordersStringCellEditEvent -> {
            saveOrderChangeButton.setDisable(false);
            ordersChanges.add(ordersStringCellEditEvent.getRowValue());
            ordersChanges.get(ordersChanges.size()-1).setSupplierID(ordersStringCellEditEvent.getNewValue());
        });
        itemOrderID.setCellValueFactory(new PropertyValueFactory<>("itemID"));
        itemOrderName.setCellValueFactory(new PropertyValueFactory<>("itemName"));

        ObservableList<orders> ordersList = getDataOrders();
        FilteredList<orders> filteredList = new FilteredList<>(ordersList, b -> true);
        searchField.textProperty().addListener((observableValue, oldValue, newValue) -> filteredList.setPredicate(order->{
            String lowerCases = newValue.toLowerCase();
            if (newValue.isEmpty()) {
                return true;
            } else if (order.getOrderID().contains(newValue)) {
                return true;
            } else if (order.getOrderStatus().toLowerCase().contains(lowerCases)) {
                return true;
            } else if (order.getSupplierName().toLowerCase().contains(lowerCases)) {
                return true;
            } else if (order.getSupplierID().toLowerCase().contains(lowerCases)) {
                return true;
            } else if (order.getItemID().toLowerCase().contains(lowerCases)) {
                return true;
            } else return order.getItemName().toLowerCase().contains(lowerCases);
        }));
        SortedList<orders> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(ordersTable.comparatorProperty());
        ordersTable.setItems(sortedList);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) { // Function that runs each time adminTablePage.fxml is open
        greetingLabel.setText("Hello " + userData.getName() + "!");
        storeDataView.setText("Viewing data for store " + userData.getShopID() + ".");

        loadDataItems();
        StockedItemsTable.setEditable(true);

        loadDataItemsToOrder();

        loadDataOrders();
        ordersTable.setEditable(true);

        StockedItemsTable.setVisible(false);
        itemsToOrderTable.setVisible(false);
        ordersTable.setVisible(false);
        searchField.setVisible(false);
    }

    public String getNextOrdersID() { // Calculates the number for the next order ID
        String trialID = null, compare;
        databaseConnection connectNow = new databaseConnection();
        Connection connectDB = connectNow.getConnection();
        for (int i = 1; i < 10000; i++) {
            trialID = String.format("OR%04d", i);
            compare = "SELECT count(*) FROM Orders WHERE Orders_ID = '" + trialID + "'";
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

    public void submitOrders() { // Function for approving generated orders
        itemsToOrder orderingItems;
        for (int i = 0; i < itemsToOrderTable.getItems().size(); i++) {
            orderingItems = itemsToOrderTable.getItems().get(i);
            createOrder(orderingItems.getOrderItemID(), orderingItems.getOrderQuantity());
        }
        loadDataItemsToOrder();
        loadDataOrders();
    }

    public void createOrder(String itemID, int quantity) { // Creates a new order entry in the SQL Server Database
        LocalDate localDate = LocalDate.now().plusWeeks(2);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date1 = localDate.format(formatter);
        localDate = LocalDate.now();
        String date0 = localDate.format(formatter);
        Random random = new Random();
        int i = random.nextInt(supplierIDs.length);
        String nextID = getNextOrdersID();
        databaseConnection connection = new databaseConnection();
        Connection connectDB = connection.getConnection();
        String insertField = "INSERT INTO Orders (Orders_ID,Orders_IssueDate,Orders_DeliveryDate,Status,Supplier_ID,Shop_ID) VALUES ";
        String insertValues = "('" + nextID + "','" + date0 + "','" + date1 + "','Pending','" + supplierIDs[i] + "','" + userData.getShopID() + "')";
        String toInsert = insertField + insertValues;
        try {
            Statement queryStatement = connectDB.createStatement();
            queryStatement.executeUpdate(toInsert);
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
        insertField = "INSERT INTO CreatedOrders (Orders_ID,StockItem_ID,Quantity) VALUES ";
        insertValues = "('" + nextID + "','" + itemID + "','" + quantity + "')";
        toInsert = insertField + insertValues;
        try {
            Statement queryStatement = connectDB.createStatement();
            queryStatement.executeUpdate(toInsert);
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
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

    public void saveOrderChanges() { // Function to save changes to the Orders table made by the user
        warningTableLabel.setText("");
        updateOrders();
        loadDataOrders();
        ordersChanges.clear();
        saveOrderChangeButton.setDisable(true);
    }

    public void updateOrders() { // Validates changes and applies them in the SQL Server Database
        boolean allowed = true;
        for (orders ordersChange : ordersChanges) {
            if (ordersChange.getOrderDelivery().isEmpty()) {
                warningTableLabel.setText("Cannot save changes! Empty field detected.");
                allowed = false;
            } else {allowed = true;}
        }
        if (allowed) {
            for (orders ordersChange : ordersChanges) {
                databaseConnection connection = new databaseConnection();
                Connection connectDB = connection.getConnection();
                String toUpdate = "UPDATE Orders SET Orders_DeliveryDate = '" + ordersChange.getOrderDelivery() + "',Status = '" + ordersChange.getOrderStatus() + "',Supplier_ID = '" + ordersChange.getSupplierID() + "' WHERE Orders_ID = '" + ordersChange.getOrderID() + "' AND Shop_ID = '" + userData.getShopID() + "'";
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

    public void deleteData() { // Function for deleting data entries
        switch (currentTable) {
            case 1 -> {
                deleteItems();
                loadDataItems();
            }
            case 3 -> {
                deleteOrder();
                loadDataOrders();
            }
        }
    }

    public void deleteOrder() { // Function to delete Orders
        orders singleOrder = ordersTable.getSelectionModel().getSelectedItem();
        databaseConnection connectNow = new databaseConnection();
        Connection connectDB = connectNow.getConnection();
        String delete = "DELETE FROM CreatedOrders WHERE Orders_ID = '" + singleOrder.getOrderID() + "'";
        try {
            Statement queryStatement = connectDB.createStatement();
            queryStatement.executeUpdate(delete);
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
        delete = "DELETE FROM Orders WHERE Orders_ID = '" + singleOrder.getOrderID() + "' AND Shop_ID = '" + userData.getShopID() + "'";
        try {
            Statement queryStatement = connectDB.createStatement();
            queryStatement.executeUpdate(delete);
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void deleteItems() { // Function to delete Item listings and everything related to them
        items singleItem = StockedItemsTable.getSelectionModel().getSelectedItem();
        ArrayList<String> orderIDToDelete = new ArrayList<>();
        String delete;
        databaseConnection connectNow = new databaseConnection();
        Connection connectDB = connectNow.getConnection();
        String compare = "SELECT Orders_ID FROM CreatedOrders WHERE StockItem_ID = '" + singleItem.getID() + "'";
        try {
            Statement queryStatement = connectDB.createStatement();
            ResultSet queryResult = queryStatement.executeQuery(compare);
            while (queryResult.next()) {
                orderIDToDelete.add(queryResult.getString("Orders_ID"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
        delete = "DELETE FROM CreatedOrders WHERE StockItem_ID = '" + singleItem.getID() + "'";
        try {
            Statement queryStatement = connectDB.createStatement();
            queryStatement.executeUpdate(delete);
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
        for (String s : orderIDToDelete) {
            delete = "DELETE FROM Orders WHERE Orders_ID = '" + s + "' AND Shop_ID = '" + userData.getShopID() + "'";
            try {
                Statement queryStatement = connectDB.createStatement();
                queryStatement.executeUpdate(delete);
            } catch (Exception e) {
                e.printStackTrace();
                e.getCause();
            }
        }
        delete = "DELETE FROM StockItems WHERE StockItem_ID = '" + singleItem.getID() + "' AND Shop_ID = '" + userData.getShopID() + "'";
        try {
            Statement queryStatement = connectDB.createStatement();
            queryStatement.executeUpdate(delete);
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void switchToTable1() { // Function for switching the main focus to table 1
        addButton.setDisable(false);
        deleteButton.setDisable(false);
        itemsToOrderTable.setVisible(false);
        StockedItemsTable.setVisible(true);
        ordersTable.setVisible(false);
        submitOrdersButton.setVisible(false);
        saveItemChangeButton.setVisible(true);
        saveOrderChangeButton.setVisible(false);
        searchField.setVisible(true);
        searchField.setPromptText("Search (Item ID, Name, Type, Price)");
        editInfoText.setVisible(true);
        currentTable = 1;
        warningTableLabel.setText("");
        loadDataItems();
    }

    public void switchToTable2() { // Function for switching the main focus to table 2
        addButton.setDisable(true);
        deleteButton.setDisable(true);
        itemsToOrderTable.setVisible(true);
        StockedItemsTable.setVisible(false);
        ordersTable.setVisible(false);
        submitOrdersButton.setVisible(true);
        saveItemChangeButton.setVisible(false);
        saveOrderChangeButton.setVisible(false);
        searchField.setVisible(true);
        searchField.setPromptText("Search (Item ID, Name)");
        editInfoText.setVisible(false);
        currentTable = 2;
        warningTableLabel.setText("");
        loadDataItemsToOrder();
    }

    public void switchToTable3() { // Function for switching the main focus to table 3
        addButton.setDisable(true);
        deleteButton.setDisable(false);
        itemsToOrderTable.setVisible(false);
        StockedItemsTable.setVisible(false);
        ordersTable.setVisible(true);
        submitOrdersButton.setVisible(false);
        saveItemChangeButton.setVisible(false);
        saveOrderChangeButton.setVisible(true);
        searchField.setVisible(true);
        searchField.setPromptText("Search (Order ID, Status, Supplier Name, Supplier ID, Item ID, Item Name)");
        editInfoText.setVisible(true);
        currentTable = 3;
        warningTableLabel.setText("");
        loadDataOrders();
    }
}