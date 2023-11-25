package com.example.barterbuddy.fragments;

import static com.example.barterbuddy.network.UpdateTradeDocument.setStateToBartering;
import static com.example.barterbuddy.network.UpdateTradeDocument.setStateToCanceled;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.barterbuddy.R;
import com.example.barterbuddy.activities.BarterPage;
import com.example.barterbuddy.activities.LoginPage;
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
import java.text.DecimalFormat;
import java.util.ArrayList;

public class IncomingOffersFragment extends Fragment {

  private static final String TAG = "UserItemsPage";
  final long FIVE_MEGABYTES = 1024 * 1024 * 5;
  private final FirebaseFirestore DB = FirebaseFirestore.getInstance();
  private final FirebaseStorage IMAGE_STORAGE = FirebaseStorage.getInstance();
  private final FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();
  private Button your_offers_button;
  private Button your_items_button;
  private Button decline_button;
  private Button accept_button;
  private ImageView posterImageView;
  private ImageView offeringImageView;
  private String username;
  private FirebaseUser currentUser;
  private String currentEmail;
  private ArrayList<Trade> trades = new ArrayList<>();
  private int currentTrade = 0;
  private StorageReference ItemImageReference;
  private ArrayList<Item> offeringItems = new ArrayList<>();
  private Item posterItem;
  private final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("0.00");
  private View includedLayout;

  IncomingOffersFragment() {
    super(R.layout.incoming_offers_page);
  }

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.incoming_offers_page, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    getXmlElements();

    // Firebase Auth process
    getCurrentUser();
    getCurrentUserInfo();

    // Decline Button
    decline_button.setOnClickListener(
        v -> {
          setStateToCanceled(trades.get(currentTrade));
          if (currentTrade < trades.size()) {
            Toast.makeText(requireContext(), "Trade Declined", Toast.LENGTH_SHORT).show();
            currentTrade++;
            if (!displayNextTrade()) {
              includedLayout.findViewById(R.id.included_layout).setVisibility(View.GONE);
              Toast.makeText(requireContext(), "No more trades!", Toast.LENGTH_SHORT).show();
            }
          }
        });

    // accept button
    accept_button.setOnClickListener(
        v -> {
          if (currentTrade < trades.size()) {

            Log.d(TAG, "accept button tapped");

            Toast.makeText(requireContext(), "Trade Accepted! Bartering begins!", Toast.LENGTH_LONG)
                .show();
            setStateToBartering(trades.get(currentTrade));
            Intent intent = new Intent(requireContext(), BarterPage.class);
            intent.putExtra("isPoster", true);
            startActivity(intent);
          }
        });

    setUpCard();

