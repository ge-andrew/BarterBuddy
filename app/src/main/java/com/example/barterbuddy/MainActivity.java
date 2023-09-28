package com.example.barterbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
  // declaring temp button
  Button details_button;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // initializing temp button
    details_button = findViewById(R.id.go_to_details_button);
    details_button.setOnClickListener(
        view -> {
          // creates an intent that switches to the ItemDetailPage activity and passes the item id
          // to the new activity
          Intent intent = new Intent(MainActivity.this, ItemDetailPage.class);
          intent.putExtra("item_id", "temp_value");
          startActivity(intent);
        });
  }
}
