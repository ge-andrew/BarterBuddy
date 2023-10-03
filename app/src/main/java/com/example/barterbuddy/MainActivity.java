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
    private String itemId;

    // declaring temp buttons
    Button details_button;
    Button add_item_button;
    TextInputEditText usernameEditText;
    TextInputEditText emailEditText;
    TextInputEditText itemIdEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // getting temp edit text fields
        usernameEditText = findViewById(R.id.username_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        itemIdEditText = findViewById(R.id.item);

        // initializing temp buttons
        details_button = findViewById(R.id.go_to_details_button);
        details_button.setOnClickListener(
                view -> {
                    // creates an intent that switches to the ItemDetailPage activity and passes the item id
                    // to the new activity
                    username = String.valueOf(usernameEditText.getText());
                    email = String.valueOf(emailEditText.getText());
                    itemId = String.valueOf(itemIdEditText.getText());
                    Intent intent = new Intent(MainActivity.this, ItemDetailPage.class);
                    intent.putExtra("username", username);
                    intent.putExtra("email", email);
                    intent.putExtra("itemId", itemId);
                    startActivity(intent);
                });

        add_item_button = findViewById(R.id.go_to_add_item_button);
        add_item_button.setOnClickListener(
                view -> {

                    username = String.valueOf(usernameEditText.getText());
                    email = String.valueOf(emailEditText.getText());
                    itemId = String.valueOf(itemIdEditText.getText());
                    Intent intent = new Intent(MainActivity.this, AddNewItem.class);
                    intent.putExtra("username", username);
                    intent.putExtra("email", email);
                    startActivity(intent);
                });
    }
}
