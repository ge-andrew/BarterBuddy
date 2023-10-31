package com.example.barterbuddy.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barterbuddy.R;
import com.example.barterbuddy.adapters.PersonalItemsRecyclerViewAdapter;
import com.example.barterbuddy.adapters.TradeCardRecyclerAdapter;
import com.example.barterbuddy.interfaces.RecyclerViewInterface;
import com.example.barterbuddy.models.Item;
import com.example.barterbuddy.models.Trade;
import com.example.barterbuddy.models.TradeCard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class YourOffersPage extends AppCompatActivity implements RecyclerViewInterface {
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
    private FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();
    private FirebaseUser currentUser;
    private String currentEmail;
    private String offeringItemId;
    private String posterItemId;
    private ArrayList<Trade> offeringItemIds = new ArrayList<>();
    private ArrayList<Trade> posterItemIds = new ArrayList<>();
    private ArrayList<Trade> userTrades = new ArrayList<>();

    private TradeCard currentTradeCard = null;
    // Recycler View
    private TradeCardRecyclerAdapter tradeCardAdapter;
    private RecyclerView tradeCardRecyclerView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.your_offers_page);
        your_offers_button = findViewById(R.id.your_offers_button);
        incoming_offers_button= findViewById(R.id.incoming_offers_button);
        your_items_button = findViewById(R.id.your_items_button);
        tradeCardRecyclerView = findViewById(R.id.recycler_view);

        username = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("email");
        //Firebase Auth process
        getCurrentUser();
        getCurrentUserInfo();

        //Takes you to userItemsPage
        your_items_button.setOnClickListener(
                v -> {
                    Intent your_items_page = new Intent(YourOffersPage.this, PersonalItemsPage.class);
                    your_items_page.putExtra("username", username);
                    your_items_page.putExtra("email", email);
                    startActivity(your_items_page);

                }
        );

        //Takes you to your offers
        your_offers_button.setOnClickListener(
                v -> {
                    Intent your_offers_page = new Intent(YourOffersPage.this, YourOffersPage.class);
                    your_offers_page.putExtra("username", username);
                    your_offers_page.putExtra("email", email);

                }
        );

        //Take you to your incoming offers
        incoming_offers_button.setOnClickListener(
                v -> {
                    Intent incoming_offers_page = new Intent(YourOffersPage.this,IncomingOffersPage.class);
                    incoming_offers_page.putExtra("username", username);
                    incoming_offers_page  .putExtra("email", email);
                    startActivity(incoming_offers_page);
                }
        );



        // Set up Tradecard
        tradeCardAdapter = new TradeCardRecyclerAdapter(this, userTrades, this);

            // onComplete
        tradeCardRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Log.d(TAG,"Before database query");
        setUpTrades(this);
        Log.d(TAG,"After data base query");
    }


    //Firebase Authentication
    private void getCurrentUser() {
        currentUser = AUTHENTICATION_INSTANCE.getCurrentUser();
    }

    private void goToLoginPage() {
        Intent intent = new Intent(getApplicationContext(), LoginPage.class);
        startActivity(intent);
        finish();
    }

    private void getCurrentUserInfo() {
        currentUser = AUTHENTICATION_INSTANCE.getCurrentUser();
        if (currentUser != null) {
            // The user is signed in, you can access their information
            username = currentUser.getDisplayName();
            currentEmail = currentUser.getEmail();
        } else {
            // The user is not signed in, handle this case (e.g., prompt the user to sign in)
            // You might want to implement a sign-in flow here.
            goToLoginPage();
        }
    }
    private void setUpTrades(Context context){
        //Firebase query
        // retrieve and insert firebase data into items
        DB.collection("trades")
                .whereEqualTo("offeringEmail", currentEmail)
                .get()
                .addOnCompleteListener(
                        task -> {
                            ArrayList<Item> newItems = new ArrayList<>();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document :
                                        task.getResult()) { // add data from each document (1 currently)
                                    Trade trade = document.toObject(Trade.class);
                                    userTrades.add(trade);
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
                            tradeCardAdapter = new TradeCardRecyclerAdapter(context, userTrades, YourOffersPage.this);
                            tradeCardRecyclerView.setAdapter(tradeCardAdapter);
                            tradeCardAdapter.notifyDataSetChanged(); // Add this to refresh the RecyclerView.
                        }
                );
    }
    private void loadImage(Item item) {
        imageReference = IMAGE_STORAGE.getReference()
                .child("users/" + email + "/" + item.getImageId() + ".jpg");
        imageReference
                .getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(
                        bytes -> {
                            Bitmap itemImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            ITEM_IMAGES.add(itemImage);
                            tradeCardAdapter.notifyDataSetChanged();  // Refresh the RecyclerView when an image is loaded.
                        })
                .addOnFailureListener(e -> Log.w(TAG, "Error getting image.", e));
    }



    @Override
    public void onItemClick(int position) {

    }
}

