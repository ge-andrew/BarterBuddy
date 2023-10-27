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
import com.example.barterbuddy.adapters.PersonalItemsRecyclerViewAdapter;
import com.example.barterbuddy.interfaces.RecyclerViewInterface;
import com.example.barterbuddy.models.Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class PersonalItemsPage extends AppCompatActivity implements RecyclerViewInterface {

  private static final String TAG = "UserItemsPage";
  private final int REQUEST_CODE = 1002;
  private final FirebaseFirestore FIRESTORE_INSTANCE = FirebaseFirestore.getInstance();
  private final FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();
  private FirebaseUser currentUser;
  private final ArrayList<Bitmap> ITEM_IMAGES = new ArrayList<>();
  private ArrayList<Item> items = new ArrayList<>();
  private Button add_new_personal_item_button;
  private Button go_to_public_items_button;
  private String currentUserUsername;
  private String currentUserEmail;
  private RecyclerView personalItemsRecycler;

  @Override

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_personal_items);

    Toolbar toolbar = findViewById(R.id.menu);
    setSupportActionBar(toolbar);

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

    // allows this page to refresh if an item was added
    add_new_personal_item_button.setOnClickListener(
        view -> {
          Intent intent = new Intent(PersonalItemsPage.this, AddNewItemPage.class);
          startActivityForResult(intent, REQUEST_CODE);
        });

    go_to_public_items_button.setOnClickListener(
        view -> {
          Intent intent = new Intent(PersonalItemsPage.this, PublicItemsPage.class);
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
    CollectionReference userItemsCollection;
    userItemsCollection = establishItemsCollection();
    userItemsCollection
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
              // set up recyclerView
              items = newItems;
              PersonalItemsRecyclerViewAdapter adapter =
                  new PersonalItemsRecyclerViewAdapter(
                      context, newItems, (RecyclerViewInterface) context, ITEM_IMAGES);
              personalItemsRecycler.setAdapter(adapter);
              personalItemsRecycler.setLayoutManager(new LinearLayoutManager(context));
            });
  }

  // detects which item was selected and opens a details page with that item's data
  @Override
  public void onItemClick(int position) {
    Intent intent = new Intent(PersonalItemsPage.this, PersonalItemsDetailPage.class);
    intent.putExtra("itemId", items.get(position).getImageId());
    startActivity(intent);
  }

  // refreshes page if a new personal item was added
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
    currentUserUsername = currentUser.getDisplayName();
    currentUserEmail = currentUser.getEmail();
  }

  private void getXmlElements() {
    add_new_personal_item_button = findViewById(R.id.add_new_personal_item_button);
    go_to_public_items_button = findViewById(R.id.active_items_buttons);
    personalItemsRecycler = findViewById(R.id.personal_items_recycler_view);
  }

  private CollectionReference establishItemsCollection() {
    return FIRESTORE_INSTANCE
            .collection("users")
            .document(currentUserEmail)
            .collection("items");
  }
}
