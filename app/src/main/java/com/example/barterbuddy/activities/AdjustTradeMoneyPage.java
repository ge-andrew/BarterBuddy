package com.example.barterbuddy.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.barterbuddy.R;
import com.example.barterbuddy.models.Item;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AdjustTradeMoneyPage extends AppCompatActivity {

  private static final String TAG = "AdjustTradeMoneyPage";
  final long ONE_MEGABYTE = 1024 * 1024;
  private final FirebaseFirestore DB = FirebaseFirestore.getInstance();
  private final FirebaseStorage IMAGE_STORAGE = FirebaseStorage.getInstance();
  private Item offeringItem;
  private Item posterItem;
  private ImageView offeringItemImageView;
  private ImageView posterItemImageView;
  private TextView offeringItemTitle;
  private TextView posterItemTitle;
  private EditText offeringItemMoneyField;
  private EditText posterItemMoneyField;
  private Button submit_trade_button;
  private DocumentReference posterItemDocReference;
  private DocumentReference offeringItemDocReference;
  private StorageReference posterItemImageReference;
  private StorageReference offeringItemImageReference;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_adjust_trade_money);

    posterItem = (Item) getIntent().getSerializableExtra("posterItem");
    offeringItem = (Item) getIntent().getSerializableExtra("offeringItem");

    // assign xml variables to elements
    getXmlElements();

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
            .getBytes(ONE_MEGABYTE)
            .addOnSuccessListener(
                    bytes -> {
                      Bitmap itemImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                      posterItemImageView.setImageBitmap(itemImage);
                    })
            .addOnFailureListener(e -> Log.w(TAG, "Error getting poster item image.", e));
    offeringItemImageReference
            .getBytes(ONE_MEGABYTE)
            .addOnSuccessListener(
                    bytes -> {
                      Bitmap itemImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                      offeringItemImageView.setImageBitmap(itemImage);
                    })
            .addOnFailureListener(e -> Log.w(TAG, "Error getting offering item image.", e));
    posterItemTitle.setText(posterItem.getTitle());
    offeringItemTitle.setText(offeringItem.getTitle());
    posterItemMoneyField.setText("0.00");
    offeringItemMoneyField.setText("0.00");

    // set up listener for button
    // if fields empty, error
    submit_trade_button.setOnClickListener(
            v -> {
              Toast toast = Toast.makeText(this, "Mock Success Message!", Toast.LENGTH_LONG);
              toast.show();
            }
    );

  }

  private void getXmlElements() {
    offeringItemImageView = findViewById(R.id.offeringItemImage);
    posterItemImageView = findViewById(R.id.posterItemImage);
    offeringItemTitle = findViewById(R.id.offeringItemTitle);
    posterItemTitle = findViewById(R.id.posterItemTitle);
    offeringItemMoneyField = findViewById(R.id.offeringItemMoneyField);
    posterItemMoneyField = findViewById(R.id.posterItemMoneyField);
    submit_trade_button = findViewById(R.id.submit_trade_button);
  }
}
