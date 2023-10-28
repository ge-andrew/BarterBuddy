package com.example.barterbuddy.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.barterbuddy.R;
import com.example.barterbuddy.models.Item;
import com.example.barterbuddy.models.TradeCard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.common.returnsreceiver.qual.This;

import java.util.ArrayList;

public class IncomingOffersPage extends AppCompatActivity {
    private Button incoming_offers_button;
    private Button your_offers_button;
    private Button your_items_button;
    private Button add_item_button;
    private Button decline_button;
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
    private ArrayList<TradeCard> offeringItemIds = new ArrayList<>();
    private ArrayList<TradeCard> posterItemIds = new ArrayList<>();
    private int currentCardIndex = 0;
    private TradeCard currentTradeCard = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incoming_offers_page);
        your_offers_button = findViewById(R.id.your_offers_button);
        incoming_offers_button= findViewById(R.id.incoming_offers_button);
        your_items_button = findViewById(R.id.your_items_button);
        add_item_button = findViewById(R.id.temp_add_item);
        View includedLayout = findViewById(R.id.included_layout);
        ImageView posterImageView = includedLayout.findViewById(R.id.wanted_item_image);
        ImageView offeringImageView = includedLayout.findViewById(R.id.offered_item_image);
        Button acceptButton = findViewById(R.id.accept_button);
        decline_button = findViewById(R.id.decline_button);

        username = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("email");
        currentEmail = getCurrentUser();
        // Decline Button
        decline_button.setOnClickListener(
                v -> {
                    //Toast.makeText(this, "Trade Declined", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, currentEmail, Toast.LENGTH_LONG).show();
                    moveToNextCard();

                }
        );
        //Takes you to userItemsPage
        your_items_button.setOnClickListener(
                v -> {
                    Intent your_items_page = new Intent(IncomingOffersPage.this, UserItemsPage.class);
                    your_items_page.putExtra("username", username);
                    your_items_page.putExtra("email", email);
                    startActivity(your_items_page);

                }
        );

        //Takes you to your offers
        your_offers_button.setOnClickListener(
                v -> {
                    Intent your_offers_page = new Intent(IncomingOffersPage.this, YourOffersPage.class);
                    your_offers_page.putExtra("username", username);
                    your_offers_page.putExtra("email", email);

                }
        );

        //Take you to your incoming offers
        incoming_offers_button.setOnClickListener(
                v -> {
                    Intent incoming_offers_page = new Intent(IncomingOffersPage.this,IncomingOffersPage.class);
                    incoming_offers_page.putExtra("username", username);
                    incoming_offers_page  .putExtra("email", email);
                    startActivity(incoming_offers_page);
                }
        );



        // Set up recyclerView
        // RecyclerView setup inside this method to prevent late loading of Firebase data from
        // onComplete
        Log.d(TAG,"Before database query");
        setUpCard(this);
        Log.d(TAG,"After data base query");
    }
    //Firebase Authentication
    private String getCurrentUser() {

        currentUser = AUTHENTICATION_INSTANCE.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getEmail();
        } else {
            return null; // Handle the case where the user is not signed in.
        }
    }

