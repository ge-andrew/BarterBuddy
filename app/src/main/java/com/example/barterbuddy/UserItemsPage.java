package com.example.barterbuddy;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

public class UserItemsPage extends AppCompatActivity implements RecyclerViewInterface {

  private static final String TAG = "UserItemsPage";
  final long ONE_MEGABYTE = 1024 * 1024;
  private final FirebaseFirestore db = FirebaseFirestore.getInstance();
  private final FirebaseStorage imageStorage = FirebaseStorage.getInstance();
  private final ArrayList<Bitmap> itemImages = new ArrayList<Bitmap>();
  private final int REQUEST_CODE = 1002;
  Button add_item_button;
  Button active_items_button;
  private ArrayList<Item> items = new ArrayList<Item>();
  private String username;
  private String email;
  private StorageReference imageReference;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_user_items);

    username = getIntent().getStringExtra("username");
    email = getIntent().getStringExtra("email");

    // Set up recyclerView
    // RecyclerView setup inside this method to prevent late loading of Firebase data from
    // onComplete

    setUpItems(this);

    // Listener for add_item_button
    add_item_button = findViewById(R.id.add_item_button);
    add_item_button.setOnClickListener(
        view -> {
          Intent intent = new Intent(UserItemsPage.this, AddNewItem.class);
          intent.putExtra("username", username);
          intent.putExtra("email", email);

          // allows this page to refresh if an item was added
          startActivityForResult(intent, REQUEST_CODE);
        });
    active_items_button = findViewById(R.id.active_items_buttons);
    active_items_button.setOnClickListener(
            view -> {
                Intent intent = new Intent(UserItemsPage.this, ItemsAvailablePage.class);
                intent.putExtra("username", username);
                intent.putExtra("email", email);
                startActivity(intent);
            }
    );
  }

  // Take arraylist of items to load recyclerView of user's items
  private void setUpItems(Context context) {
    // retrieve and insert firebase data into items
    db.collection("users")
        .document(email)
        .collection("items")
        .get()
        .addOnCompleteListener(
            task -> {
              ArrayList<Item> newItems = new ArrayList<>();
              if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document :
                    task.getResult()) { // add data from each document (1 currently)
                  newItems.add((document.toObject(Item.class)));
                }
              } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
              }
              items = newItems;

              for (Item item : items) {
                imageReference =
                    imageStorage
                        .getReference()
                        .child("users/" + email + "/" + item.getImageId() + ".jpg");
                imageReference
                    .getBytes(ONE_MEGABYTE)
                    .addOnSuccessListener(
                        bytes -> {
                          Bitmap itemImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                          itemImages.add(itemImage);
                        })
                    .addOnFailureListener(e -> Log.w(TAG, "Error getting image.", e));
              }

              // set up recyclerView
              RecyclerView recyclerView = findViewById(R.id.recycler_view);
              UserItemsRecyclerViewAdapter adapter =
                  new UserItemsRecyclerViewAdapter(
                      context, items, (RecyclerViewInterface) context, itemImages);
              recyclerView.setAdapter(adapter);
              recyclerView.setLayoutManager(new LinearLayoutManager(context));
            });
  }

  // take position of clicked card in recyclerView to start and send correct data to itemDetailPage
  // activity
  @Override
  public void onItemClick(int position) {
    Intent intent = new Intent(UserItemsPage.this, UserItemDetailPage.class);

    intent.putExtra("itemId", items.get(position).getImageId());
    intent.putExtra("username", username);
    intent.putExtra("email", email);

    startActivity(intent);
  }

  // refreshes page if an item was added
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
      setUpItems(this);
    }
  }
}
