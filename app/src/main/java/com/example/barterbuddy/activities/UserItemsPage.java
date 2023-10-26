package com.example.barterbuddy.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.barterbuddy.R;
import com.example.barterbuddy.adapters.UserItemsRecyclerViewAdapter;
import com.example.barterbuddy.interfaces.RecyclerViewInterface;
import com.example.barterbuddy.models.Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class UserItemsPage extends AppCompatActivity implements RecyclerViewInterface {

  private static final String TAG = "UserItemsPage";
  private final int REQUEST_CODE = 1002;
  private final FirebaseFirestore FIRESTORE_INSTANCE = FirebaseFirestore.getInstance();
  private final FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();
  private FirebaseUser currentUser;
  private final ArrayList<Bitmap> ITEM_IMAGES = new ArrayList<>();
  private ArrayList<Item> items = new ArrayList<>();
  private Button add_item_button;
  private Button active_items_button;
  private String username;
  private String email;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_items);

    Toolbar toolbar = findViewById(R.id.menu);
    setSupportActionBar(toolbar);

    getCurrentUser();
    if (currentUser == null) {
      goToLoginPage();
    }
    getCurrentUserInfo();

    // Set up recyclerView
    // RecyclerView setup inside this method to prevent late loading of Firebase data from
    // onComplete

    setUpItems(this);

    // Listener for add_item_button
    add_item_button = findViewById(R.id.add_item_button);
    add_item_button.setOnClickListener(
        view -> {
          Intent intent = new Intent(UserItemsPage.this, AddNewItemPage.class);
          // allows this page to refresh if an item was added
          startActivityForResult(intent, REQUEST_CODE);
        });
    active_items_button = findViewById(R.id.active_items_buttons);
    active_items_button.setOnClickListener(
        view -> {
          Intent intent = new Intent(UserItemsPage.this, ItemsAvailablePage.class);
          startActivity(intent);
        });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.profile_menu, menu);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.logout) {
      AUTHENTICATION_INSTANCE.signOut();
      Intent intent = new Intent(getApplicationContext(), LoginPage.class);
      startActivity(intent);
      finish();
    }
    return true;
  }

  // Take arraylist of items to load recyclerView of user's items
  private void setUpItems(Context context) {
    // retrieve and insert firebase data into items
    FIRESTORE_INSTANCE
        .collection("users")
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

              // set up recyclerView
              RecyclerView recyclerView = findViewById(R.id.recycler_view);
              UserItemsRecyclerViewAdapter adapter =
                  new UserItemsRecyclerViewAdapter(
                      context, items, (RecyclerViewInterface) context, ITEM_IMAGES);
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
}
