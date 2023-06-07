package com.example.oop_cw_st79404;

public class orders {
    String orderID, orderIssue, orderDelivery, orderStatus, supplierName, supplierID, itemID, itemName;

    public orders(String orderID, String orderIssue, String orderDelivery, String orderStatus, String supplierName, String supplierID, String itemID, String itemName) {
        this.orderID = orderID;
        this.orderIssue = orderIssue;
        this.orderDelivery = orderDelivery;
        this.orderStatus = orderStatus;
        this.supplierName = supplierName;
        this.supplierID = supplierID;
        this.itemID = itemID;
        this.itemName = itemName;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOrderIssue() {
        return orderIssue;
    }

    public void setOrderIssue(String orderIssue) {
        this.orderIssue = orderIssue;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getOrderDelivery() {
        return orderDelivery;
    }

    public void setOrderDelivery(String orderDelivery) {
        this.orderDelivery = orderDelivery;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getItemID() {
        return itemID;
    }
    public String getItemName() {
        return itemName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }
}
