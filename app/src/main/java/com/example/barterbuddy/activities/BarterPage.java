package com.example.barterbuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.barterbuddy.R;
import com.example.barterbuddy.models.Item;
import com.example.barterbuddy.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class BarterPage extends AppCompatActivity {

    private static final String TAG = "BarterPage"; // for logging from this activity
    private final FirebaseFirestore FIRESTORE_INSTANCE = FirebaseFirestore.getInstance();
    private final FirebaseStorage IMAGE_STORAGE_INSTANCE = FirebaseStorage.getInstance();
    private final FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();
    private String username;
    private String email;
    private DocumentReference itemDocReference;
    private StorageReference imageReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barter_page);

        getCurrentUser();
        if (currentUser == null) {
            goToLoginPage();
        }
        getCurrentUserInfo();

        getXmlElements();

        // listener for abort trade button, closes trade and starts "tinder" activity

        // Check counteroffer # and whose turn it is

        // If not your turn, notify using toast, and disable money fields, counteroffer button, approve button

        // If is your turn, enable money fields, counteroffer button, approve button
            // if counteroffer button AND money fields are changed, send in counter offer, disable fields
            // if approve, update firebase, send to chat screen
    }

    private void getCurrentUser() {
        currentUser = AUTHENTICATION_INSTANCE.getCurrentUser();
    }

    private void goToLoginPage() {
        Intent intent = new Intent(getApplicationContext(), LoginPage.class);
        startActivity(intent);
        finish();
    }

    private void getCurrentUserInfo() {
        username = currentUser.getDisplayName();
        email = currentUser.getEmail();
    }

    private void getXmlElements() {

    }
}
