package com.example.barterbuddy.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.barterbuddy.R;
import com.example.barterbuddy.activities.LoginPage;
import com.example.barterbuddy.models.Trade;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class OffersFragment extends Fragment {
  static final String TAG = "OffersFragmentHolder";
  private final FirebaseFirestore FIRESTORE_INSTANCE = FirebaseFirestore.getInstance();
  private final FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();
  private Fragment yourOffersFragment;
  private Fragment incomingOffersFragment;
  private Fragment backToBarterFragment;
  private Fragment backToChattingFragment;
  private FirebaseUser currentUser;
  private String currentUserUsername;
  private String currentUserEmail;
  private Activity rootActivity;

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    rootActivity = requireActivity();

    getCurrentUser();
    if (currentUser == null) {
      goToLoginPage();
    }
    getCurrentUserInfo();

    return inflater.inflate(R.layout.fragment_offers, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    yourOffersFragment = new YourOffersFragment();
    incomingOffersFragment = new IncomingOffersFragment();
    backToBarterFragment = new BackToBarteringFragment();
    backToChattingFragment = new BackToChattingFragment();
    setCurrentFragment(yourOffersFragment);

    BottomNavigationView bottomNavigationView = rootActivity.findViewById(R.id.top_navigation_view);
    bottomNavigationView.setOnItemSelectedListener(
        item -> {
          if (item.getItemId() == R.id.menu_item_your_offers) {
            Log.d(TAG, "Menu item opened");
            setCurrentFragment(yourOffersFragment);
          }
          if (item.getItemId() == R.id.menu_item_incoming_offers) {
            Log.d(TAG, "Menu item opened");
            showBarterOrIncomingTradesFragment();
          }
          return true;
        });
  }

  private void showBarterOrIncomingTradesFragment() {
    CollectionReference allTrades = FIRESTORE_INSTANCE.collection("trades");

    allTrades
            .whereEqualTo("posterEmail", currentUserEmail)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
              @Override
              public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                  Log.d(TAG, task.getResult().toString());
                  for (QueryDocumentSnapshot document : task.getResult()) {
                    Trade trade = document.toObject(Trade.class);
                    if(trade.getStateOfCompletion().equals("BARTERING")) {
                      setCurrentFragment(backToBarterFragment);
                      return;
                    }
                    else if(trade.getStateOfCompletion().equals("CHATTING")) {
                      setCurrentFragment(backToChattingFragment);
                      return;
                    }
                  }
                  setCurrentFragment(incomingOffersFragment);
                } else {
                  Log.d(TAG, "Error getting trades", task.getException());
                }
              }
            });
  }

  private void setCurrentFragment(Fragment fragment) {
    getChildFragmentManager()
        .beginTransaction()
        .replace(R.id.fl_offers_fragments_holder, fragment)
        .commit();
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
    currentUserUsername = currentUser.getDisplayName();
    currentUserEmail = currentUser.getEmail();
  }
}
