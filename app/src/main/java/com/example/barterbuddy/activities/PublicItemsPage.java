package com.example.barterbuddy.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.barterbuddy.R;
import com.example.barterbuddy.adapters.PublicItemsRecyclerAdapter;
import com.example.barterbuddy.interfaces.RecyclerViewInterface;
import com.example.barterbuddy.models.Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class PublicItemsPage extends AppCompatActivity implements RecyclerViewInterface {

  private static final String TAG = "ItemsAvailable";
  private final ArrayList<Bitmap> ITEM_IMAGES = new ArrayList<>();
  private final FirebaseFirestore FIRESTORE_INSTANCE = FirebaseFirestore.getInstance();
  private final FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();
  private FirebaseUser currentUser;
  private Button user_items_button;
  private RecyclerView publicItemsRecycler;
  private SwipeRefreshLayout publicItemsSwipeRefreshLayout;
  private ArrayList<Item> itemsFromFirestore = new ArrayList<>();
  private String currentUserUsername;
  private String currentUserEmail;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_public_items);

    getCurrentUser();
    if (currentUser == null) {
      goToLoginPage();
    }
    getCurrentUserInfo();

    getXmlElements();

    // Set up recyclerView
    // RecyclerView setup inside this method to prevent late loading of Firebase data from
    // onComplete
    setUpItems(this);

    user_items_button.setOnClickListener(
        view -> {
          Intent intent = new Intent(PublicItemsPage.this, UserProfileHub.class);
          intent.putExtra("username", currentUserUsername);
          intent.putExtra("email", currentUserEmail);
          startActivity(intent);
        });

    publicItemsSwipeRefreshLayout.setOnRefreshListener(() -> {
      setUpItems(this);
      publicItemsSwipeRefreshLayout.setRefreshing(false);
    });
  }


  // Take arraylist of items to load recyclerView of user's items
  private void setUpItems(Context context) {
    // retrieve and insert firebase data into items
    FIRESTORE_INSTANCE
        .collectionGroup("items")
        .whereEqualTo("active", true)
        .get()
        .addOnCompleteListener(
            task -> {
              ArrayList<Item> availableItems = new ArrayList<>();
              if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                  // don't add item if is user's own item
                  if ((document.get("email") != null && document.get("username") != null)
                      && !(document.get("email").equals(currentUserEmail)
                          && document.get("username").equals(currentUserUsername)))
                    availableItems.add((document.toObject(Item.class)));
                }
              } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
              }
              itemsFromFirestore = availableItems;
              // set up recyclerView
              PublicItemsRecyclerAdapter adapter =
                  new PublicItemsRecyclerAdapter(
                      context, availableItems, (RecyclerViewInterface) context, ITEM_IMAGES);
              publicItemsRecycler.setAdapter(adapter);
              publicItemsRecycler.setLayoutManager(new LinearLayoutManager(context));
            });
  }

  // take position of clicked card in recyclerView to start and send correct data to
  // PublicItemDetailPage
  // activity
  @Override
  public void onItemClick(int position) {
    Intent intent = new Intent(PublicItemsPage.this, PublicItemsDetailPage.class);
    intent.putExtra("itemId", itemsFromFirestore.get(position).getImageId());
    intent.putExtra("username", itemsFromFirestore.get(position).getUsername());
    intent.putExtra("email", itemsFromFirestore.get(position).getEmail());
    startActivity(intent);
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
    currentUserUsername = currentUser.getDisplayName();
    currentUserEmail = currentUser.getEmail();
  }

  private void getXmlElements() {
    user_items_button = findViewById(R.id.User_Items_Button);
    publicItemsRecycler = findViewById(R.id.PublicItemsRecyclerView);
    publicItemsSwipeRefreshLayout = findViewById(R.id.public_items_swipeRefresh);
  }
}