    Log.d(TAG, "End of onCreate");
  }

  private void getXmlElements() {
    your_offers_button = getActivity().findViewById(R.id.your_offers_button);
    your_items_button = getActivity().findViewById(R.id.your_items_button);
    includedLayout = getActivity().findViewById(R.id.included_layout);
    posterImageView = includedLayout.findViewById(R.id.poster_item_image);
    offeringImageView = includedLayout.findViewById(R.id.offering_item_image);
    accept_button = getActivity().findViewById(R.id.accept_button);
    decline_button = getActivity().findViewById(R.id.decline_button);
  }

  private void getCurrentUser() {
    currentUser = AUTHENTICATION_INSTANCE.getCurrentUser();
  }

  private void goToLoginPage() {
    Intent intent = new Intent(getActivity().getApplicationContext(), LoginPage.class);
    startActivity(intent);
    getActivity().finish();
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

  private void setUpCard() {
    Log.d(TAG, "Start query");

    DB.collectionGroup("trades")
        .get()
        .addOnCompleteListener(
            task -> {
              if (task.isSuccessful()) {
                Log.d(TAG, "query success ");

                ArrayList<DocumentReference> offeringItemDocumentReferences = new ArrayList<>();
                DocumentReference posterItemDocumentReference = null;

                for (QueryDocumentSnapshot tradeDoc : task.getResult()) {
                  if (tradeDoc.getString("posterEmail").equals(currentEmail)
                      && tradeDoc.getString("stateOfCompletion").equals("IN_PROGRESS")) {
                    String posterEmail = tradeDoc.getString("posterEmail");
                    String offeringEmail = tradeDoc.getString("offeringEmail");
                    String stateOfCompletion = tradeDoc.getString("stateOfCompletion");
                    double money = tradeDoc.getDouble("money");

                    trades.add(
                        new Trade(
                            posterEmail, null, offeringEmail, null, money, stateOfCompletion));

                    offeringItemDocumentReferences.add(
                        tradeDoc.getDocumentReference("offeringItem"));
                    posterItemDocumentReference = tradeDoc.getDocumentReference("posterItem");
                  }
                }
                if (offeringItemDocumentReferences.size() > 0) {
                  loadItems(offeringItemDocumentReferences, posterItemDocumentReference);
                } else {
                  includedLayout.findViewById(R.id.included_layout).setVisibility(View.GONE);
                  Toast.makeText(getActivity(), "No new trades yet!", Toast.LENGTH_SHORT).show();
                }
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
    posterItemDocumentReference
        .get()
        .addOnCompleteListener(
            v -> {
              if (v.isSuccessful()) {
                DocumentSnapshot posterItemDoc = v.getResult();
                if (posterItemDoc.exists()) {
                  posterItem = posterItemDoc.toObject(Item.class);
                } else {
                  Log.d(TAG, "Poster Item did not exist at location.");
                }

                for (DocumentReference d : offeringItemDocumentReferences) {
                  d.get()
                      .addOnCompleteListener(
                          w -> {
                            if (w.isSuccessful()) {
                              DocumentSnapshot offeringItemDoc = w.getResult();
                              if (offeringItemDoc.exists()) {
                                offeringItems.add(offeringItemDoc.toObject(Item.class));
                                if (!displayNextTrade()) {
                                  return;
                                }
                              }
                            } else {
                              Log.d(TAG, "Poster Item did not exist at location.");
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
    if (trades.size() <= currentTrade) {
      Log.d(TAG, "No trades remaining");
      return false;
    }

    View includedLayout = getActivity().findViewById(R.id.included_layout);

    TextView posterMoneyTextView = includedLayout.findViewById(R.id.poster_trade_money);
    TextView offeringMoneyTextView = includedLayout.findViewById((R.id.offering_trade_money));

    double money = trades.get(currentTrade).getMoney();

    if (trades.get(currentTrade).getMoney() < 0) {
      money *= -1;
      posterMoneyTextView.setText("$" + CURRENCY_FORMAT.format(money));
      offeringMoneyTextView.setText("$0.00");
    } else {
      offeringMoneyTextView.setText("$" + CURRENCY_FORMAT.format(money));
      posterMoneyTextView.setText("$0.00");
    }

    TextView posterTitleTextView = includedLayout.findViewById(R.id.posterItemTitle);
    posterTitleTextView.setText(posterItem.getTitle());
    TextView offeringTitleTextView = includedLayout.findViewById(R.id.offeringItemTitle);
    offeringTitleTextView.setText(offeringItems.get(currentTrade).getTitle());

    ImageView posterImageView = includedLayout.findViewById(R.id.poster_item_image);
    loadAndDisplayImage(
        trades.get(currentTrade).getPosterEmail(), posterItem.getImageId(), posterImageView);
    ImageView offeringImageView = includedLayout.findViewById(R.id.offering_item_image);
    loadAndDisplayImage(
        offeringItems.get(currentTrade).getEmail(),
        offeringItems.get(currentTrade).getImageId(),
        offeringImageView);

    return true;
  }

  private void loadAndDisplayImage(String currentEmail, String imageId, ImageView imageView) {
    Log.d(TAG, "start load and display images");

    String imageUrl = "users/" + currentEmail + "/" + imageId + ".jpg";

    ItemImageReference = IMAGE_STORAGE.getReference().child(imageUrl);

    ItemImageReference.getBytes(FIVE_MEGABYTES)
        .addOnSuccessListener(
            bytes -> {
              Bitmap itemImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
              imageView.setImageBitmap(itemImage);
            })
        .addOnFailureListener(e -> Log.w(TAG, "Error getting item image.", e));
  }
}
