package com.example.barterbuddy.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.barterbuddy.R;
import com.example.barterbuddy.utils.AuthenticationUtil;

public class RateUserPage extends AppCompatActivity {

  private TextView usernameTextView;
  private Button submit_button;
  private Button skip_button;
  private RatingBar ratingBar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_rate_user_page);

    getXmlElements();

    // temp just for testing UI delete once backend is in place
    usernameTextView.setText(AuthenticationUtil.getCurrentUserUsername());
  }

  private void getXmlElements() {
    usernameTextView = findViewById(R.id.username);
    submit_button = findViewById(R.id.submitButton);
    skip_button = findViewById(R.id.skipButton);
    ratingBar = findViewById(R.id.ratingBar);
  }
}
