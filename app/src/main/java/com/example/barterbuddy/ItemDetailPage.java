package com.example.barterbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ItemDetailPage extends AppCompatActivity {
  private static final String TAG = "ItemDetailPage"; // for logging from this activity
  private TextView itemTitle;
  private TextView username;
  private TextView itemDescription;
  private ImageView imageView;
  private Button offerTradeButton;
  private String itemId;
  private String posterId;

  private DocumentReference itemDocReference;
  private final FirebaseFirestore db = FirebaseFirestore.getInstance();
  private StorageReference imageReference;
  private final FirebaseStorage imageStorage = FirebaseStorage.getInstance();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_item_detail_page);

    // get item id and poster id from recycler view
    itemId = getIntent().getStringExtra("item_id");
    posterId = getIntent().getStringExtra("poster_id");

    Log.d(TAG, "User ID is " + posterId);
    Log.d(TAG, "Item ID is " + itemId);

    // get the Firestore document reference for the given user and item ids
    itemDocReference =
        db.collection("users").document(posterId).collection("items").document(itemId);

    // initializing views and buttons
    username = findViewById(R.id.username_text_view);
    itemTitle = findViewById(R.id.item_title_text_view);
    itemDescription = findViewById(R.id.description_text_view);
    offerTradeButton = findViewById(R.id.offer_trade_button);
    imageView = findViewById(R.id.item_image_view);

    // populate our private fields with data from Firestore
    itemDocReference
        .get()
        .addOnSuccessListener(
            documentSnapshot -> {
              Item item;
              if (documentSnapshot.exists()) {
                // convert the document data to an Item object
                item = documentSnapshot.toObject(Item.class);
                Log.d(TAG, "Item information: " + item);

                // set the title and description based on information from the object
                if (item != null) {
                  itemTitle.setText(item.getTitle());
                  itemDescription.setText(item.getDescription());

                  // get the image for this item from Firebase Cloud Storage
                  imageReference = imageStorage.getReferenceFromUrl(item.getImageId());

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
                                username.setText(user.getUsername());
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
  }
}
