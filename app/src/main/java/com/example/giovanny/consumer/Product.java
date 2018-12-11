package com.example.giovanny.consumer;

public class Product {
    private String name,uidItem;
    private int amount;

    public Product(String uidItem ,String name, int amount) {
        this.name = name;
        this.amount = amount;
        this.uidItem = uidItem;
    }

    public Product() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public String getUidItem() {
        return uidItem;
    }

    public void setUidItem(String uidItem) {
        this.uidItem = uidItem;
    }

    public void setAmount(int amount) {
        this.amount = amount;

    }
}
