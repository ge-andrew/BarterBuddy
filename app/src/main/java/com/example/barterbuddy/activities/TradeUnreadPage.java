package com.example.barterbuddy.activities;

import static com.example.barterbuddy.network.UpdateTradeDocument.setStateToCanceled;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.barterbuddy.R;
import com.example.barterbuddy.models.Trade;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class TradeUnreadPage extends AppCompatActivity {
  private final FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();
  private final FirebaseFirestore FIRESTORE_INSTANCE = FirebaseFirestore.getInstance();
  private FirebaseUser currentUser;
  private String username;
  private String email;
  private Button cancel_button;
  private Trade trade;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_trade_unread_page);

    trade =
        new Trade(
            getIntent().getStringExtra("posterEmail"),
            FIRESTORE_INSTANCE.document(getIntent().getStringExtra("posterItem")),
            getIntent().getStringExtra("offeringEmail"),
            FIRESTORE_INSTANCE.document(getIntent().getStringExtra("offeringItem")),
            getIntent().getDoubleExtra("money", -1),
            getIntent().getStringExtra("stateOfCompletion"));

    getCurrentUser();
    if (currentUser == null) {
      goToLoginPage();
    }
    getCurrentUserInfo();
    getXmlElements();

    cancel_button.setOnClickListener(
        v -> {
          setStateToCanceled(trade);
          Toast.makeText(getApplicationContext(), "Trade canceled", Toast.LENGTH_SHORT).show();
          finish();
        });
  }

  private void getCurrentUser() {
    currentUser = AUTHENTICATION_INSTANCE.getCurrentUser();
  }

  private void getCurrentUserInfo() {
    username = currentUser.getDisplayName();
    email = currentUser.getEmail();
  }

  private void goToLoginPage() {
    Intent intent = new Intent(getApplicationContext(), LoginPage.class);
    startActivity(intent);
    finish();
  }

  private void getXmlElements() {
    cancel_button = findViewById(R.id.cancel_button);
  }
}
