package com.example.barterbuddy.network;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.barterbuddy.models.Item;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/** Interface used to make changes to Firestore more convenient */
public class UpdateItemDocument {
  private static final String TAG = "UpdateItemDocument";

  /**
   * Set the Firestore field for the given item to active or inactive and adjust the other items
   * statuses for its owner accordingly
   *
   * @param item the item to be updated
   * @param active true if the item should be set to active
   * @return true if the item status was changed
   * @throws NullPointerException if the "email" or "title" fields are not found in the given item
   */
  public static boolean makeItemActive(@NonNull Item item, boolean active) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Check for missing fields
    if (item.getEmail() == null) {
      throw new NullPointerException("email not found in item");
    } else if (item.getTitle() == null) {
      throw new NullPointerException("title not found in item");
    }
    // update active status
    if (item.getActive() == active) {
      return false; // item status was not changed
    } else {
      item.setActive(active);
      // find the document for this item in Firebase
      String itemId = item.getEmail() + "-" + item.getTitle();
      CollectionReference userItems =
          db.collection("users").document(item.getEmail()).collection("items");
      DocumentReference docRef = userItems.document(itemId);
      // if we're setting active to "true", we must set all other items in this user's collection to
      // "false"
      if (active) {
        // update all this user's items EXCEPT the one being set to active
        userItems
            .whereNotEqualTo("title", item.getTitle())
            .get()
            .addOnSuccessListener(
                querySnapshot -> {
                  for (DocumentSnapshot snapshot : querySnapshot) {
                    snapshot
                        .getReference()
                        .update("active", false)
                        .addOnSuccessListener(e -> Log.d(TAG, "Active update succeeded"))
                        .addOnFailureListener(e -> Log.w(TAG, "Active update failed"));
                  }
                })
            .addOnFailureListener(e -> Log.w(TAG, "Query not resolved"));
      }
      // set this item's "active" to its new value
      docRef
          .update("active", active)
          .addOnSuccessListener(e -> Log.d(TAG, "Active update succeeded"))
          .addOnFailureListener(e -> Log.w(TAG, "Active update failed"));

      return true;
    }
  }
}
