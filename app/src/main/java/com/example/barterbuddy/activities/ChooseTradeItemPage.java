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
import com.example.barterbuddy.models.User;
import com.google.firebase.firestore.DocumentReference;
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
    final long ONE_MEGABYTE = 1024 * 1024;
    private StorageReference imageReference;
    private ArrayList<Item> items = new ArrayList<>();
    private final ArrayList<Bitmap> ITEM_IMAGES = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_trade_item);

        posterItem = (Item) getIntent().getSerializableExtra("posterItem");
        username = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("email");

        add_item_button = findViewById(R.id.add_item_button);

        // dummy data
        Item offeringItem = new Item();

        setUpItems(this);

        add_item_button.setOnClickListener(
                v -> {
                    // creates an intent that switches to the OfferTradePage activity and passes the item
                    // to the new activity
                    Intent intent = new Intent(ChooseTradeItemPage.this, AdjustTradeMoneyPage.class);
                    intent.putExtra("posterItem", posterItem);
                    intent.putExtra("offeringItem", offeringItem);
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
        Intent intent = new Intent(ChooseTradeItemPage.this, UserItemDetailPage.class);

        intent.putExtra("itemId", items.get(position).getImageId());
        intent.putExtra("username", username);
        intent.putExtra("email", email);

        startActivity(intent);
    }
}
