package com.example.barterbuddy.activities;

import static com.example.barterbuddy.network.UpdateTradeDocument.sendCounteroffer;
import static com.example.barterbuddy.network.UpdateTradeDocument.setStateToCanceled;
import static com.example.barterbuddy.network.UpdateTradeDocument.setStateToChatting;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.barterbuddy.R;
import com.example.barterbuddy.models.Item;
import com.example.barterbuddy.models.Trade;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.text.DecimalFormat;

public class BarterPage extends AppCompatActivity {

  private static final String TAG = "BarterPage"; // for logging from this activity
  final long FIVE_MEGABYTES = 1024 * 1024 * 5;
  private final FirebaseFirestore DB = FirebaseFirestore.getInstance();
  private final FirebaseStorage IMAGE_STORAGE = FirebaseStorage.getInstance();
  private final FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();
  private final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("0.00");
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
  private CardView lock_in_button;
  private CardView withdraw_button;
  private Button counteroffer_button;
  private TextView posterItemTitle;
  private TextView offeringItemTitle;
  private TextView counteroffersLeft;
  private TextInputEditText posterMoneyField;
  private TextInputEditText offeringMoneyField;
  private String offeringItemMoney;
  private String posterItemMoney;

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

    offeringMoneyField.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {}

          @Override
          public void afterTextChanged(Editable s) {
            String tempString = "";
            offeringItemMoney = String.valueOf(offeringMoneyField.getText());
            int positionOfDecimal = offeringItemMoney.indexOf('.');
            int lengthOfValue = offeringItemMoney.length();
            boolean isLeadingZero;

            if ((lengthOfValue < 3) || (positionOfDecimal != offeringItemMoney.length() - 3)) {
              isLeadingZero = true;
              for (int index = 0; index < offeringItemMoney.length(); index++) {
                if (offeringItemMoney.charAt(index) != '0'
                    && offeringItemMoney.charAt(index) != '.') {
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
              offeringMoneyField.setText(tempString);
              offeringMoneyField.setSelection(lengthOfTempString);
            }
          }
        });
    // set up listener for poster money text input formatting
    posterMoneyField.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {}

          @Override
          public void afterTextChanged(Editable s) {
            String tempString = "";
            posterItemMoney = String.valueOf(posterMoneyField.getText());
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
              posterMoneyField.setText(tempString);
              posterMoneyField.setSelection(lengthOfTempString);
            }
          }
        });

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
        .addOnFailureListener(task -> Log.d(TAG, "Error getting trade info"));
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
      offeringMoneyField.setText(CURRENCY_FORMAT.format(trade.getMoney()));
    } else if (trade.getMoney() < 0) {
      posterMoneyField.setText(CURRENCY_FORMAT.format(trade.getMoney() * -1));
    }

    if (isPoster) {
      counteroffersLeft.setText(String.valueOf(trade.getNumberCounteroffersLeft() / 2));
    } else {
      counteroffersLeft.setText(
          String.valueOf(
              trade.getNumberCounteroffersLeft() / 2 + trade.getNumberCounteroffersLeft() % 2));
    }

    configurePageToUser();
  }

  private void configurePageToUser() {
    withdraw_button.setOnClickListener(
        v -> {
          setStateToCanceled(trade);
          Toast.makeText(this, "Withdrew from Trade", Toast.LENGTH_SHORT).show();
          finish();
        });
    if (tradeIsLastChance() && isPoster) {
      Toast.makeText(this, "No counteroffers left! Last chance!", Toast.LENGTH_LONG).show();
      allowCounterOffers(false);
      allowAcceptTrade(true);
    } else if ((tradeIsPosterTurn() && isPoster) || (!tradeIsPosterTurn() && !isPoster)) {
      Toast.makeText(this, "Accept trade or counteroffer", Toast.LENGTH_LONG).show();
      allowCounterOffers(true);
      allowAcceptTrade(true);
    } else {
      Toast.makeText(this, "Your last offer is pending", Toast.LENGTH_LONG).show();
      allowCounterOffers(false);
      allowAcceptTrade(false);
    }
  }

  public boolean tradeIsPosterTurn() {
    return trade.getNumberCounteroffersLeft() % 2 == 0;
  }

  private boolean tradeIsLastChance() {
    return trade.getNumberCounteroffersLeft() == 0;
  }

  private void allowCounterOffers(boolean isAllowed) {
    if (isAllowed) {
      posterMoneyField.setFocusable(true);
      offeringMoneyField.setFocusable(true);

      counteroffer_button.setOnClickListener(
          v -> {
            trade.setMoney(0);
            if (!posterMoneyField.getText().toString().equals("")) {
              trade.setMoney(-Double.parseDouble(posterMoneyField.getText().toString()));
            }
            if (!offeringMoneyField.getText().toString().equals("")) {
              trade.setMoney(
                  trade.getMoney() + Double.parseDouble(offeringMoneyField.getText().toString()));
            }
            sendCounteroffer(trade);

            if (isPoster) {
              counteroffersLeft.setText(String.valueOf(trade.getNumberCounteroffersLeft() / 2 - 1));
            } else {
              counteroffersLeft.setText(
                  String.valueOf(
                      trade.getNumberCounteroffersLeft() / 2
                          + trade.getNumberCounteroffersLeft() % 2
                          - 1));
            }

            Toast.makeText(this, "Counteroffer sent!", Toast.LENGTH_SHORT).show();
            allowAcceptTrade(false);
            allowCounterOffers(false);
          });

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

            trade.setMoney(
                Double.parseDouble(posterMoneyField.getText().toString())
                    - Double.parseDouble(posterMoneyField.getText().toString()));

            sendCounteroffer(trade);
            setStateToChatting(trade);

            startActivity(intent);
            finish();
          });
    } else {
      posterMoneyField.setFocusable(false);
      offeringMoneyField.setFocusable(false);
      counteroffer_button.setEnabled(false);

      counteroffer_button.setBackgroundColor(
          getResources().getColor(R.color.light_gray, getTheme()));
    }
  }

  private void allowAcceptTrade(boolean isAllowed) {
    lock_in_button.setFocusable(isAllowed);
    lock_in_button.setEnabled(isAllowed);

    if (isAllowed) {
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

            sendCounteroffer(trade);
            setStateToChatting(trade);

            startActivity(intent);
            finish();
          });
    } else {
      int disabledColor = getResources().getColor(R.color.light_gray, getTheme());
      lock_in_button.setBackgroundTintList(ColorStateList.valueOf(disabledColor));
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
    lock_in_button = (CardView) includedLayout.findViewById(R.id.accept_button);
    withdraw_button = (CardView) includedLayout.findViewById(R.id.decline_button);
    counteroffer_button = includedLayout.findViewById(R.id.counteroffer_button);
    posterImageView = includedLayout.findViewById(R.id.poster_item_image);
    posterItemTitle = includedLayout.findViewById(R.id.posterItemTitle);
    posterMoneyField = includedLayout.findViewById(R.id.poster_trade_money_barter);
    offeringImageView = includedLayout.findViewById(R.id.offering_item_image);
    offeringItemTitle = includedLayout.findViewById(R.id.offeringItemTitle);
    offeringMoneyField = includedLayout.findViewById(R.id.offering_trade_money_barter);
    counteroffersLeft = includedLayout.findViewById(R.id.counteroffers_left);
  }
}
