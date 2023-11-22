package com.example.barterbuddy.network;

import com.example.barterbuddy.utils.FirebaseUtil;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateUserDocument {
    public static void addRating(String userEmail, int newRating) {
        final FirebaseFirestore FIRESTORE_INSTANCE = FirebaseFirestore.getInstance();

        DocumentReference ratedUser = FirebaseUtil.getUserReference(userEmail);

    }
}
