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
import com.example.barterbuddy.adapters.UserItemsRecyclerViewAdapter;
import com.example.barterbuddy.interfaces.RecyclerViewInterface;
import com.example.barterbuddy.models.Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

public class ChooseTradeItemPage extends AppCompatActivity implements RecyclerViewInterface {

    private static final String TAG = "ChooseTradeItemPage";
    private Item posterItem;
    private String username;
    private String email;
    private Button add_item_button;
    private final FirebaseFirestore DB = FirebaseFirestore.getInstance();
    private final FirebaseStorage IMAGE_STORAGE = FirebaseStorage.getInstance();
    private final FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();
    private FirebaseUser currentUser;
    final long ONE_MEGABYTE = 1024 * 1024;
    private final int REQUEST_CODE = 1002;
    private StorageReference imageReference;
    private ArrayList<Item> items = new ArrayList<>();
    private final ArrayList<Bitmap> ITEM_IMAGES = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_trade_item);

        getCurrentUser();
        if (currentUser == null) {
            goToLoginPage();
        }
        getCurrentUserInfo();

        posterItem = (Item) getIntent().getSerializableExtra("posterItem");
        username = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("email");

        getXmlElements();

        setUpItems(this);

        // setup onclick listener for adding item
        add_item_button.setOnClickListener(
                v -> {
                    // creates an intent that switches to the OfferTradePage activity and passes the item
                    // to the new activity
                    Intent intent = new Intent(ChooseTradeItemPage.this, AddNewItemPage.class);
                    intent.putExtra("username", username);
                    intent.putExtra("email", email);
                    startActivity(intent);
                });
    }

    private void setUpItems(Context context) {
        // retrieve and insert firebase data into items
        DB.collection("users")
                .document(email)
                .collection("items")
                .get()
                .addOnCompleteListener(
                        task -> {
                            // Get each item from request and add to NewItems
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

                            // For each item's image, place in ITEM_IMAGES
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
                            RecyclerView recyclerView = findViewById(R.id.recycler_view);
                            UserItemsRecyclerViewAdapter adapter =
                                    new UserItemsRecyclerViewAdapter(
                                            context, items, (RecyclerViewInterface) context, ITEM_IMAGES);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        });
    }

    // take position of clicked card in recyclerView to start and send correct data to adjustTradeMoneyPage
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(ChooseTradeItemPage.this, AdjustTradeMoneyPage.class);

        intent.putExtra("offeringItem", items.get(position));
        intent.putExtra("posterItem", posterItem);

        startActivity(intent);
    }

    private void goToLoginPage() {
        Intent intent = new Intent(getApplicationContext(), LoginPage.class);
        startActivity(intent);
        finish();
    }

    private void getCurrentUser() {
        currentUser = AUTHENTICATION_INSTANCE.getCurrentUser();
    }

    private void getCurrentUserInfo() {
        username = currentUser.getDisplayName();
        email = currentUser.getEmail();
    }

  private void getXmlElements() {
        add_item_button = findViewById(R.id.add_item_button);
    }
}
