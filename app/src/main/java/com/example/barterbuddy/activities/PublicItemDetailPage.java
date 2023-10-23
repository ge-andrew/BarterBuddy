package com.example.barterbuddy.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.barterbuddy.R;
import com.example.barterbuddy.models.Item;
import com.example.barterbuddy.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PublicItemDetailPage extends AppCompatActivity {

  private static final String TAG = "ItemDetailPage"; // for logging from this activity
  private final FirebaseFirestore FIRESTORE_INSTANCE = FirebaseFirestore.getInstance();
  private final FirebaseStorage IMAGE_STORAGE_INSTANCE = FirebaseStorage.getInstance();
  private final FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();
  private TextView itemTitle;
  private TextView usernameTextView;
  private TextView itemDescription;
  private ImageView imageView;
  private Button offer_trade_button;
  private String itemId;
  private String itemUsername;
  private String itemEmail;
  private String username;
  private String email;
  private Item posterItem;
  private DocumentReference itemDocReference;
  private StorageReference imageReference;
  private FirebaseUser currentUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_public_item_detail_page);

    getCurrentUser();
    if (currentUser == null) {
      goToLoginPage();
    }
    getCurrentUserInfo();
    getItemDataFromIntent();

    Log.d(TAG, "User username is " + username);
    Log.d(TAG, "User email is " + email);
    Log.d(TAG, "Item ID is " + itemId);

    establishItemReference();
    getXmlElements();

    // populate our private fields with data from Firestore
    itemDocReference
        .get()
        .addOnSuccessListener(
            documentSnapshot -> {
              if (documentSnapshot.exists()) {
                // convert the document data to an Item object
                posterItem = documentSnapshot.toObject(Item.class);
                Log.d(TAG, "Item information: " + posterItem);

                // set the title and description based on information from the object
                if (posterItem != null) {
                  itemTitle.setText(posterItem.getTitle());
                  itemDescription.setText(posterItem.getDescription());

                  // get the image for this item from Firebase Cloud Storage
                  imageReference =
                      IMAGE_STORAGE_INSTANCE
                          .getReference()
                          .child("users/" + itemEmail + "/" + itemId + ".jpg");

                  final long ONE_MEGABYTE = 1024 * 1024;
                  imageReference
                      .getBytes(ONE_MEGABYTE)
                      .addOnSuccessListener(
                          bytes -> {
                            // convert the ByteArray of the image into a Bitmap
                            Bitmap itemImage =
                                BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imageView.setImageBitmap(itemImage);
                          })
                      .addOnFailureListener(e -> Log.w(TAG, "Error getting image.", e));
                } else {
                  Log.w(TAG, "Item is null");
                }

                // get the title of the poster by checking the parent collections and documents
                DocumentReference postingUserDocRef = itemDocReference.getParent().getParent();
                if (postingUserDocRef != null) {
                  postingUserDocRef
                      .get()
                      .addOnSuccessListener(
                          userDocSnapshot -> {
                            User user;
                            if (userDocSnapshot.exists()) {
                              user = userDocSnapshot.toObject(User.class);
                              Log.d(TAG, "User information: " + user);

                              // set the username field
                              if (user != null) {
                                usernameTextView.setText(user.getUsername());
                              } else {
                                Log.w(TAG, "User object is null");
                              }
                            }
                          })
                      .addOnFailureListener(e -> Log.w(TAG, "Error getting user document.", e));
                } else {
                  Log.w(TAG, "User is null");
                }
              }
            })
        .addOnFailureListener(e -> Log.w(TAG, "Error getting item document.", e));

    // prepare the offer trade button for advancing to the next page
    offer_trade_button.setOnClickListener(
        v -> {
          // creates an intent that switches to the OfferTradePage activity and passes the item
          // to the new activity
          Intent intent = new Intent(PublicItemDetailPage.this, ChooseTradeItemPage.class);
          intent.putExtra("posterItem", posterItem);
          intent.putExtra("username", username);
          intent.putExtra("email", email);
          startActivity(intent);
        });
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

  private void establishItemReference() {
    itemDocReference =
        FIRESTORE_INSTANCE.collection("users").document(itemEmail).collection("items").document(itemId);
  }

  private void getXmlElements() {
    usernameTextView = findViewById(R.id.username_text_view);
    itemTitle = findViewById(R.id.item_title_text_view);
    itemDescription = findViewById(R.id.description_text_view);
    offer_trade_button = findViewById(R.id.offer_trade_button);
    imageView = findViewById(R.id.item_image_view);
  }

  private void getItemDataFromIntent() {
    itemId = getIntent().getStringExtra("itemId");
    itemUsername = getIntent().getStringExtra("username");
    itemEmail = getIntent().getStringExtra("email");
  }
}
