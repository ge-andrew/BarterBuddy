package com.example.barterbuddy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.barterbuddy.R;
import com.example.barterbuddy.activities.IncomingOffersPage;
import com.example.barterbuddy.activities.LoginPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class OffersFragment extends Fragment {
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

    incoming_offers_button.setOnClickListener(
            v -> {
              Intent intent = new Intent(getActivity(), IncomingOffersPage.class);
              startActivity(intent);
            }
    );
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
