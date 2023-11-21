package com.example.barterbuddy.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import com.example.barterbuddy.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.barterbuddy.activities.LoginPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OffersFragment extends Fragment {
  static final String TAG = "OffersFragmentHolder";
  private final FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();
  private FirebaseUser currentUser;
  private String currentUserUsername;
  private String currentUserEmail;
  Fragment yourOffersFragment = new YourOffersFragment();
  Fragment incomingOffersFragment = new IncomingOffersFragment();

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

//    Fragment yourOffersFragment = new YourOffersFragment();
//    Fragment incomingOffersFragment = new IncomingOffersFragment();
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
            setCurrentFragment(incomingOffersFragment);
          }
          return true;
        });
  }

  @Override
  public void onResume() {
    super.onResume();

    Log.d(TAG, "onResume invoked.");

    // TODO: fix bug where wrong fragment shows on resume activity
    // select incoming, go to my items, return to offers

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
