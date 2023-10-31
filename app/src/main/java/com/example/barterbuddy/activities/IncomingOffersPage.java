package com.example.barterbuddy.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.barterbuddy.R;
import com.example.barterbuddy.models.Item;
import com.example.barterbuddy.models.Trade;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class IncomingOffersPage extends AppCompatActivity {
  private static final String TAG = "UserItemsPage";
  final long ONE_MEGABYTE = 1024 * 1024;
  private final FirebaseFirestore DB = FirebaseFirestore.getInstance();
  private final FirebaseStorage IMAGE_STORAGE = FirebaseStorage.getInstance();
  private final ArrayList<Bitmap> ITEM_IMAGES = new ArrayList<>();
  private final int REQUEST_CODE = 1002;
  private Button incoming_offers_button;
  private Button your_offers_button;
  private Button your_items_button;
  private Button decline_button;
  private Button accept_button;
  private ImageView offeredItemImage;
  private ImageView wantedItemImage;
  private TextView offeredTrade;
  private TextView wantedTrade;
  private String username;
  private String email;
  private StorageReference imageReference;
  private FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();
  private FirebaseUser currentUser;
  private String currentEmail;
  private String offeringItemId;
  private String posterItemId;
  private ArrayList<Trade> trades = new ArrayList<>();
  private ArrayList<String> usernames = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_incoming_offers_page);
    your_offers_button = findViewById(R.id.your_offers_button);
    incoming_offers_button = findViewById(R.id.incoming_offers_button);
    your_items_button = findViewById(R.id.your_items_button);
    View includedLayout = findViewById(R.id.included_layout);
    ImageView posterImageView = includedLayout.findViewById(R.id.poster_item_image);
    ImageView offeringImageView = includedLayout.findViewById(R.id.offering_item_image);
    accept_button = findViewById(R.id.accept_button);
    decline_button = findViewById(R.id.decline_button);

    username = getIntent().getStringExtra("username");
    email = getIntent().getStringExtra("email");

    // Firebase Auth process
    getCurrentUser();
    getCurrentUserInfo();

    // Decline Button
    decline_button.setOnClickListener(
        v -> {
          Toast.makeText(this, "Trade Declined", Toast.LENGTH_SHORT).show();
        });

    accept_button.setOnClickListener(
        v -> {
          Toast.makeText(this, "Trade Accepted", Toast.LENGTH_SHORT).show();
        });
    // Takes you to userItemsPage
    your_items_button.setOnClickListener(
        v -> {
          Intent your_items_page = new Intent(IncomingOffersPage.this, PersonalItemsPage.class);
          your_items_page.putExtra("username", username);
          your_items_page.putExtra("email", email);
          startActivity(your_items_page);
        });

    // Takes you to your offers
    your_offers_button.setOnClickListener(
        v -> {
          Intent your_offers_page = new Intent(IncomingOffersPage.this, YourOffersPage.class);
          your_offers_page.putExtra("username", username);
          your_offers_page.putExtra("email", email);
        });

    setUpCard();
    Log.d(TAG, "End of onCreate");
  }

  // Firebase Authentication
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

  private void setUpCard() {
    // Firebase query
    Log.d(TAG, "Start query");

    DB.collectionGroup("trades")
        //        .whereEqualTo("offeringEmail", "daniel@google.com")
        .get()
        .addOnCompleteListener(
            task -> {
              if (task.isSuccessful()) {
                Log.d(TAG, "query success ");

                ArrayList<DocumentReference> offeringItemDocumentReferences = new ArrayList<>();
                DocumentReference posterItemDocumentReference = null;

                for (QueryDocumentSnapshot tradeDoc : task.getResult()) {
                  // TODO: make name of documents poster_sender consistent, find way to import
                  // document directly into Trade object
                  if (tradeDoc.getString("posterEmail").equals(email)) {
                    String posterEmail = tradeDoc.getString("posterEmail");
                    String offeringEmail = tradeDoc.getString("offeringEmail");
                    double money = tradeDoc.getDouble("money");

                    trades.add(new Trade(posterEmail, null, offeringEmail, null, money));

                    offeringItemDocumentReferences.add(
                        tradeDoc.getDocumentReference("offeringItem"));
                    posterItemDocumentReference = tradeDoc.getDocumentReference("posterItem");
                  }
                }
                loadItems(offeringItemDocumentReferences, posterItemDocumentReference);
              }
            })
        .addOnFailureListener(
            task -> {
              Log.d(TAG, "Error getting documents. ");
            });
  }

  private void loadItems(
      ArrayList<DocumentReference> offeringItemDocumentReferences,
      DocumentReference posterItemDocumentReference) {
    // Fetch the "posterItem" document

    posterItemDocumentReference
        .get()
        .addOnCompleteListener(
            v -> {
              if (v.isSuccessful()) {
                DocumentSnapshot posterItemDoc = v.getResult();
                if (posterItemDoc.exists()) {
                  for (Trade t : trades) {
                    t.setPosterItem(posterItemDoc.toObject(Item.class));
                  }
                } else {
                  Log.d(TAG, "Poster Item did not exist at location.");
                }

                final int[] i = {0};

                for (DocumentReference d : offeringItemDocumentReferences) {
                  d.get()
                      .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> w) {
                          if (w.isSuccessful()) {
                            DocumentSnapshot offeringItemDoc = w.getResult();
                            if (offeringItemDoc.exists()) {
                              trades.get(i[0]).setOfferingItem(offeringItemDoc.toObject(Item.class));
                              i[0]++;
                            }
                          } else {
                            Log.d(TAG, "Poster Item did not exist at location.");
                          }
                        }
                      });
                }

              } else {
                Log.d(TAG, "Poster Item get failed.");
              }
            });

    Log.d(TAG, "End of loadItems reached");
  }

  private boolean displayNextTrade() {
    if (trades.size() == 0) {
      return false;
    }

    View includedLayout = findViewById(R.id.included_layout);

    TextView posterMoneyTextView = includedLayout.findViewById(R.id.poster_trade_money);
    TextView offeringMoneyTextView = includedLayout.findViewById((R.id.offering_trade_money));
    if (trades.get(0).getMoney() < 0) {
      posterMoneyTextView.setText(-1 * (int) trades.get(0).getMoney());
    } else {
      offeringMoneyTextView.setText((int) trades.get(0).getMoney());
    }

    //    ImageView posterImageView = includedLayout.findViewById(R.id.poster_item_image);
    //    loadAndDisplayImage(trades.get(0).getPosterItem().getImageId(), posterImageView);
    //    ImageView offeringImageView = includedLayout.findViewById(R.id.offering_item_image);
    //    loadAndDisplayImage(trades.get(0).getOfferingItem().getImageId(), offeringImageView);

    trades.remove(0);

    return true;
  }

  private void loadAndDisplayImage(String imageId, ImageView imageView) {
    Log.d(TAG, "start load and display images");

    String imageUrl = "users/" + email + "/items/" + imageId;
    RequestOptions requestOptions =
        new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

    Glide.with(this).load(imageUrl).apply(requestOptions).into(imageView);
  }
}
