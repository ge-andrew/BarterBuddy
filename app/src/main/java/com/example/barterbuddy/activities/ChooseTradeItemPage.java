package com.example.barterbuddy.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.barterbuddy.R;
import com.example.barterbuddy.models.Item;
import com.example.barterbuddy.models.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ChooseTradeItemPage extends AppCompatActivity {

    private Item posterItem;
    private Button add_item_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_trade_item);

        posterItem = (Item) getIntent().getSerializableExtra("posterItem");

        add_item_button = findViewById(R.id.add_item_button);

        // dummy data
        Item offeringItem = new Item();

        add_item_button.setOnClickListener(
                v -> {
                    // creates an intent that switches to the OfferTradePage activity and passes the item
                    // to the new activity
                    Intent intent = new Intent(ChooseTradeItemPage.this, AdjustTradeMoneyPage.class);
                    intent.putExtra("posterItem", posterItem);
                    intent.putExtra("offeringItem", offeringItem);
                    startActivity(intent);
                });
    }
}
