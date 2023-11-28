package com.example.barterbuddy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.barterbuddy.R;
import com.example.barterbuddy.activities.ChatPage;
import com.example.barterbuddy.activities.LoginPage;
import com.example.barterbuddy.models.Trade;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class BackToChattingFragment extends Fragment {
  private final FirebaseFirestore FIRESTORE_INSTANCE = FirebaseFirestore.getInstance();
  private final FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();
  private Button back_to_chatting;
  private String offeringEmail;
  private String email;
  private FirebaseUser currentUser;

  BackToChattingFragment() {
    super(R.layout.back_to_chatting_page);
  }

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.back_to_chatting_page, container, false);
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

    getOtherUser();

    back_to_chatting.setOnClickListener(
        v -> {
          Intent intent = new Intent(requireContext(), ChatPage.class);
          intent.putExtra("isPoster", true);
          intent.putExtra("otherUserEmail", offeringEmail);
          startActivity(intent);
        });
  }

  private void getCurrentUser() {
    currentUser = AUTHENTICATION_INSTANCE.getCurrentUser();
  }

  private void goToLoginPage() {
    Intent intent = new Intent(getActivity(), LoginPage.class);
    startActivity(intent);
  }

  private void getCurrentUserInfo() {
    email = currentUser.getEmail();
  }

  private void getOtherUser() {
    FIRESTORE_INSTANCE
        .collection("trades")
        .whereEqualTo("posterEmail", email)
        .whereEqualTo("stateOfCompletion", "CHATTING")
        .get()
        .addOnCompleteListener(
            task -> {
              if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                  Trade trade = document.toObject(Trade.class);
                  offeringEmail = trade.getOfferingEmail();
                }
              }
            });
  }

  private void getXmlElements() {
    back_to_chatting = getActivity().findViewById(R.id.back_to_chatting);
  }
}
