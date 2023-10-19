package com.example.barterbuddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.barterbuddy.R;
import com.google.android.material.textfield.TextInputEditText;

public class LoginPage extends AppCompatActivity {

  private String email;
  private String password;

  TextInputEditText emailEditText;
  TextInputEditText passwordEditText;
  TextView signUpTextView;

  Button login_button;
  CheckBox showPassword;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login_page);

    getXmlElements();
    getEmailAndPassword();

    showPassword.setOnClickListener(
        view -> {
          if (showPassword.isChecked()) {
            // setting password to visible
            passwordEditText.setTransformationMethod(null);
          } else {
            // setting password to invisible
            passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
          }
        });

    signUpTextView.setOnClickListener(
        view -> {
          Intent intent = new Intent(LoginPage.this, CreateAccountPage.class);
          startActivity(intent);
        });
  }

  public void getXmlElements() {
    emailEditText = findViewById(R.id.email_text_field);
    passwordEditText = findViewById(R.id.password_text_field);
    login_button = findViewById(R.id.login_button);
    showPassword = findViewById(R.id.show_password_checkbox);
    signUpTextView = findViewById(R.id.sign_up_text_view);
  }

  public void getEmailAndPassword() {
    email = String.valueOf(emailEditText.getText());
    password = String.valueOf(passwordEditText.getText());
  }

  public boolean emailIsNull() {
    return TextUtils.isEmpty(email);
  }

  public boolean passwordIsNull() {
    return TextUtils.isEmpty(password);
  }
}
