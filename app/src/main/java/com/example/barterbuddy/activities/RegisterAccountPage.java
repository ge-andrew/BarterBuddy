package com.example.barterbuddy.activities;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.barterbuddy.R;
import com.example.barterbuddy.models.User;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterAccountPage extends AppCompatActivity {

  // declaring private variables
  private String username;
  private String email;
  private String password;

  // declaring views
  TextView loginTextView;
  TextInputEditText usernameEditText;
  TextInputEditText emailEditText;
  TextInputEditText passwordEditText;
  Button createButton;
  CheckBox showPasswordCheckBox;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register_account_page);

    // find views
    loginTextView = findViewById(R.id.login_text_view);
    usernameEditText = findViewById(R.id.username_text_field);
    emailEditText = findViewById(R.id.email_text_field);
    passwordEditText = findViewById(R.id.password_text_field);
    createButton = findViewById(R.id.create_button);
    showPasswordCheckBox = findViewById(R.id.show_password_checkbox);

    // change the visibility status of of the password field
    showPasswordCheckBox.setOnClickListener(
        view -> {
          if (showPasswordCheckBox.isChecked()) {
            // setting password to visible
            passwordEditText.setTransformationMethod(null);
          } else {
            // setting password to invisible
            passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
          }
        });

    // save account and save data in database
    createButton.setOnClickListener(
        view -> {
          // getting account information from input fields
          username = String.valueOf(usernameEditText.getText());
          email = String.valueOf(emailEditText.getText());
          password = String.valueOf(passwordEditText.getText());

          User newUser = new User(username, email, password);
        });

    // switch to login page
    loginTextView.setOnClickListener(view -> {});
  }
}
