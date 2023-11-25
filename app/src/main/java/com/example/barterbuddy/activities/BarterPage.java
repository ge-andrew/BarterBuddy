package com.example.barterbuddy.activities;

import static com.example.barterbuddy.network.UpdateTradeDocument.setStateToCanceled;
import static com.example.barterbuddy.network.UpdateTradeDocument.setStateToChatting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.barterbuddy.R;
import com.example.barterbuddy.models.Item;
import com.example.barterbuddy.models.Trade;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.text.DecimalFormat;

public class BarterPage extends AppCompatActivity {

  private static final String TAG = "BarterPage"; // for logging from this activity
  final long FIVE_MEGABYTES = 1024 * 1024 * 5;
  private final FirebaseFirestore DB = FirebaseFirestore.getInstance();
  private final FirebaseStorage IMAGE_STORAGE = FirebaseStorage.getInstance();
  private final FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();
  private String username;
  private String email;
  private FirebaseUser currentUser;
  private boolean isPoster;
  private Trade trade;
  private Item posterItem;
  private Item offeringItem;
  private View includedLayout;
  private ImageView posterImageView;
  private ImageView offeringImageView;
  private Button lock_in_button;
  private Button withdraw_button;
  private TextView posterItemTitle;
  private TextView offeringItemTitle;
  private TextInputEditText posterMoneyField;
  private TextInputEditText offeringMoneyField;
  private final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("0.00");

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_barter_page);

    isPoster = getIntent().getBooleanExtra("isPoster", false);

    getCurrentUser();
    if (currentUser == null) {
      goToLoginPage();
    }
    getCurrentUserInfo();

    getXmlElements();

    loadPage();
  }

  private void loadPage() {
    getTradeData();
  }

  private void getTradeData() {
    String fieldName;
    if (isPoster) {
      fieldName = "posterEmail";
    } else {
      fieldName = "offeringEmail";
    }
    DB.collection("trades")
        .whereEqualTo(fieldName, email)
        .whereEqualTo("stateOfCompletion", "BARTERING")
        .get()
        .addOnCompleteListener(
                task -> {
                  if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                      Log.d(TAG, "Document info is: " + document.getData());
                      trade = document.toObject(Trade.class);

                      getPosterItemData();
                    }
                  } else {
                    Log.d(TAG, "Error getting trade info ", task.getException());
                  }
                })
            .addOnFailureListener(
                    task -> {
                        Log.d(TAG, "Error getting trade info");
                    }
            );
  }

  private void getPosterItemData() {
    DocumentReference posterItemDocRef = trade.getPosterItem();

    posterItemDocRef
        .get()
        .addOnCompleteListener(
                task -> {
                  if (task.isSuccessful()) {
                    posterItem = task.getResult().toObject(Item.class);

                    getOfferingItemData();

                  } else {
                    Log.d(TAG, "Error getting poster item ", task.getException());
                  }
                });
  }

  private void getOfferingItemData() {
    DocumentReference offeringItemDocRef = trade.getOfferingItem();

    offeringItemDocRef
        .get()
        .addOnCompleteListener(
                task -> {
                  if (task.isSuccessful()) {
                    offeringItem = task.getResult().toObject(Item.class);

                    getPosterImage();

                  } else {
                    Log.d(TAG, "Error getting offering item ", task.getException());
                  }
                });
  }

  private void getPosterImage() {
    String imageUrl = "users/" + posterItem.getEmail() + "/" + posterItem.getImageId() + ".jpg";
    StorageReference posterImageRef = IMAGE_STORAGE.getReference().child(imageUrl);

    posterImageRef
        .getBytes(FIVE_MEGABYTES)
        .addOnSuccessListener(
            bytes -> {
              Bitmap itemImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
              posterImageView.setImageBitmap(itemImage);

              getOfferingImage();
            });
  }

  private void getOfferingImage() {
    String imageUrl = "users/" + offeringItem.getEmail() + "/" + offeringItem.getImageId() + ".jpg";
    StorageReference offeringImageRef = IMAGE_STORAGE.getReference().child(imageUrl);

    offeringImageRef
        .getBytes(FIVE_MEGABYTES)
        .addOnSuccessListener(
            bytes -> {
              Bitmap itemImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
              offeringImageView.setImageBitmap(itemImage);

              insertAssets();
            });
  }

  private void insertAssets() {
    posterItemTitle.setText(posterItem.getTitle());
    offeringItemTitle.setText(offeringItem.getTitle());
    if (trade.getMoney() > 0) {
      offeringMoneyField.setText("$" + CURRENCY_FORMAT.format(trade.getMoney()));
    } else if (trade.getMoney() < 0) {
      posterMoneyField.setText("$" + CURRENCY_FORMAT.format(trade.getMoney() * -1));
    }

    configurePageToUser();
  }

  private void configurePageToUser() {
    withdraw_button.setOnClickListener(
        v -> {
          setStateToCanceled(trade);
          Toast.makeText(this, "Withdrew from trade", Toast.LENGTH_SHORT).show();
          finish();
        });
    if (true) { // TODO: true when user's turn to counteroffer
      allowCounterOffers();
    }
  }

  private void getCurrentUser() {
    currentUser = AUTHENTICATION_INSTANCE.getCurrentUser();
  }

  private void goToLoginPage() {
    Intent intent = new Intent(getApplicationContext(), LoginPage.class);
    startActivity(intent);
    finish();
  }

  private void getCurrentUserInfo() {
    username = currentUser.getDisplayName();
    email = currentUser.getEmail();
  }

  private void getXmlElements() {
    includedLayout = findViewById(R.id.included_layout);
    lock_in_button = includedLayout.findViewById(R.id.accept_button);
    withdraw_button = includedLayout.findViewById(R.id.decline_button);
    posterImageView = includedLayout.findViewById(R.id.poster_item_image);
    posterItemTitle = includedLayout.findViewById(R.id.posterItemTitle);
    posterMoneyField = includedLayout.findViewById(R.id.poster_trade_money);
    offeringImageView = includedLayout.findViewById(R.id.offering_item_image);
    offeringItemTitle = includedLayout.findViewById(R.id.offeringItemTitle);
    offeringMoneyField = includedLayout.findViewById(R.id.offering_trade_money);
  }

  private void allowCounterOffers() {
    posterMoneyField.setFocusable(true);
    offeringMoneyField.setFocusable(true);

    lock_in_button.setOnClickListener(
        v -> {
          Intent intent = new Intent(BarterPage.this, ChatPage.class);
          String otherUserEmail;
          if (isPoster) {
            otherUserEmail = offeringItem.getEmail();
          } else {
            otherUserEmail = posterItem.getEmail();
          }
          intent.putExtra("isPoster", isPoster);
          intent.putExtra("otherUserEmail", otherUserEmail);
          setStateToChatting(trade);
          startActivity(intent);
          finish();
        });
  }
}
