package com.example.barterbuddy.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.barterbuddy.R;
import com.example.barterbuddy.adapters.ItemsToTradeRecyclerAdapter;
import com.example.barterbuddy.interfaces.RecyclerViewInterface;
import com.example.barterbuddy.models.Item;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

public class ItemsAvailablePage extends AppCompatActivity implements RecyclerViewInterface {

  private static final String TAG = "ItemsAvailable";
  final long ONE_MEGABYTE = 1024 * 1024;
  private final FirebaseFirestore DB = FirebaseFirestore.getInstance();
  private final FirebaseStorage IMAGE_STORAGE = FirebaseStorage.getInstance();
  private final ArrayList<Bitmap> ITEM_IMAGES = new ArrayList<>();
  Button user_items_button;
  private ArrayList<Item> items = new ArrayList<>();
  private String username;
  private String email;
  // private CollectionReference collectionReference;
  private StorageReference imageReference;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.recycler_layout);

    username = getIntent().getStringExtra("username");
    email = getIntent().getStringExtra("email");

    // Set up recyclerView
    // RecyclerView setup inside this method to prevent late loading of Firebase data from
    // onComplete

    setUpItems(this);
    user_items_button = findViewById(R.id.User_Items_Button);
    user_items_button.setOnClickListener(
        view -> {
          Intent intent = new Intent(ItemsAvailablePage.this, UserItemsPage.class);
          intent.putExtra("username", username);
          intent.putExtra("email", email);
          startActivity(intent);
        });
  }

  // Take arraylist of items to load recyclerView of user's items
  private void setUpItems(Context context) {
    // retrieve and insert firebase data into items
    DB.collectionGroup("items")
        .whereEqualTo("active", true)
        .get()
        .addOnCompleteListener(
            task -> {
              ArrayList<Item> availableItems = new ArrayList<>();
              if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                  // don't add item if is user's own item
                  if ((document.get("email") != null && document.get("username") != null)
                      && !(document.get("email").equals(email)
                          && document.get("username").equals(username)))
                    availableItems.add((document.toObject(Item.class)));
                }
              } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
              }
              items = availableItems;

              for (Item item : items) {
                imageReference =
                    IMAGE_STORAGE
                        .getReference()
                        .child("users/" + email + "/" + item.getImageId() + ".jpg");
                imageReference
                    .getBytes(ONE_MEGABYTE)
                    .addOnSuccessListener(
                        bytes -> {
                          Bitmap itemImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                          ITEM_IMAGES.add(itemImage);
                        })
                    .addOnFailureListener(e -> Log.w(TAG, "Error getting image.", e));
              }

              // set up recyclerView
              RecyclerView recyclerView = findViewById(R.id.RecyclerView);
              ItemsToTradeRecyclerAdapter adapter =
                  new ItemsToTradeRecyclerAdapter(
                      context, items, (RecyclerViewInterface) context, ITEM_IMAGES);
              recyclerView.setAdapter(adapter);
              recyclerView.setLayoutManager(new LinearLayoutManager(context));
            });
  }

  // take position of clicked card in recyclerView to start and send correct data to
  // PublicItemDetailPage
  // activity
  @Override
  public void onItemClick(int position) {
    Intent intent = new Intent(ItemsAvailablePage.this, PublicItemDetailPage.class);

    intent.putExtra("itemId", items.get(position).getImageId());
    intent.putExtra("username", items.get(position).getUsername());
    intent.putExtra("email", items.get(position).getEmail());

    startActivity(intent);
  }
}
