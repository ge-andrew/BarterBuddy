package com.example.barterbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barterbuddy.Models.RecyclerItemModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
  // for LogCat
  String TAG = "MainActivity";

  // declaring temp buttons
  Button details_button;
  Button view_my_items_button;
  Button set_active_button;
  Button recycler_view_button;
  TextInputEditText itemIdEditText;
  // declaring temp itemId (for testing and demonstration)
  private String username;
  private String email;
  private String itemId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    // getting username and email from previous activity
    username = getIntent().getStringExtra("username");
    email = getIntent().getStringExtra("email");


    // setting up user items button
    view_my_items_button = findViewById(R.id.go_to_my_items);
    view_my_items_button.setOnClickListener(
        view -> {
          Intent intent = new Intent(MainActivity.this, UserItemsPage.class);
          intent.putExtra("username", username);
          intent.putExtra("email", email);
          startActivity(intent);
        });

    // setting up public market button
    recycler_view_button = findViewById(R.id.recycler_view_button);
    recycler_view_button.setOnClickListener(
            view -> {

                Intent intent = new Intent(MainActivity.this, ItemsAvailablePage.class);
                intent.putExtra("username", username);
                intent.putExtra("email", email);
                intent.putExtra("itemId", itemId);
                startActivity(intent);
            }
    );


  }
}