//    private void goToLoginPage() {
//        Intent intent = new Intent(getApplicationContext(), LoginPage.class);
//        startActivity(intent);
//        finish();
//    }
    private void getCurrentUserInfo() {
        currentEmail = currentUser.getEmail();
    }
    private void setUpCard(Context context){
        //Firebase query
        Log.d(TAG,"Start query");

        DB.collection("items")
                .whereEqualTo("posterEmail", currentEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG,"query success ");

                        for (QueryDocumentSnapshot itemDoc : task.getResult()) {
                            // Part 2: Retrieve referenced documents and their "stringId" fields
                            DocumentReference offeringItemRef = itemDoc.getDocumentReference("offeringItem");
                            DocumentReference posterItemRef = itemDoc.getDocumentReference("posterItem");

                            // Fetch the "offeringItem" document
                            Log.d(TAG,"Load offering image url");

                            offeringItemRef.get().addOnCompleteListener(offeringTask -> {
                                if (offeringTask.isSuccessful()) {
                                    DocumentSnapshot offeringItemDoc = offeringTask.getResult();
                                    if (offeringItemDoc.exists()) {
                                        offeringItemId = offeringItemDoc.getString("imageId");

                                        Log.d(TAG, "Offering items exist with ID" + offeringItemId);
                                        // Use offeringItemId as needed
                                        TradeCard tradeCard = new TradeCard(offeringItemId, null, null, null, 0); // Assuming you have other fields
                                        offeringItemIds.add(tradeCard);
                                        //Load image
                                        displayCard(tradeCard);
                                    } else {
                                        Log.d(TAG, "OfferingItem does not exist");
                                    }
                                } else {
                                    Log.e(TAG, "Error fetching offeringItem: " + offeringTask.getException());
                                }
                            });
                            Log.d(TAG,"offering image done loading");


                            // Fetch the "posterItem" document
                            Log.d(TAG,"Being loading posterItem");

                            posterItemRef.get().addOnCompleteListener(posterTask -> {
                                if (posterTask.isSuccessful()) {
                                    Log.d(TAG,"refrence succesfully located for poster item ");
                                    DocumentSnapshot posterItemDoc = posterTask.getResult();
                                    if (posterItemDoc.exists()) {
                                        posterItemId = posterItemDoc.getString("imageId");

                                        Log.d(TAG, "posting items exist with ID" + posterItemId);
                                        // Use posterItemId as needed
                                        TradeCard tradeCard = new TradeCard(null, posterItemId, null, null, 0); // Assuming you have other fields
                                        posterItemIds.add(tradeCard);
                                        // load images
                                        displayCard(tradeCard);
                                    } else {
                                        Log.d(TAG, "POstingItem does not exist");
                                    }
                                } else {
                                    Log.e(TAG, "Error fetching postingItem: " + posterTask.getException());
                                }
                            });
                            Log.d(TAG,"Loading of poster image complete ");
                        }
                    }
                });

    }
    private void displayCurrentCard() {
        if (currentCardIndex >= 0 && currentCardIndex < offeringItemIds.size()) {
            currentTradeCard = offeringItemIds.get(currentCardIndex);
            displayCard(currentTradeCard);
        } else {
            // Handle when there are no more cards to display.
            // You can reset the index or show a message to the user.
            //Toast.makeText(this, "No more incoming trades",Toast.LENGTH_SHORT).show();
        }
    }

    private void displayCard(TradeCard tradeCard) {
        // Load and display offering image
        View includedLayout = findViewById(R.id.included_layout);
        if (tradeCard.getOfferingImageUrl() != null && !tradeCard.getOfferingImageUrl().isEmpty()) {
            ImageView offeringImageView = includedLayout.findViewById(R.id.offered_item_image);

            Log.d("File Path", "Offering Image URL: " + tradeCard.getOfferingImageUrl());

            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

            Glide.with(this)
                    .load(tradeCard.getOfferingImageUrl())
                    .apply(requestOptions)
                    .into(offeringImageView);

        }

        // Load and display poster image
        if (tradeCard.getPosterImageUrl() != null && !tradeCard.getPosterImageUrl().isEmpty()) {
            ImageView posterImageView = includedLayout.findViewById(R.id.wanted_item_image);

            Log.d("File Path", "Poster Image URL: " + tradeCard.getPosterImageUrl());

            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

            Glide.with(this)
                    .load(tradeCard.getPosterImageUrl())
                    .apply(requestOptions)
                    .into(posterImageView);


        }
    }

    // When you want to move to the next card, you can call this method.
    private void moveToNextCard() {
        currentCardIndex++; // Increment the index to move to the next card.
        displayCurrentCard();
    }




}
