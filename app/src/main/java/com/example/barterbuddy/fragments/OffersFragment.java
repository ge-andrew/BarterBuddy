package com.example.barterbuddy.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.barterbuddy.R;
import com.google.android.material.navigation.NavigationView;

public class OffersFragment extends Fragment {
  static final String TAG = "OffersFragmentHolder";

  private Activity rootActivity;
  private Context rootContext;

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    rootActivity = requireActivity();
    rootContext = requireContext();

    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    Fragment yourOffersFragment = new YourOffersFragment();
    Fragment incomingOffersFragment = new IncomingOffersFragment();
    setCurrentFragment(yourOffersFragment);

    NavigationView topNavigationView = rootActivity.findViewById(R.id.top_navigation_view);
    topNavigationView.setNavigationItemSelectedListener(
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

  private void setCurrentFragment(Fragment fragment) {
    getChildFragmentManager()
        .beginTransaction()
        .replace(R.id.fl_fragment_holder, fragment)
        .commit();
  }
}
