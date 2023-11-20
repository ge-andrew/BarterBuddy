package com.example.barterbuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.example.barterbuddy.R;
import com.example.barterbuddy.fragments.OffersFragment;
import com.example.barterbuddy.fragments.PublicItemsPageFragment;
import com.example.barterbuddy.fragments.UserItemsPageFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
  static final String TAG = "MainActivity";
  private final FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Toolbar toolbar = findViewById(R.id.menu);
    setSupportActionBar(toolbar);

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

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.profile_menu, menu);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.logout) {
      AUTHENTICATION_INSTANCE.signOut();
      Intent intent = new Intent(getApplicationContext(), LoginPage.class);
      startActivity(intent);
      finish();
    }
    return true;
  }
}
