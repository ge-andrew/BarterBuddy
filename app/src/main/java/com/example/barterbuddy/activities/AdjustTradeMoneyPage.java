package com.example.barterbuddy.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdjustTradeMoneyPage extends AppCompatActivity {

  private static final String TAG = "AdjustTradeMoneyPage";
  private static final Locale locale = new Locale("en", "US");
  private static final DecimalFormat formatter =
      (DecimalFormat) NumberFormat.getCurrencyInstance(locale);
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
  private String offeringItemMoney;
  private TextInputEditText posterItemMoneyField;
  private String posterItemMoney;
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
    posterUsername.setText(posterItem.getUsername());
    offeringItemMoneyField.setText("0.00");
    posterItemMoneyField.setText("0.00");

    // set up listener for button
    submit_trade_button.setOnClickListener(
        v -> {
          Map<String, Object> tradeData = new HashMap<>();

          tradeData.put(
              "money",
              Double.parseDouble(posterItemMoneyField.getText().toString())
                  - Double.parseDouble(offeringItemMoneyField.getText().toString()));
          tradeData.put("offeringEmail", offeringItem.getEmail());
          tradeData.put("posterEmail", posterItem.getEmail());
          tradeData.put(
              "offeringItem",
              DB.document(
                  "users/"
                      + offeringItem.getEmail()
                      + "/items/"
                      + offeringItem.getEmail()
                      + "-"
                      + offeringItem.getTitle()));
          tradeData.put(
              "posterItem",
              DB.document(
                  "/users/"
                      + posterItem.getEmail()
                      + "/items/"
                      + posterItem.getEmail()
                      + "-"
                      + posterItem.getTitle()));
          tradeData.put("stateOfCompletion", "IN_PROGRESS");

          DB.collection("trades")
              .document(posterItem.getEmail() + "_" + offeringItem.getEmail())
              .set(tradeData)
              .addOnSuccessListener(
                  u -> {
                    Toast toast = Toast.makeText(this, "Trade submitted!", Toast.LENGTH_LONG);
                    toast.show();

                    Intent intent = new Intent(AdjustTradeMoneyPage.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                  })
              .addOnFailureListener(
                  w -> {
                    Toast toast =
                        Toast.makeText(this, "Error submitting trade.", Toast.LENGTH_LONG);
                    toast.show();
                  });
        });

    // set up listener for offering money text input formatting
      offeringItemMoneyField.addTextChangedListener(
              new TextWatcher() {
                  @Override
                  public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                  @Override
                  public void onTextChanged(CharSequence s, int start, int before, int count) {}

                  @Override
                  public void afterTextChanged(Editable s) {
                      String tempString = "";
                      offeringItemMoney = String.valueOf(offeringItemMoneyField.getText());
                      int positionOfDecimal = offeringItemMoney.indexOf('.');
                      int lengthOfValue = offeringItemMoney.length();
                      boolean isLeadingZero;

                      if ((lengthOfValue < 3) || (positionOfDecimal != offeringItemMoney.length() - 3)) {
                          isLeadingZero = true;
                          for (int index = 0; index < offeringItemMoney.length(); index++) {
                              if (offeringItemMoney.charAt(index) != '0' && offeringItemMoney.charAt(index) != '.') {
                                  tempString = tempString + offeringItemMoney.charAt(index);
                                  isLeadingZero = false;
                              } else if (offeringItemMoney.charAt(index) == '0' && !isLeadingZero) {
                                  tempString = tempString + offeringItemMoney.charAt(index);
                              }
                          }

                          int lengthOfTempString = tempString.length();
                          if (lengthOfTempString < 3) {
                              for (int index = 0; index < 3 - lengthOfTempString; index++) {
                                  tempString = '0' + tempString;
                              }

                              String firstHalf = tempString.substring(0, 1);
                              String secondHalf = tempString.substring(1, 3);
                              tempString = firstHalf + '.' + secondHalf;
                          } else {
                              String firstHalf = tempString.substring(0, lengthOfTempString - 2);
                              String secondHalf =
                                      tempString.substring(lengthOfTempString - 2, lengthOfTempString);
                              tempString = firstHalf + '.' + secondHalf;
                          }
                          lengthOfTempString = tempString.length();
                          offeringItemMoneyField.setText(tempString);
                          offeringItemMoneyField.setSelection(lengthOfTempString);
                      }
                  }
              });
      // set up listener for poster money text input formatting
      posterItemMoneyField.addTextChangedListener(
              new TextWatcher() {
                  @Override
                  public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                  @Override
                  public void onTextChanged(CharSequence s, int start, int before, int count) {}

                  @Override
                  public void afterTextChanged(Editable s) {
                      String tempString = "";
                      posterItemMoney = String.valueOf(posterItemMoneyField.getText());
                      int positionOfDecimal = posterItemMoney.indexOf('.');
                      int lengthOfValue = posterItemMoney.length();
                      boolean isLeadingZero;

                      if ((lengthOfValue < 3) || (positionOfDecimal != posterItemMoney.length() - 3)) {
                          isLeadingZero = true;
                          for (int index = 0; index < posterItemMoney.length(); index++) {
                              if (posterItemMoney.charAt(index) != '0' && posterItemMoney.charAt(index) != '.') {
                                  tempString = tempString + posterItemMoney.charAt(index);
                                  isLeadingZero = false;
                              } else if (posterItemMoney.charAt(index) == '0' && !isLeadingZero) {
                                  tempString = tempString + posterItemMoney.charAt(index);
                              }
                          }

                          int lengthOfTempString = tempString.length();
                          if (lengthOfTempString < 3) {
                              for (int index = 0; index < 3 - lengthOfTempString; index++) {
                                  tempString = '0' + tempString;
                              }

                              String firstHalf = tempString.substring(0, 1);
                              String secondHalf = tempString.substring(1, 3);
                              tempString = firstHalf + '.' + secondHalf;
                          } else {
                              String firstHalf = tempString.substring(0, lengthOfTempString - 2);
                              String secondHalf =
                                      tempString.substring(lengthOfTempString - 2, lengthOfTempString);
                              tempString = firstHalf + '.' + secondHalf;
                          }
                          lengthOfTempString = tempString.length();
                          posterItemMoneyField.setText(tempString);
                          posterItemMoneyField.setSelection(lengthOfTempString);
                      }
                  }
              });

  }

  private void getXmlElements() {
    offeringItemImageView = findViewById(R.id.personalItemImage);
    posterItemImageView = findViewById(R.id.publicItemImage);
    offeringItemTitle = findViewById(R.id.posterItemTitle);
    posterItemTitle = findViewById(R.id.offeringItemTitle);
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
