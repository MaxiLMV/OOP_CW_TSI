package com.example.oop_cw_st79404;

public class currentUser {

    private static final currentUser instance = new currentUser();

    private String name, shopID;

    private currentUser(){}

    public static currentUser getInstance() {
        return instance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }
}
