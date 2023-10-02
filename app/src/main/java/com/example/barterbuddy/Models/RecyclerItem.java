package com.example.barterbuddy.Models;

import com.example.barterbuddy.Item;

public class RecyclerItem extends Item {
    String item_name = getTitle();
    String description = getDescription();
    String poster;
    int price;
}
