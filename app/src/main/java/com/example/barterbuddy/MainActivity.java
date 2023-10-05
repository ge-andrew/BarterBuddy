package com.example.barterbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    // declaring temp itemId (for testing and demonstration)
    private String username;
    private String email;

    TextInputEditText itemIdEditText;

    private String itemId;

    // declaring temp buttons
    Button details_button;
    Button add_item_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // getting username and email from previous activity
        username = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("email");

        // setting up item id text field
        itemIdEditText = findViewById(R.id.item);
        itemIdEditText.setText(email + "-");

        // setting up details button
        details_button = findViewById(R.id.go_to_details_button);
        details_button.setOnClickListener(
                view -> {
                    itemId = String.valueOf(itemIdEditText.getText());
                    // creating intent to open ItemDetailPage activity
                    Intent intent = new Intent(MainActivity.this, ItemDetailPage.class);
                    intent.putExtra("username", username);
                    intent.putExtra("email", email);
                    intent.putExtra("itemId", itemId);
                    startActivity(intent);
                });

        // setting up add item button
        add_item_button = findViewById(R.id.go_to_add_item_button);
        add_item_button.setOnClickListener(
                view -> {
                    // creating intent to open AddNewItem activity
                    Intent intent = new Intent(MainActivity.this, AddNewItem.class);
                    intent.putExtra("username", username);
                    intent.putExtra("email", email);
                    startActivity(intent);
                });
    }
}
