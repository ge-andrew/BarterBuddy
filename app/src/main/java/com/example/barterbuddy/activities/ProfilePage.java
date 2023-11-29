package com.example.barterbuddy.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.barterbuddy.R;
import com.example.barterbuddy.utils.AuthenticationUtil;

public class ProfilePage extends AppCompatActivity {
  private ImageView backArrow;
  private TextView usernameTextView;
  private TextView emailTextView;
  private RatingBar ratingbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile_page);

    getXmlElements();

    usernameTextView.setText(String.valueOf(AuthenticationUtil.getCurrentUserUsername()));
    emailTextView.setText(String.valueOf(AuthenticationUtil.getCurrentUserEmail()));
    backArrow.setOnClickListener(view -> finish());

    // ratingbar.setRating(user.getCurrentAverageRating());
  }

  private void getXmlElements() {
    backArrow = findViewById(R.id.back_arrow);
    usernameTextView = findViewById(R.id.username);
    emailTextView = findViewById(R.id.email);
    ratingbar = findViewById(R.id.rating_bar);
  }
}
