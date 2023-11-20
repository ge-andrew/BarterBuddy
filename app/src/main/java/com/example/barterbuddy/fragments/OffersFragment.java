package com.example.barterbuddy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.barterbuddy.R;
import com.example.barterbuddy.activities.BarterPage;
import com.example.barterbuddy.activities.IncomingOffersPage;
import com.example.barterbuddy.activities.LoginPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class OffersFragment extends Fragment {
  private static final String TAG = "OfferFragment.java";
  private final FirebaseFirestore FIRESTORE_INSTANCE = FirebaseFirestore.getInstance();
  private final FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();
  private Button incoming_offers_button;
  private Button your_offers_button;
  private FirebaseUser currentUser;
  private String currentUserUsername;
  private String currentUserEmail;

  public OffersFragment() {
    super(R.layout.fragment_offers);
  }

  @Override
  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    getCurrentUser();
    if (currentUser == null) {
      goToLoginPage();
    }
    getCurrentUserInfo();

    getXmlElements();

    // setupIncomingTradeButton();

    incoming_offers_button.setOnClickListener(
        v -> {
          setupIncomingTradesButton();
        });
  }

  private void setupIncomingTradesButton() {
    CollectionReference allTrades = FIRESTORE_INSTANCE.collection("trades");

    allTrades
        .whereEqualTo("posterEmail", currentUserEmail)
        .whereEqualTo("stateOfCompletion", "BARTERING")
        .get()
        .addOnCompleteListener(
            new OnCompleteListener<QuerySnapshot>() {
              @Override
              public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                  if (task.getResult().size() == 1) {
                    Intent intent = new Intent(getActivity(), BarterPage.class);
                    startActivity(intent);
                  } else if (task.getResult().size() > 1) {
                    Log.d(TAG, "ERROR: more than one actively bartering trade exists");
                    Intent intent = new Intent(getActivity(), BarterPage.class);
                    startActivity(intent);
                  } else {
                    Intent intent = new Intent(getActivity(), IncomingOffersPage.class);
                    startActivity(intent);
                  }
                } else {
                  Log.d(TAG, "Error getting documents: ", task.getException());
                }
              }
            });
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

  private void getXmlElements() {
    incoming_offers_button = getActivity().findViewById(R.id.incoming_offers_button);
    your_offers_button = getActivity().findViewById(R.id.your_offers_button);
  }
}
