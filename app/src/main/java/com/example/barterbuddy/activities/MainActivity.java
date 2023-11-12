package com.example.barterbuddy.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.barterbuddy.R;
import com.example.barterbuddy.fragments.OffersFragment;
import com.example.barterbuddy.fragments.PublicItemsPageFragment;
import com.example.barterbuddy.fragments.UserItemsPageFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
  static final String TAG = "MainActivity";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Fragment publicItemsFragment = new PublicItemsPageFragment();
    Fragment userItemsPageFragment = new UserItemsPageFragment();
    Fragment offersFragment = new OffersFragment();

    setCurrentFragment(publicItemsFragment);

    BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
    bottomNavigationView.setOnItemSelectedListener(
        item -> {
          if (item.getItemId() == R.id.menu_item_market) {
            Log.d(TAG, "Menu item opened");
            setCurrentFragment(publicItemsFragment);
          }
          if (item.getItemId() == R.id.menu_item_items) {
            setCurrentFragment(userItemsPageFragment);
          }
          if (item.getItemId() == R.id.menu_item_offers) {
            setCurrentFragment(offersFragment);
          }
          return true;
        });
  }

  private void setCurrentFragment(Fragment fragment) {
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.fl_fragment_holder, fragment)
        .commit();
  }
}
