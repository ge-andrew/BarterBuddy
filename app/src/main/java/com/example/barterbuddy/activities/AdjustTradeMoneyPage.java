package com.example.barterbuddy.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.barterbuddy.R;
import com.example.barterbuddy.models.Item;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AdjustTradeMoneyPage extends AppCompatActivity {

  private static final String TAG = "AdjustTradeMoneyPage";
  final long FIVE_MEGABYTES = 1024 * 1024 * 5;
  private final FirebaseFirestore DB = FirebaseFirestore.getInstance();
  private final FirebaseStorage IMAGE_STORAGE = FirebaseStorage.getInstance();
  private final FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();
  private FirebaseUser currentUser;
  private Item offeringItem;
  private Item posterItem;
  private ShapeableImageView offeringItemImageView;
  private ShapeableImageView posterItemImageView;
  private TextView offeringItemTitle;
  private TextView posterItemTitle;
  private TextView posterUsername;
  private ImageView backArrow;
  private TextInputEditText offeringItemMoneyField;
  private TextInputEditText posterItemMoneyField;
  private Button submit_trade_button;
  private DocumentReference posterItemDocReference;
  private DocumentReference offeringItemDocReference;
  private StorageReference posterItemImageReference;
  private StorageReference offeringItemImageReference;
  private String username;
  private String email;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_adjust_trade_money);

    getCurrentUser();
    if (currentUser == null) {
      goToLoginPage();
    }
    getCurrentUserInfo();

    posterItem = (Item) getIntent().getSerializableExtra("posterItem");
    offeringItem = (Item) getIntent().getSerializableExtra("offeringItem");

    // assign xml variables to elements
    getXmlElements();

    backArrow.setOnClickListener(view -> finish());

    // establish directories in Firebase
    posterItemDocReference =
        DB.collection("users")
            .document(posterItem.getEmail())
            .collection("items")
            .document(posterItem.getEmail() + "-" + posterItem.getTitle());
    offeringItemDocReference =
        DB.collection("users")
            .document(offeringItem.getEmail())
            .collection("items")
            .document(offeringItem.getEmail() + "-" + offeringItem.getTitle());
    posterItemImageReference =
        IMAGE_STORAGE
            .getReference()
            .child(
                "users/"
                    + posterItem.getEmail()
                    + "/"
                    + posterItem.getEmail()
                    + "-"
                    + posterItem.getTitle()
                    + ".jpg");
    offeringItemImageReference =
        IMAGE_STORAGE
            .getReference()
            .child(
                "users/"
                    + offeringItem.getEmail()
                    + "/"
                    + offeringItem.getEmail()
                    + "-"
                    + offeringItem.getTitle()
                    + ".jpg");

    // load in assets, ready text fields
    posterItemImageReference
        .getBytes(FIVE_MEGABYTES)
        .addOnSuccessListener(
            bytes -> {
              Bitmap itemImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
              posterItemImageView.setImageBitmap(itemImage);
            })
        .addOnFailureListener(e -> Log.w(TAG, "Error getting poster item image.", e));
    offeringItemImageReference
        .getBytes(FIVE_MEGABYTES)
        .addOnSuccessListener(
            bytes -> {
              Bitmap itemImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
              offeringItemImageView.setImageBitmap(itemImage);
            })
        .addOnFailureListener(e -> Log.w(TAG, "Error getting offering item image.", e));
    posterItemTitle.setText(posterItem.getTitle());
    offeringItemTitle.setText(offeringItem.getTitle());
    //    posterItemMoneyField.setText("0.00");
    //    offeringItemMoneyField.setText("0.00");
    posterUsername.setText(posterItem.getUsername());

    // set up listener for button
    // if fields empty, error
    submit_trade_button.setOnClickListener(
        v -> {
          Toast toast = Toast.makeText(this, "Mock Success Message!", Toast.LENGTH_LONG);
          toast.show();
        });
  }

  private void getXmlElements() {
    offeringItemImageView = findViewById(R.id.personalItemImage);
    posterItemImageView = findViewById(R.id.publicItemImage);
    offeringItemTitle = findViewById(R.id.personalItemTitle);
    posterItemTitle = findViewById(R.id.publicItemTitle);
    offeringItemMoneyField = findViewById(R.id.personalMoneyField);
    posterItemMoneyField = findViewById(R.id.publicMoneyField);
    submit_trade_button = findViewById(R.id.submit_trade_button);
    posterUsername = findViewById(R.id.posterUsernameTextView);
    backArrow = findViewById(R.id.back_arrow);
  }

  private void goToLoginPage() {
    Intent intent = new Intent(getApplicationContext(), LoginPage.class);
    startActivity(intent);
    finish();
  }

  private void getCurrentUser() {
    currentUser = AUTHENTICATION_INSTANCE.getCurrentUser();
  }

  private void getCurrentUserInfo() {
    username = currentUser.getDisplayName();
    email = currentUser.getEmail();
  }
}
