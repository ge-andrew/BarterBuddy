package com.example.barterbuddy.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.barterbuddy.R;
import com.example.barterbuddy.fragments.PublicItemsPageFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
  private BottomNavigationView bottomNavigationView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Fragment publicItemsFragment = new PublicItemsPageFragment();

    setCurrentFragment(publicItemsFragment);

    bottomNavigationView = findViewById(R.id.bottom_navigation_view);
    bottomNavigationView.setOnItemSelectedListener(
        item -> {
          if (item.getItemId() == R.id.menu_item_public_items) {
            setCurrentFragment(publicItemsFragment);
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
