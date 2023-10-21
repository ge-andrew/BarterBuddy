package com.example.barterbuddy.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.barterbuddy.R;

public class IncomingOffersPage extends AppCompatActivity {
    private Button accept_offers_button;
    private Button decline_offers_button;
    private ImageView offeredItemImage;
    private ImageView wantedItemImage;
    private TextView offeredTrade;
    private TextView wantedTrade;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    setContentView(R.layout.offers_hub_page);
    decline_offers_button = findViewById(R.id.decline_trade_button);
    accept_offers_button = findViewById(R.id.accept_trade_button);

    }
}

