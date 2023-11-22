package com.example.barterbuddy.network;

import android.util.Log;

import com.example.barterbuddy.models.User;
import com.example.barterbuddy.utils.FirebaseUtil;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateUserDocument {
    private static final String TAG = "UpdateUserDocument";
    public static void addRating(String userEmail, int newRating) {
        DocumentReference ratedUserDoc = FirebaseUtil.getUserReference(userEmail);
        ratedUserDoc.get().addOnSuccessListener(documentSnapshot -> {
            User ratedUser = documentSnapshot.toObject(User.class);
            if (ratedUser != null) {
                int currentAverageRating = ratedUser.getCurrentAverageRating();
                int numOfRatings = ratedUser.getNumOfTimesRated();
                int newAverageRating = (currentAverageRating + newRating / numOfRatings + 1);
                ratedUserDoc.update("currentAverageRating", newAverageRating)
                        .addOnSuccessListener(e -> Log.d(TAG, "Average rating update succeeded"))
                        .addOnFailureListener(e -> Log.w(TAG, "Average rating update failed"));
                ratedUserDoc.update("numOfTimesRated", numOfRatings + 1)
                        .addOnSuccessListener(e -> Log.d(TAG, "Rating num update succeeded"))
                        .addOnFailureListener(e -> Log.w(TAG, "Rating num update failed"));
            }
        }).addOnFailureListener(e -> Log.d(TAG, "Couldn't find user", e));
    }
}
