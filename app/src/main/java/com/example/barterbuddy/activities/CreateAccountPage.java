package com.example.barterbuddy.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.barterbuddy.R;
import com.example.barterbuddy.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class CreateAccountPage extends AppCompatActivity {

  // declaring firebase variables
  private final FirebaseFirestore DATABASE_INSTANCE = FirebaseFirestore.getInstance();
  private final FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();
  FirebaseAuth userAuthentication;

  // declaring variables for holding user data
  private String username;
  private String email;
  private String password;

  // declaring views
  TextView loginTextView;
  TextView emailWarningTextView;
  TextView usernameWarningTextView;
  TextInputEditText usernameEditText;
  TextInputEditText emailEditText;
  TextInputEditText passwordEditText;
  Button createButton;
  CheckBox showPassword;
  ImageView backArrow;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_account_page);

    // initializing authenticator variable
    userAuthentication = AUTHENTICATION_INSTANCE;

    // find views
    loginTextView = findViewById(R.id.login_text_view);
    emailWarningTextView = findViewById(R.id.email_warning);
    usernameWarningTextView = findViewById(R.id.username_warning);
    usernameEditText = findViewById(R.id.username_text_field);
    emailEditText = findViewById(R.id.email_text_field);
    passwordEditText = findViewById(R.id.password_text_field);
    createButton = findViewById(R.id.create_button);
    showPassword = findViewById(R.id.show_password_checkbox);
    backArrow = findViewById(R.id.back_arrow);

    // change the visibility status of the password field
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

    // attempts to create account
    createButton.setOnClickListener(
        view -> {
          hideEmailWarning();
          hideUsernameWarning();

          // getting account information from input fields
          username = String.valueOf(usernameEditText.getText());
          email = String.valueOf(emailEditText.getText());
          password = String.valueOf(passwordEditText.getText());

          // create user object to store data
          User newUser = new User(username, email, password);

          if (missingUserInfo(newUser)) {
            return;
          }

          // checking if username already exists and attempting to create an account if it does not
          Query query =
              DATABASE_INSTANCE.collection("users").whereEqualTo("username", newUser.getUsername());
          query
              .get()
              .addOnCompleteListener(
                  task -> {
                    if (task.isSuccessful()) {
                      boolean usernameAlreadyExists = false;
                      for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.get("username") != null) {
                          usernameAlreadyExists = true;
                        }
                      }
                      if (!usernameAlreadyExists) {
                        createAccount(newUser);
                      } else {
                        showUsernameWarning();
                      }
                    }
                  });
        });

    // goes back to login page
    backArrow.setOnClickListener(view -> finish());

    // goes back to login page
    loginTextView.setOnClickListener(view -> finish());
  }

  // check if information is missing
  public boolean missingUserInfo(User user) {
    // check if all information was provided
    if (TextUtils.isEmpty(user.getUsername()) && TextUtils.isEmpty(user.getEmail())) {
      Toast.makeText(CreateAccountPage.this, "Missing information", Toast.LENGTH_SHORT).show();
      return true;
    } else if (TextUtils.isEmpty(user.getEmail()) && TextUtils.isEmpty(user.getPassword())) {
      Toast.makeText(CreateAccountPage.this, "Missing information", Toast.LENGTH_SHORT).show();
      return true;
    } else if (TextUtils.isEmpty(user.getUsername()) && TextUtils.isEmpty(user.getPassword())) {
      Toast.makeText(CreateAccountPage.this, "Missing information", Toast.LENGTH_SHORT).show();
      return true;
    } else if (TextUtils.isEmpty(user.getUsername())) {
      Toast.makeText(CreateAccountPage.this, "Missing username", Toast.LENGTH_SHORT).show();
      return true;
    } else if (TextUtils.isEmpty(user.getEmail())) {
      Toast.makeText(CreateAccountPage.this, "Missing email", Toast.LENGTH_SHORT).show();
      return true;
    } else if (TextUtils.isEmpty(user.getPassword())) {
      Toast.makeText(CreateAccountPage.this, "Missing password", Toast.LENGTH_SHORT).show();
      return true;
    } else {
      return false;
    }
  }

  public void createAccount(User user) {
    userAuthentication
        .createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
        .addOnCompleteListener(
            task -> {
              if (task.isSuccessful()) {
                showAccountedCreatedToast();
                addUserToFirestore(user);
                finish();
              } else {
                // If sign in fails, display a message to the user.
                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                  showEmailWarning();
                } else {
                  showAccountFailedToCreateToast();
                }
              }
            });
  }

  public void showAccountedCreatedToast() {
    Toast.makeText(CreateAccountPage.this, "Account created!", Toast.LENGTH_SHORT).show();
  }

  public void showAccountFailedToCreateToast() {
    Toast.makeText(CreateAccountPage.this, "Account creation failed.", Toast.LENGTH_SHORT).show();
  }

  public void addUserToFirestore(User user) {
    // create reference to new user
    DocumentReference userReference =
        DATABASE_INSTANCE.collection("users").document(user.getEmail());

    // store user data in Firestore
    userReference.set(user);
  }

  public void showUsernameWarning() {
    usernameWarningTextView.setVisibility(View.VISIBLE);
  }

  public void hideUsernameWarning() {
    usernameWarningTextView.setVisibility(View.GONE);
  }

  public void showEmailWarning() {
    emailWarningTextView.setVisibility(View.VISIBLE);
  }

  public void hideEmailWarning() {
    emailWarningTextView.setVisibility(View.GONE);
  }
}
