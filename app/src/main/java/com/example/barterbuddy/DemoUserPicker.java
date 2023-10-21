package com.example.barterbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.barterbuddy.activities.ItemsAvailablePage;
import com.example.barterbuddy.activities.LoginPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DemoUserPicker extends AppCompatActivity {

  private final String DANIEL = "Daniel";
  private final String DANIEL_EMAIL = "daniel@google.com";
  private final String ANDREW = "Andrew";
  private final String ANDREW_EMAIL = "andrew@google.com";
  private final String SKYLER = "Skyler";
  private final String SKYLER_EMAIL = "skyler@google.com";
  private final String MATT = "Matt";
  private final String MATT_EMAIL = "matt@google.com";

  Button andrewButton;
  Button mattButton;
  Button skylerButton;
  Button danielButton;

  FirebaseUser user;
  FirebaseAuth auth;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_demo_user_picker);

    andrewButton = findViewById(R.id.Andrew);
    mattButton = findViewById(R.id.Matt);
    skylerButton = findViewById(R.id.Skyler);
    danielButton = findViewById(R.id.Daniel);

    auth = FirebaseAuth.getInstance();
    user = auth.getCurrentUser();

    if (user == null) {
      Intent intent = new Intent(getApplicationContext(), LoginPage.class);
      startActivity(intent);
      finish();
    }

    andrewButton.setOnClickListener(
        view -> {
          Intent intent = new Intent(DemoUserPicker.this, ItemsAvailablePage.class);
          intent.putExtra("username", ANDREW);
          intent.putExtra("email", ANDREW_EMAIL);
          startActivity(intent);
        });

    mattButton.setOnClickListener(
        view -> {
          Intent intent = new Intent(DemoUserPicker.this, ItemsAvailablePage.class);
          intent.putExtra("username", MATT);
          intent.putExtra("email", MATT_EMAIL);
          startActivity(intent);
        });

    skylerButton.setOnClickListener(
        view -> {
          Intent intent = new Intent(DemoUserPicker.this, ItemsAvailablePage.class);
          intent.putExtra("username", SKYLER);
          intent.putExtra("email", SKYLER_EMAIL);
          startActivity(intent);
        });

    danielButton.setOnClickListener(
        view -> {
          Intent intent = new Intent(DemoUserPicker.this, ItemsAvailablePage.class);
          intent.putExtra("username", DANIEL);
          intent.putExtra("email", DANIEL_EMAIL);
          startActivity(intent);
        });
  }
}
