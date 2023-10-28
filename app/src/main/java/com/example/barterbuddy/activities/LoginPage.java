package com.example.barterbuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.barterbuddy.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {

  private String email;
  private String password;
  private TextInputEditText emailEditText;
  private TextInputEditText passwordEditText;
  private TextView signUpTextView;
  private TextView invalidCredentialsWarning;
  private Button login_button;
  private CheckBox showPassword;
  private final FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();

  // Check if user is signed in and sends them to the home screen if they are
  @Override
  public void onStart() {
    super.onStart();
    FirebaseUser currentUser = AUTHENTICATION_INSTANCE.getCurrentUser();
    if (currentUser != null) {
      Intent intent = new Intent(getApplicationContext(), PublicItemsPage.class);
      startActivity(intent);
      finish();
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login_page);

    getXmlElements();

    // attempts to login a user with the given credentials
    login_button.setOnClickListener(
        v -> {
          hideInvalidCredWarning();

          getCredentials();

          if (isMissingCredentials()) {
            return;
          }

          attemptSignIn();
        });

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
    emailEditText = findViewById(R.id.email_field);
    passwordEditText = findViewById(R.id.password_field);
    login_button = findViewById(R.id.login_button);
    showPassword = findViewById(R.id.show_password_checkbox);
    signUpTextView = findViewById(R.id.sign_up_text_view);
    invalidCredentialsWarning = findViewById(R.id.invalidUsernameOrPasswordWarning);
  }

  public void getCredentials() {
    email = String.valueOf(emailEditText.getText());
    password = String.valueOf(passwordEditText.getText());
  }

  // checks if credentials are missing and informs users if they are
  public boolean isMissingCredentials() {
    if (passwordIsNull() && emailIsNull()) {
      Toast.makeText(LoginPage.this, "Missing email and password", Toast.LENGTH_SHORT).show();
      return true;
    } else if (emailIsNull()) {
      Toast.makeText(LoginPage.this, "Missing email", Toast.LENGTH_SHORT).show();
      return true;
    } else if (passwordIsNull()) {
      Toast.makeText(LoginPage.this, "Missing password", Toast.LENGTH_SHORT).show();
      return true;
    }
    return false;
  }

  public boolean emailIsNull() {
    return TextUtils.isEmpty(email);
  }

  public boolean passwordIsNull() {
    return TextUtils.isEmpty(password);
  }

  // attempts to sign in with the given credentials
  public void attemptSignIn() {
    AUTHENTICATION_INSTANCE
        .signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(
            task -> {
              if (task.isSuccessful()) {

                Toast.makeText(LoginPage.this, "Login successful", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), PublicItemsPage.class);
                startActivity(intent);
                finish();

              } else {
                showInvalidCredWarning();
              }
            });
  }

  public void showInvalidCredWarning() {
    invalidCredentialsWarning.setVisibility(View.VISIBLE);
  }

  public void hideInvalidCredWarning() {
    invalidCredentialsWarning.setVisibility(View.GONE);
  }
}
