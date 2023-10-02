package com.example.barterbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    // declaring temp itemId (for testing and demonstration)
    private static final String sampleItemId = "2KQyKs0TWNc4ABmev3IP";
    private static final String samplePosterId = "lRpydQcIPq4bIo1cvcl4";
    private String username;
    private String email;

    // declaring temp buttons
    Button details_button;
    Button add_item_button;
    TextInputEditText usernameEditText;
    TextInputEditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // getting temp edit text fields
        usernameEditText = findViewById(R.id.username_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);

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

                    username = String.valueOf(usernameEditText.getText());
                    email = String.valueOf(emailEditText.getText());
                    Intent intent = new Intent(MainActivity.this, AddNewItem.class);
                    intent.putExtra("username", username);
                    intent.putExtra("email", email);
                    startActivity(intent);
                });
    }
}
