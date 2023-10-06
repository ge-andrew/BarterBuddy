package com.example.barterbuddy;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UserItemsPage extends AppCompatActivity implements RecyclerViewInterface {

  private static final String TAG = "UserItemsPage";
  private final FirebaseFirestore db = FirebaseFirestore.getInstance();
  //  private final FirebaseStorage imageStorage = FirebaseStorage.getInstance();
  //  FirebaseUser user;
  Button add_item_button;
  private ArrayList<Item> items = new ArrayList<Item>();
  private ArrayList<Bitmap> itemImages = new ArrayList<Bitmap>();
  private String username;
  private String email;
  private CollectionReference collectionReference;
  private StorageReference imageReference;
  final long ONE_MEGABYTE = 1024 * 1024;
  private final FirebaseStorage imageStorage = FirebaseStorage.getInstance();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_user_items);

    // TODO: include after authentication has been implemented
    // user = FirebaseAuth.getInstance().getCurrentUser();

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
          startActivity(intent);
        });
  }

  // Take arraylist of items to load recyclerView of user's items
  private void setUpItems(Context context) {
    db.collection("users")
        .document(email) // TODO: change documentPath placeholder to variable
        .collection("items")
        .get()
        .addOnCompleteListener(
            new OnCompleteListener<QuerySnapshot>() {
              // retrieve and insert firebase data into items
              @Override
              public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<Item> newItems = new ArrayList<Item>();
                if (task.isSuccessful()) {
                  for (QueryDocumentSnapshot document :
                      task.getResult()) { // add data from each document (1 currently)
                    // TODO: need to map to object instead of individual fields when everything
                    // fully set up
                    newItems.add((document.toObject(Item.class)));

                    imageReference =
                            imageStorage.getReference().child("users/" + email + "/" + (String)document.get("itemId") + ".jpg");
                    imageReference
                            .getBytes(ONE_MEGABYTE)
                            .addOnSuccessListener(
                                    bytes -> {
                                      Bitmap itemImage =
                                              BitmapFactory.decodeByteArray(bytes, 0 ,bytes.length);
                                      itemImages.add(itemImage);
                                    })
                            .addOnFailureListener(e -> Log.w(TAG, "Error getting image.", e));
                  }
                } else {
                  Log.d(TAG, "Error getting documents: ", task.getException());
                }
                items = newItems;

                Log.d(TAG, "Items: " + items.toString());
                Log.d(TAG, "Size of image array: " + itemImages.size());

                // set up recyclerView
                RecyclerView recyclerView = findViewById(R.id.recycler_view);
                UserItemsRecyclerViewAdapter adapter =
                    new UserItemsRecyclerViewAdapter(
                        context, items, (RecyclerViewInterface) context);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
              }
            });
  }

  // take position of clicked card in recyclerView to start and send correct data to itemDetailPage
  // activity
  @Override
  public void onItemClick(int position) {
    Intent intent = new Intent(UserItemsPage.this, ItemDetailPage.class);

    Log.d(TAG, "Item 1's id: " + items.get(0).getImageId());

    intent.putExtra("itemId", items.get(position).getImageId());
    intent.putExtra("username", username);
    intent.putExtra("email", email);

    startActivity(intent);
  }
}
