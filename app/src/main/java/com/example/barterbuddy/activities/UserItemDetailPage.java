package com.example.barterbuddy.activities;

import static com.example.barterbuddy.network.UpdateItemDocument.makeItemActive;

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
import com.example.barterbuddy.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserItemDetailPage extends AppCompatActivity {

  private static final String TAG = "ItemDetailPage"; // for logging from this activity
  private final FirebaseFirestore FIRESTORE_INSTANCE = FirebaseFirestore.getInstance();
  private final FirebaseStorage IMAGE_STORAGE_INSTANCE = FirebaseStorage.getInstance();
  private final FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();
  private TextView itemTitle;
  private TextView usernameTextView;
  private TextView itemDescription;
  private ImageView imageView;
  private Button set_active_items_button;
  private String itemId;
  private String username;
  private String email;
  private Item currentItem;
  private DocumentReference itemDocReference;
  private StorageReference imageReference;
  private FirebaseUser currentUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_item_detail_page);

    getCurrentUser();
    if (currentUser == null) {
      goToLoginPage();
    }
    getCurrentUserInfo();
    itemId = getIntent().getStringExtra("itemId");

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
                currentItem = documentSnapshot.toObject(Item.class);
                Log.d(TAG, "Item information: " + currentItem);

                // set the title and description based on information from the object
                if (currentItem != null) {
                  itemTitle.setText(currentItem.getTitle());
                  itemDescription.setText(currentItem.getDescription());

                  // get the image for this item from Firebase Cloud Storage
                  imageReference =
                      IMAGE_STORAGE_INSTANCE
                          .getReference()
                          .child("users/" + email + "/" + itemId + ".jpg");

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
    set_active_items_button.setOnClickListener(
        v -> {
          makeItemActive(
              new Item(
                  (String) itemTitle.getText(),
                  (String) itemDescription.getText(),
                  itemId,
                  false,
                  username,
                  email),
              true);

          // creates an intent that switches to the OfferTradePage activity and passes the item
          // to the new activity
          Intent intent = new Intent(UserItemDetailPage.this, ChooseTradeItemPage.class);
          intent.putExtra("itemToTradeFor", currentItem);
          Toast toast = Toast.makeText(this, "Set to Active", Toast.LENGTH_LONG);
          toast.show();
          // startActivity(intent);
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
        FIRESTORE_INSTANCE.collection("users").document(email).collection("items").document(itemId);
  }

  private void getXmlElements() {
    usernameTextView = findViewById(R.id.username_text_view);
    itemTitle = findViewById(R.id.item_title_text_view);
    itemDescription = findViewById(R.id.description_text_view);
    set_active_items_button = findViewById(R.id.offer_trade_button);
    imageView = findViewById(R.id.item_image_view);
  }
}
