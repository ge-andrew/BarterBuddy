package com.example.barterbuddy.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.barterbuddy.R;
import com.example.barterbuddy.models.Item;
import com.example.barterbuddy.models.Trade;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class IncomingOffersPage extends AppCompatActivity {
    private Button incoming_offers_button;
    private Button your_offers_button;
    private Button your_items_button;
    private Button decline_button;
    private Button accept_button;
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
    private String username;
    private String email;
    private StorageReference imageReference;
    private FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();
    private FirebaseUser currentUser;
    private String currentEmail;
    private String offeringItemId;
    private String posterItemId;
    private ArrayList<Trade> trades = new ArrayList<>();
    private int currentCardIndex = 0;
    private Trade currentTradeCard = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incoming_offers_page);
        your_offers_button = findViewById(R.id.your_offers_button);
        incoming_offers_button= findViewById(R.id.incoming_offers_button);
        your_items_button = findViewById(R.id.your_items_button);
        View includedLayout = findViewById(R.id.included_layout);
        ImageView posterImageView = includedLayout.findViewById(R.id.wanted_item_image);
        ImageView offeringImageView = includedLayout.findViewById(R.id.offered_item_image);
        accept_button = findViewById(R.id.accept_button);
        decline_button = findViewById(R.id.decline_button);

        username = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("email");
        //Firebase Auth process
        getCurrentUser();
        getCurrentUserInfo();

        // Decline Button
        decline_button.setOnClickListener(
                v -> {
                    Toast.makeText(this, "Trade Declined", Toast.LENGTH_SHORT).show();
                    Intent gotoChat = new Intent(IncomingOffersPage.this, ChatPage.class);
                    gotoChat.putExtra("poster email", currentEmail);

                }
        );

        accept_button.setOnClickListener(
                v -> {
                    Toast.makeText(this, "Trade Accepted", Toast.LENGTH_SHORT).show();

                }
        );
        //Takes you to userItemsPage
        your_items_button.setOnClickListener(
                v -> {
                    Intent your_items_page = new Intent(IncomingOffersPage.this, PersonalItemsPage.class);
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
                    startActivity(your_offers_page);

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



        // Set up Tradecard
        //
        // onComplete
        Log.d(TAG,"Before database query");
        setUpCard(this);
        Log.d(TAG,"After data base query");
        displayTrade(currentTradeCard);

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

    private String getCurrentUserInfo() {
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
        return currentEmail;
    }
    private void setUpCard(Context context) {
        //Firebase query
        try {
            Log.d(TAG, "Start query");

            DB.collectionGroup("trades")
                    .whereEqualTo("posterEmail", "matt@google.com")
                    .get()
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            Log.d(TAG, "query success ");

                            for (QueryDocumentSnapshot tradeDoc : task.getResult()) {
                                // Part 2: Retrieve referenced documents and their "stringId" fields
                                // Debug
                                int docCount = task.getResult().size();
                                Log.d(TAG, "Number of docs found: " + docCount);

                                Log.d(TAG, "First step in loading line 172");

                                String posterEmail = tradeDoc.getString("posterEmail");
                                String offeringEmail = tradeDoc.getString("offeringEmail");
                                double money = tradeDoc.getDouble("money");

                                DocumentReference offeringItemRef = tradeDoc.getDocumentReference("offeringItem");
                                DocumentReference posterItemRef = tradeDoc.getDocumentReference("posterItem");

                                //Load Items
                                loadItem(posterItemRef, offeringItemRef, posterEmail, offeringEmail, money);
                            }
                        } else {
                            Log.w(TAG, "Query failed", task.getException());
                            Toast.makeText(this, "No trades available", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(this, "No trades available", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadItem(DocumentReference posterItemRef, DocumentReference offeringItemRef, String posterEmail, String offeringEmail, double money) {
        // Fetch the "posterItem" document
        posterItemRef.get().addOnCompleteListener(posterTask -> {
            if (posterTask.isSuccessful()) {
                DocumentSnapshot posterItemDoc = posterTask.getResult();
                if (posterItemDoc.exists()) {
                    Item posterItem = posterItemDoc.toObject(Item.class);

                    // Fetch the "offeringItem" document
                    offeringItemRef.get().addOnCompleteListener(offeringTask -> {
                        if (offeringTask.isSuccessful()) {
                            DocumentSnapshot offeringItemDoc = offeringTask.getResult();
                            if (offeringItemDoc.exists()) {
                                Item offeringItem = offeringItemDoc.toObject(Item.class);

                                Trade trade = new Trade(posterEmail, posterItem, offeringEmail, offeringItem, money);
                                trades.add(trade);

                                displayTrade(trade);
                            } else {
                                Log.d(TAG, "OfferingItem does not exist");
                            }
                        } else {
                            Log.e(TAG, "Error fetching offeringItem: " + offeringTask.getException());
                        }
                    });
                } else {
                    Log.d(TAG, "PostingItem does not exist");
                }
            } else {
                Log.e(TAG, "Error fetching postingItem: " + posterTask.getException());
            }
        });
    }




    private void displayTrade(Trade trade) {
        // Access trade details and display them in your UI elements
// For example:
        Log.d(TAG, "start display trade");

        View includedLayout = findViewById(R.id.included_layout);

        TextView wantedMoneyTextView = includedLayout.findViewById(R.id.wanted_value);
        TextView offeredMoneyTextView = includedLayout.findViewById((R.id.offered_value));

// Check if trade is not null before accessing its properties
        if (trade != null) {
            if (trade.getMoney() < 0) {
                double moneyValue = -trade.getMoney();
                String moneyText = String.valueOf(moneyValue);
                wantedMoneyTextView.setText(moneyText);
            } else {
                double moneyValue = trade.getMoney();
                String moneyText = String.valueOf(moneyValue);
                offeredMoneyTextView.setText(moneyText);
            }
            // Load and display images if needed
            // Example: Load poster item image
            ImageView posterImageView = includedLayout.findViewById(R.id.wanted_item_image);
            // Check if poster item is not null before accessing its properties
            if (trade.getPosterItem() != null) {
                loadAndDisplayImage(trade.getPosterItem().getImageId(), posterImageView);
            } else {
                Log.d(TAG, "Poster item is null");
            }

            // Load and display offering item image
            ImageView offeringImageView = includedLayout.findViewById(R.id.offered_item_image);
            // Check if offering item is not null before accessing its properties
            if (trade.getOfferingItem() != null) {
                loadAndDisplayImage(trade.getOfferingItem().getImageId(), offeringImageView);
            } else {
                Log.d(TAG, "Offering item is null");
            }
        } else {
            Log.d(TAG, "Trade is null");
        }
    }

    private void loadAndDisplayImage(String imageId, ImageView imageView) {
        // Load the image using your preferred method (e.g., Glide or the `getBytes` method).
        // You can adapt the image loading code you've previously used.
        // Example using Glide:

        Log.d(TAG, "start load and display images");

        String imageUrl = "users/" + email + "/items/"+ imageId; // Update the URL accordingly
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        Glide.with(this)
                .load(imageUrl)
                .apply(requestOptions)
                .into(imageView);
    }


    // When you want to move to the next card, you can call this method.





}
