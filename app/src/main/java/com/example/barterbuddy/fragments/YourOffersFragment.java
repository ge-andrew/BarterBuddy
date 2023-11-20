package com.example.barterbuddy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barterbuddy.R;
import com.example.barterbuddy.activities.LoginPage;
import com.example.barterbuddy.adapters.TradeCardRecyclerAdapter;
import com.example.barterbuddy.interfaces.RecyclerViewInterface;
import com.example.barterbuddy.models.Trade;
import com.example.barterbuddy.models.TradeWithRef;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class YourOffersFragment extends Fragment implements RecyclerViewInterface {
  private static final String TAG = "UserItemsPage";
  private final FirebaseFirestore DB = FirebaseFirestore.getInstance();
  private final FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();
  private FirebaseUser currentUser;
  private ArrayList<TradeWithRef> userTrades = new ArrayList<>();
  private Context rootContext;

  // Recycler View
  private TradeCardRecyclerAdapter tradeCardAdapter;

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    rootContext = requireContext();
    return inflater.inflate(R.layout.your_offers_page, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    getCurrentUser();
    if (currentUser == null) {
      goToLoginPage();
    }

    RecyclerView tradeCardRecyclerView = view.findViewById(R.id.recycler_view);

    // Set up Tradecard
    tradeCardAdapter = new TradeCardRecyclerAdapter(rootContext, userTrades, this);
    tradeCardRecyclerView.setAdapter(tradeCardAdapter);
    tradeCardRecyclerView.setLayoutManager(new LinearLayoutManager(rootContext));

    Log.d(TAG, "Before database query");
    setUpTrades(rootContext);
    Log.d(TAG, "After data base query");
  }

  private void setUpTrades(Context context) {
    try {
      // Firebase query
      // retrieve and insert firebase data into items
      DB.collection("trades")
          .whereEqualTo("offeringEmail", currentUser.getEmail())
          .get()
          .addOnSuccessListener(
              task -> {
                userTrades = new ArrayList<>();

                Log.d(TAG, "Successfully retrieved trades");
                Log.d(TAG, "Query results size: " + task.size());

                if (task.isEmpty()) {
                  Toast.makeText(context, "No trades found", Toast.LENGTH_SHORT).show();
                } else {
                  for (QueryDocumentSnapshot document : task) {
                    Log.d(TAG, "Inside loop");

                    TradeWithRef trade = document.toObject(TradeWithRef.class);
                    userTrades.add(trade);
                    Log.d(TAG, "Trade added: " + trade);

//                    // Load offering item image
//                    Log.d(
//                        TAG,
//                        "Loading offering item image for trade with offering email: "
//                            + trade.getOfferingEmail());
//                    loadItem(trade.getOfferingItem());
//
//                    // Load poster item image
//                    Log.d(
//                        TAG,
//                        "Loading poster item image for trade with poster email: "
//                            + trade.getPosterEmail());
//                    loadItem(trade.getPosterItem());
                  }
                }
                tradeCardAdapter.updateTrades(userTrades);
              })
          .addOnFailureListener(e -> Log.w(TAG, "Error getting documents: ", e));
    } catch (Exception e) {
      Log.w(TAG, "Error setting up trades: ", e);
      Toast.makeText(context, "An error occurred while setting up trades", Toast.LENGTH_SHORT)
          .show();
    }
  }

//  private void loadItem(DocumentReference itemRef) {
//
//    if (itemRef != null) {
//      itemRef
//          .get()
//          .addOnSuccessListener(
//              documentSnapshot -> {
//                Item item = documentSnapshot.toObject(Item.class);
//                if (item != null) {
//                  email = item.getEmail();
//                  Log.d(TAG, "Loading image for item with image ID: " + item.getImageId());
//                  StorageReference imageReference =
//                      IMAGE_STORAGE
//                          .getReference()
//                          .child("users/" + email + "/" + item.getImageId() + ".jpg");
//                  Log.d(TAG, "Loading image path: " + imageReference);
//
//                  imageReference
//                      .getBytes(ONE_MEGABYTE)
//                      .addOnSuccessListener(
//                          bytes -> {
//                            Bitmap itemImage =
//                                BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                            ITEM_IMAGES.add(itemImage);
//                            Log.d(
//                                TAG,
//                                "Successfully loaded image for item with image ID: "
//                                    + item.getImageId());
//                            tradeCardAdapter
//                                .notifyDataSetChanged(); // Refresh the RecyclerView after loading
//                            // the image.
//                          })
//                      .addOnFailureListener(
//                          e ->
//                              Log.w(
//                                  TAG,
//                                  "Error getting image for item with image ID: "
//                                      + item.getImageId(),
//                                  e));
//                } else {
//                  Log.w(TAG, "Item is null");
//                }
//              })
//          .addOnFailureListener(e -> Log.w(TAG, "Error getting item from reference", e));
//    } else {
//      Log.w(TAG, "Item reference is null");
//    }
//  }

  private boolean matchesCriteria(Trade trade, String currentEmail) {
    return trade.getOfferingEmail().equals(currentEmail);
  }

  private void getCurrentUser() {
    currentUser = AUTHENTICATION_INSTANCE.getCurrentUser();
  }

  private void goToLoginPage() {
    Intent intent = new Intent(getActivity().getApplicationContext(), LoginPage.class);
    startActivity(intent);
    getActivity().finish();
  }

  @Override
  public void onItemClick(int position) {}
}
