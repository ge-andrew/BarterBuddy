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

import com.example.barterbuddy.Models.RecyclerItemModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

public class ItemsAvailablePage extends AppCompatActivity implements RecyclerViewInterface {

    private static final String TAG = "ItemsAvailable";
    final long ONE_MEGABYTE = 1024 * 1024;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage imageStorage = FirebaseStorage.getInstance();

    private ArrayList<Item> items = new ArrayList<Item>();
    private final ArrayList<Bitmap> itemImages = new ArrayList<Bitmap>();
    private String username;
    private String email;
    private String description;
    private CollectionReference collectionReference;
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

    }

    // Take arraylist of items to load recyclerView of user's items
    private void setUpItems(Context context) {
        db.collectionGroup("items").whereEqualTo("active", true).get()
                .addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            // retrieve and insert firebase data into items
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                ArrayList<Item> availableItems = new ArrayList<Item>();
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document :
                                            task.getResult()) { // add data from each document (1 currently)
                                        availableItems.add((document.toObject(Item.class)));
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                                items = availableItems;

                                for(Item item : items) {
                                    imageReference =
                                            imageStorage.getReference().child("users/" + email + "/" + item.getImageId() + ".jpg");
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

                                // set up recyclerView
                                RecyclerView recyclerView = findViewById(R.id.RecyclerView);
                                ItemsToTradeRecyclerAdapter adapter =
                                        new ItemsToTradeRecyclerAdapter(
                                                context, items, (RecyclerViewInterface) context, itemImages);
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                            }
                        });
    }

    // take position of clicked card in recyclerView to start and send correct data to itemDetailPage
    // activity
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(ItemsAvailablePage.this, ItemDetailPage.class);

        intent.putExtra("itemId", items.get(position).getImageId());
        intent.putExtra("username", username);
        intent.putExtra("email", email);

        startActivity(intent);
    }
}
