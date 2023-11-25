package com.example.barterbuddy.fragments;

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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.barterbuddy.R;
import com.example.barterbuddy.activities.LoginPage;
import com.example.barterbuddy.models.Item;
import com.example.barterbuddy.models.Trade;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
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
  private final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("0.00");
  private Button your_offers_button;
  private Button your_items_button;
  private Button decline_button;
  private Button accept_button;
  private TextView posterTitleTextView;
  private TextView offeringTitleTextView;
  private TextView posterMoneyTextView;
  private TextView offeringMoneyTextView;
  private ImageView posterImageView;
  private ImageView offeringImageView;
  private String username;
  private FirebaseUser currentUser;
  private String currentEmail;
  private ArrayList<Trade> trades = new ArrayList<>();
  private ArrayList<Item> offeringItems = new ArrayList<>();
  private Item posterItem;
  private Bitmap posterItemImage;
  private int currentTrade = 0;
  private View includedLayout;
  private ArrayList<Integer> offeringItemOrder; // testing

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
    //    decline_button.setOnClickListener(
    //        v -> {
    //          setStateToCanceled(trades.get(currentTrade));
    //          if (currentTrade < trades.size()) {
    //            Toast.makeText(requireContext(), "Trade Declined", Toast.LENGTH_SHORT).show();
    //            currentTrade++;
    //            if (!displayNextTrade()) {
    //              includedLayout.findViewById(R.id.included_layout).setVisibility(View.GONE);
    //              Toast.makeText(requireContext(), "No more trades!", Toast.LENGTH_SHORT).show();
    //            }
    //          }
    //        });
    //
    //    // accept button
    //    accept_button.setOnClickListener(
    //        v -> {
    //          if (currentTrade < trades.size()) {
    //
    //            Log.d(TAG, "accept button tapped");
    //
    //            Toast.makeText(requireContext(), "Trade Accepted! Bartering begins!",
    // Toast.LENGTH_LONG)
    //                .show();
    //            setStateToBartering(trades.get(currentTrade));
    //            Intent intent = new Intent(requireContext(), BarterPage.class);
    //            intent.putExtra("isPoster", true);
    //            startActivity(intent);
    //          }
    //        });

    setUpCard();

    Log.d(TAG, "End of onCreate");
  }

  private void setUpCard() {
    getFirebaseTrades();
  }

  private void getFirebaseTrades() {
    CollectionReference collectionReference = DB.collection("trades");
    collectionReference
        .get()
        .addOnCompleteListener(
            task -> {
              if (task.isSuccessful()) {
                for (QueryDocumentSnapshot tradeDoc : task.getResult()) {
                  Trade trade = tradeDoc.toObject(Trade.class);
                  if(trade.getPosterEmail().equals(currentEmail)
                  && trade.getStateOfCompletion().equals("IN_PROGRESS")){
                    trades.add(trade);
                    Log.d(TAG, "Trade received: " + trade);
                  }
                }
                getFirebasePosterItem();
              }
            })
        .addOnFailureListener(
            v -> {
              Log.d(TAG, "Error getting trades");
            });
  }

  private void getFirebasePosterItem() {
    trades
        .get(0)
        .getPosterItem()
        .get()
        .addOnCompleteListener(
            v -> {
              if (v.isSuccessful()) {
                posterItem = v.getResult().toObject(Item.class);
                Log.d(TAG, "Poster item entered: " + posterItem);

                getAndSetPosterImage(posterItem.getEmail(), posterItem.getImageId());
                getFirebaseOfferingItems();
              }
            })
        .addOnFailureListener(
            v -> {
              Log.d(TAG, "Error getting poster item image");
            });
  }

  private void getFirebaseOfferingItems() {
    for (Trade t : trades) {
      t.getOfferingItem()
          .get()
          .addOnCompleteListener(
              v -> {
                if (v.isSuccessful()) {
                  Item item = v.getResult().toObject(Item.class);
                  offeringItems.add(item);
                  Log.d(TAG, "Offering item entered: " + item);
                  loadFragment(currentTrade);
                }
              })
          .addOnFailureListener(
              v -> {
                Log.d(TAG, "Error getting offering items ");
              });
    }
  }

  private void getAndSetPosterImage(String email, String imageId) {
    String imageUrl = "users/" + email + "/" + imageId + ".jpg";
    StorageReference reference = IMAGE_STORAGE.getReference().child(imageUrl);
    reference
        .getBytes(FIVE_MEGABYTES)
        .addOnSuccessListener(
            bytes -> {
              posterImageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            })
        .addOnFailureListener(e -> Log.w(TAG, "Error getting poster item image", e));
  }

  private void getAndSetOfferingImage(String email, String imageId) {
    Log.d(TAG, "getAndSetOfferingImage email: " + email + " imageId: " + imageId);
    String imageUrl = "users/" + email + "/" + imageId + ".jpg";
    StorageReference reference = IMAGE_STORAGE.getReference().child(imageUrl);
    reference
        .getBytes(FIVE_MEGABYTES)
        .addOnSuccessListener(
            bytes -> {
              offeringImageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            })
        .addOnFailureListener(e -> Log.w(TAG, "Error getting offering item image", e));
  }

  private void loadFragment(int currentTrade) {
    posterTitleTextView.setText(posterItem.getTitle());
    offeringTitleTextView.setText(offeringItems.get(currentTrade).getTitle());
    double money = trades.get(currentTrade).getMoney();
    if (money < 0) {
      money *= -1;
      posterMoneyTextView.setText("$" + CURRENCY_FORMAT.format(money));
      offeringMoneyTextView.setText("$0.00");
    } else {
      offeringMoneyTextView.setText("$" + CURRENCY_FORMAT.format(money));
      posterMoneyTextView.setText("$0.00");
    }

    getAndSetOfferingImage(
        trades.get(currentTrade).getOfferingEmail(), offeringItems.get(currentTrade).getImageId());
  }

  private void displayNextTrade() {}

  private void getXmlElements() {
    your_offers_button = getActivity().findViewById(R.id.your_offers_button);
    your_items_button = getActivity().findViewById(R.id.your_items_button);
    includedLayout = getActivity().findViewById(R.id.included_layout);
    posterTitleTextView = getActivity().findViewById(R.id.posterItemTitle);
    offeringTitleTextView = getActivity().findViewById(R.id.offeringItemTitle);
    posterMoneyTextView = getActivity().findViewById(R.id.poster_trade_money);
    offeringMoneyTextView = getActivity().findViewById(R.id.offering_trade_money);
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
}
