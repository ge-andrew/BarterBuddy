package com.example.barterbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class ItemDetailPage extends AppCompatActivity {
    private TextView item_title;
    private TextView username;
    private TextView item_description;
    private Button offer_trade_button;
    private String item_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail_page);

        // getting item id from recycler view
        item_id = getIntent().getStringExtra("item_id");

        // initializing views and buttons
        username = findViewById(R.id.username_text_view);
        item_title = findViewById(R.id.item_title_text_view);
        item_description = findViewById(R.id.description_text_view);
        offer_trade_button = findViewById(R.id.offer_trade_button);
    }
}