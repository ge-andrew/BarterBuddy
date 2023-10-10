package com.example.barterbuddy.Models;

import com.example.barterbuddy.Item;

public class RecyclerItemModel extends Item {
    String item_name;
    String description;
    String poster;
    Item recycleItem = new Item();
    //int price;

    public RecyclerItemModel() {
        this.item_name = recycleItem.getTitle();
        this.description = recycleItem.getDescription();
        this.poster = recycleItem.getEmail();
        //this.price = price;
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

   /* public int getPrice() {
        return price;
    }*/
}