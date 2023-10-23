package com.example.barterbuddy.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barterbuddy.R;
import com.example.barterbuddy.adapters.UserItemsRecyclerViewAdapter;
import com.example.barterbuddy.interfaces.RecyclerViewInterface;
import com.example.barterbuddy.models.Item;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;



import java.util.ArrayList;

public class UserProfileHub extends AppCompatActivity implements RecyclerViewInterface {
    private Button incoming_offers_button;
    private Button your_offers_button;
    private Button your_items_button;
    private ImageView offeredItemImage;
    private ImageView wantedItemImage;
    private TextView offeredTrade;
    private TextView wantedTrade;
    private static final String TAG = "UserItemsPage";
    final long ONE_MEGABYTE = 1024 * 1024;
    private final FirebaseFirestore DB = FirebaseFirestore.getInstance();
    private final FirebaseStorage IMAGE_STORAGE = FirebaseStorage.getInstance();
    private final ArrayList<Bitmap> ITEM_IMAGES = new ArrayList<>();
    private final int REQUEST_CODE = 1002;
    private ArrayList<Item> items = new ArrayList<>();
    private String username;
    private String email;
    private StorageReference imageReference;
    // This page initally just shows a recycler upon hitting your items button it pulls up orginial page
//    with ability to add post new item


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    setContentView(R.layout.user_profile_hub);
    your_offers_button = findViewById(R.id.your_offers_button);
    incoming_offers_button= findViewById(R.id.incoming_offers_button);
    your_items_button = findViewById(R.id.your_items_button);

    username = getIntent().getStringExtra("username");
    email = getIntent().getStringExtra("email");
    //Takes you to userItemsPage
    your_items_button.setOnClickListener(
            v -> {
                Intent your_items_page = new Intent(UserProfileHub.this, UserItemsPage.class);

            }
    );

    //Takes you to your offers
        your_offers_button.setOnClickListener(
                v -> {
                    Intent your_offers_page = new Intent(UserProfileHub.this, YourOffersPage.class);
                }
        );

        //Take you to your incoming offers
        incoming_offers_button.setOnClickListener(
                v -> {
                    Intent incoming_offers_page = new Intent(UserProfileHub.this,IncomingOffersPage.class);
                }
        );


    // Set up recyclerView
    // RecyclerView setup inside this method to prevent late loading of Firebase data from
    // onComplete

    setUpItems(this);
}

    // Take arraylist of items to load recyclerView of user's items
    private void setUpItems(Context context) {
        // retrieve and insert firebase data into items
        DB.collection("users")
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

    // take position of clicked card in recyclerView to start and send correct data to itemDetailPage
    // activity
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(UserProfileHub.this, UserItemDetailPage.class);

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

