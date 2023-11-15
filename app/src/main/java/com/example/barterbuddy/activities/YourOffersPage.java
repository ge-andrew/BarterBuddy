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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barterbuddy.R;
import com.example.barterbuddy.adapters.TradeCardRecyclerAdapter;
import com.example.barterbuddy.interfaces.RecyclerViewInterface;
import com.example.barterbuddy.models.Item;
import com.example.barterbuddy.models.Trade;
import com.example.barterbuddy.models.TradeWithRef;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
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
    private ArrayList<TradeWithRef> userTrades = new ArrayList<>();

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

        //Loads up trade

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
        try {
            // Firebase query
            // retrieve and insert firebase data into items
            DB.collection("trades")
                    .whereEqualTo("offeringEmail", "bob@google.com")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Successfully retrieved trades");
                            Log.d(TAG, "Query results size: " + task.getResult().size());

                            if (task.getResult().isEmpty()) {
                                Toast.makeText(context, "No trades found", Toast.LENGTH_SHORT).show();
                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, "Inside loop");

                                    TradeWithRef trade = document.toObject(TradeWithRef.class);
                                    userTrades.add(trade);
                                    Log.d(TAG, "Trade added: " + trade);

                                    // Load offering item image
                                    Log.d(TAG, "Loading offering item image for trade with offering email: " + trade.getOfferingEmail());
                                    loadItem(trade.getOfferingItem());

                                    // Load poster item image
                                    Log.d(TAG, "Loading poster item image for trade with poster email: " + trade.getPosterEmail());
                                    loadItem(trade.getPosterItem());
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents: ", task.getException());
                        }

                        // set up recyclerView
                        tradeCardAdapter = new TradeCardRecyclerAdapter(context, userTrades, YourOffersPage.this);
                        tradeCardRecyclerView.setAdapter(tradeCardAdapter);
                        tradeCardAdapter.notifyDataSetChanged(); // Add this to refresh the RecyclerView.
                    });
        } catch (Exception e) {
            Log.w(TAG, "Error setting up trades: ", e);
            Toast.makeText(context, "An error occurred while setting up trades", Toast.LENGTH_SHORT).show();
        }

    }
    private void loadItem(DocumentReference itemRef) {

        if (itemRef != null) {
            itemRef.get().addOnSuccessListener(
                            documentSnapshot -> {
                                Item item = documentSnapshot.toObject(Item.class);
                                if (item != null) {
                                    email = item.getEmail();
                                    Log.d(TAG, "Loading image for item with image ID: " + item.getImageId());
                                    StorageReference imageReference = IMAGE_STORAGE
                                            .getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/barterbuddy-fb48c.appspot.com/o/users%2Fandrew%40google.com%2Fandrew%40google.com-Scissors.jpg?alt=media&token=017f8897-fb7b-4460-832a-b8ae62d91464");
                                            //.child("users/" + email + "/" + item.getImageId() + ".jpg");
                                    Log.d(TAG, "Loading image path: " + imageReference);

                                    imageReference
                                            .getBytes(ONE_MEGABYTE)
                                            .addOnSuccessListener(
                                                    bytes -> {
                                                        Bitmap itemImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                        ITEM_IMAGES.add(itemImage);
                                                        Log.d(TAG, "Successfully loaded image for item with image ID: " + item.getImageId());
                                                        tradeCardAdapter.notifyDataSetChanged(); // Refresh the RecyclerView after loading the image.
                                                    })
                                            .addOnFailureListener(e -> {
                                                Log.w(TAG, "Error getting image for item with image ID: " + item.getImageId(), e);
                                            });
                                } else {
                                    Log.w(TAG, "Item is null");
                                }
                            })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error getting item from reference", e);
                    });
        } else {
            Log.w(TAG, "Item reference is null");
        }
    }

    private boolean matchesCriteria(Trade trade, String currentEmail) {
        return trade.getOfferingEmail().equals(currentEmail);
    }



    @Override
    public void onItemClick(int position) {

    }
}

