package com.example.barterbuddy.Models;

public class RecyclerItemModel {
    String item_name;
    String description;
    String poster;
    int price;

    public RecyclerItemModel(String item_name, String description, String poster, int price) {
        this.item_name = item_name;
        this.description = description;
        this.poster = poster;
        this.price = price;
    }

    public String getItem_name() {
        return item_name;
    }

    public String getDescription() {
        return description;
    }

    public String getPoster() {
        return poster;
    }

    public int getPrice() {
        return price;
    }
}
