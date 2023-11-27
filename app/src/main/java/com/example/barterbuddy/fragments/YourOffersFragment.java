package com.example.barterbuddy.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.barterbuddy.R;
import com.example.barterbuddy.activities.BarterPage;
import com.example.barterbuddy.activities.ChatPage;
import com.example.barterbuddy.activities.LoginPage;
import com.example.barterbuddy.activities.TradeUnreadPage;
import com.example.barterbuddy.adapters.TradeCardRecyclerAdapter;
import com.example.barterbuddy.interfaces.RecyclerViewInterface;
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

                    if (document
                            .toObject(TradeWithRef.class)
                            .getStateOfCompletion()
                            .equals("IN_PROGRESS")
                        || document
                            .toObject(TradeWithRef.class)
                            .getStateOfCompletion()
                            .equals("BARTERING")
                        || document
                            .toObject(TradeWithRef.class)
                            .getStateOfCompletion()
                            .equals("CHATTING")) {
                      TradeWithRef trade = document.toObject(TradeWithRef.class);
                      userTrades.add(trade);
                      Log.d(TAG, "Trade added: " + trade);
                    }
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

  private void getCurrentUser() {
    currentUser = AUTHENTICATION_INSTANCE.getCurrentUser();
  }

  private void goToLoginPage() {
    Intent intent = new Intent(getActivity().getApplicationContext(), LoginPage.class);
    startActivity(intent);
    getActivity().finish();
  }

  @Override
  public void onItemClick(int position) {
    Intent intent;
    if (userTrades.get(position).getStateOfCompletion().equals("BARTERING")) {
      intent = new Intent(getActivity(), BarterPage.class);
      intent.putExtra("isPoster", false);
    } else if (userTrades.get(position).getStateOfCompletion().equals("CHATTING")) {
      intent = new Intent(getActivity(), ChatPage.class);
      intent.putExtra("otherUserEmail", userTrades.get(position).getPosterEmail());
      intent.putExtra("isPoster", false);
    } else {
      intent = new Intent(getActivity(), TradeUnreadPage.class);
      intent.putExtra("posterEmail", userTrades.get(position).getPosterEmail());
      intent.putExtra("posterItem", userTrades.get(position).getPosterItem().getPath());
      intent.putExtra("offeringEmail", userTrades.get(position).getOfferingEmail());
      intent.putExtra("offeringItem", userTrades.get(position).getOfferingItem().getPath());
      intent.putExtra("money", userTrades.get(position).getMoney());
      intent.putExtra("stateOfCompletion", userTrades.get(position).getStateOfCompletion());
    }
    startActivity(intent);
  }
}
