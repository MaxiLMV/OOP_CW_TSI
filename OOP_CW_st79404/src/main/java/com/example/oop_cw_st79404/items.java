package com.example.oop_cw_st79404;

public class items {

    String ID, name, type;
    int available, desired;
    float price;

    public items(String ID, String name, String type, int available, int desired, float price) {
        this.ID = ID;
        this.name = name;
        this.type = type;
        this.available = available;
        this.desired = desired;
        this.price = price;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getDesired() {
        return desired;
    }

    public void setDesired(int desired) {
        this.desired = desired;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
