package com.example.oop_cw_st79404;

public class itemsToOrder {
    String orderItemID, orderItemName;
    int orderQuantity;

    public itemsToOrder(String orderItemID, String orderItemName, int orderQuantity) {
        this.orderItemID = orderItemID;
        this.orderItemName = orderItemName;
        this.orderQuantity = orderQuantity;
    }

    public void setOrderItemID(String orderItemID) {
        this.orderItemID = orderItemID;
    }

    public void setOrderItemName(String orderItemName) {
        this.orderItemName = orderItemName;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public String getOrderItemID() {
        return orderItemID;
    }

    public String getOrderItemName() {
        return orderItemName;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }
}
