package com.example.barterbuddy.network;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.barterbuddy.models.Item;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

/** Interface used to make changes to Firestore more convenient */
public class UpdateItemDocument {
//  private static final String TAG = "UpdateItemDocument";
//
//  /**
//   * Set the Firestore field for the given item to active and set all the user's other items to
//   * inactive
//   *
//   * @throws NullPointerException if the "email" or "title" fields are not found in the given item
//   */
//  public static void setAsTheActiveItem(@NonNull Item item) {
//    ensureTitleAndEmailExist(item);
//    if (!item.getActive()) {
//      // update status in the Item object
//      item.setActive(true);
//      // update status in Firestore
//      setItemFieldToActive(item);
//    }
//  }
//
//  private static void ensureTitleAndEmailExist(@NonNull Item item) {
//    if (item.getEmail() == null) {
//      throw new NullPointerException("email not found in item");
//    } else if (item.getTitle() == null) {
//      throw new NullPointerException("title not found in item");
//    }
//  }
//
//  private static void setItemFieldToActive(@NonNull Item item) {
//    // find the collection for the other items in Firebase
//    CollectionReference userItems = item.getParent();
//    // set this user's other items to inactive
//    userItems
//        .whereNotEqualTo("title", item.getTitle())
//        .get()
//        .addOnSuccessListener(
//            querySnapshot -> {
//              for (DocumentSnapshot snapshot : querySnapshot) {
//                snapshot
//                    .getReference()
//                    .update("active", false)
//                    .addOnSuccessListener(e -> Log.d(TAG, "Active update succeeded"))
//                    .addOnFailureListener(e -> Log.w(TAG, "Active update failed"));
//              }
//            })
//        .addOnFailureListener(e -> Log.w(TAG, "Query not resolved"));
//    setItemActiveField(item.getItemDocument(), true);
//  }
//
//  private static void setItemActiveField(DocumentReference itemRef, boolean active) {
//    itemRef
//            .update("active", active)
//            .addOnSuccessListener(e -> Log.d(TAG, "Active update succeeded"))
//            .addOnFailureListener(e -> Log.w(TAG, "Active update failed"));
//  }
//
//  public static void setItemToInactive(@NonNull Item item) {
//    ensureTitleAndEmailExist(item);
//    if (item.getActive()) {
//      // update status in the Item object
//      item.setActive(false);
//      // update status in Firestore
//      setItemActiveField(item.getItemDocument(), false);
//    }
//  }
}
