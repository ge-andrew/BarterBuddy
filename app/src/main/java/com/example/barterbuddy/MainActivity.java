package com.example.barterbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    
  // declaring temp itemId (for testing and demonstration)
  private static final String sampleItemId = "2KQyKs0TWNc4ABmev3IP";
  private static final String samplePosterId = "lRpydQcIPq4bIo1cvcl4";

  // declaring temp buttons
  Button details_button;
  Button add_item_button;
  Button view_my_items_button;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // initializing temp buttons
    details_button = findViewById(R.id.go_to_details_button);
    details_button.setOnClickListener(
        view -> {
          // creates an intent that switches to the ItemDetailPage activity and passes the item id
          // to the new activity
          Intent intent = new Intent(MainActivity.this, ItemDetailPage.class);
          intent.putExtra("item_id", sampleItemId);
          intent.putExtra("poster_id", samplePosterId);
          startActivity(intent);
        });

    add_item_button = findViewById(R.id.go_to_add_item_button);
    add_item_button.setOnClickListener(
        view -> {
          Intent intent = new Intent(MainActivity.this, AddNewItem.class);
          intent.putExtra("user_id", "temp_value");
          startActivity(intent);
        });
      add_item_button = findViewById(R.id.go_to_my_items);
      add_item_button.setOnClickListener(
              view -> {
                  Intent intent = new Intent(MainActivity.this, UserItemsPage.class);
                  startActivity(intent);
              });
  }
}
