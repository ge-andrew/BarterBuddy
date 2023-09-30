package com.example.barterbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ItemDetailPage extends AppCompatActivity {
  // Put the database string values into constants
  private static final String USERNAME_KEY = "username";
  private static final String TAG = "ItemDetailPage"; // for logging from this activity
  private TextView item_title;
  private TextView username;
  private TextView item_description;
  private ImageView image_view;
  private Button offer_trade_button;
  private String item_id;
  private String poster_id;

  private DocumentReference itemDocReference;
  private final FirebaseFirestore db = FirebaseFirestore.getInstance();
  private StorageReference imageReference;
  private final FirebaseStorage imageStorage = FirebaseStorage.getInstance();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_item_detail_page);

    // get item id and poster id from recycler view
    item_id = getIntent().getStringExtra("item_id");
    poster_id = getIntent().getStringExtra("poster_id");

    Log.d(TAG, "User ID is " + poster_id);
    Log.d(TAG, "Item ID is " + item_id);

    // get the Firestore document reference for the given user and item ids
    itemDocReference =
        db.collection("users").document(poster_id).collection("items").document(item_id);

    // initializing views and buttons
    username = findViewById(R.id.username_text_view);
    item_title = findViewById(R.id.item_title_text_view);
    item_description = findViewById(R.id.description_text_view);
    offer_trade_button = findViewById(R.id.offer_trade_button);
    image_view = findViewById(R.id.item_image_view);

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
                item_title.setText(item.getTitle());
                item_description.setText(item.getDescription());

                // get the title of the poster by checking the parent collections and documents
                // TODO: There may be a better way to write this
                /*
                  This code is created under the assumption that each item document
                  is stored in a collection of the poster's items, which is one of the fields
                  of that poster document, which has another field for its username.
                  So we're traversing up the parents to get to the name of the poster.
                  If we use a different structure for the database,
                  this code will have to be updated.
                */
                // Option A: Store the username of the parent in the Item document for easier access
                // Option B: Create a function that will do the onSuccessListener boilerplate stuff
                // any time we need to use it
                DocumentReference postingUserDocRef = itemDocReference.getParent().getParent();
                postingUserDocRef
                    .get()
                    .addOnSuccessListener(
                        userDocSnapshot -> {
                          if (userDocSnapshot.exists()) {
                            // TODO: Make a "User" class similar to the "Item" class
                            username.setText((String) userDocSnapshot.get(USERNAME_KEY));
                          }
                        })
                    .addOnFailureListener(e -> Log.w(TAG, "Error getting user document.", e));

                // get the image for this item from Firebase Cloud Storage
                imageReference = imageStorage.getReferenceFromUrl(item.getImage_uri());

                final long ONE_MEGABYTE = 1024 * 1024;
                imageReference.getBytes(ONE_MEGABYTE)
                        .addOnSuccessListener(bytes -> {
                          // convert the ByteArray of the image into a Bitmap
                          Bitmap itemImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                          image_view.setImageBitmap(itemImage);
                        })
                        .addOnFailureListener(e -> Log.w(TAG, "Error getting image.", e));
              }
            })
        .addOnFailureListener(e -> Log.w(TAG, "Error getting item document.", e));
  }
}
