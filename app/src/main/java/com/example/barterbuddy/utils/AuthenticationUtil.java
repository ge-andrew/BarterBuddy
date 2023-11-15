package com.example.barterbuddy.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthenticationUtil {

    public static String getCurrentUserUsername() {
        return getCurrentUser().getDisplayName();
    }

    public static String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }

    public static FirebaseUser getCurrentUser() {
        final FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();
        FirebaseUser currentUser = AUTHENTICATION_INSTANCE.getCurrentUser();
        if (currentUser == null)
        {
            throw new NullPointerException("User not logged in");
        }
        return currentUser;
    }

}
