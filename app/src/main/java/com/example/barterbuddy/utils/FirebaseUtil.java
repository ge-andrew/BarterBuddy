package com.example.barterbuddy.utils;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/** Utilities class with many helpful features for simplifying common Firebase functions */
public class FirebaseUtil {

  private static final String TAG = "Firebase Util";

  /** Get a document reference to a Chatroom saved in Firestore */
  public static DocumentReference getChatroomReference(String chatroomId) {
    return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
  }

  /** Get a collection reference to a Chatroom Message Collection saved in Firestore */
  public static CollectionReference getChatroomMessageReference(String chatroomId) {
    return getChatroomReference(chatroomId).collection("chats");
  }

  /**
   * Get a unique chatroom id, given two chatting users. User order is irrelevant.
   *
   * @param userId1 the email of one of the users
   * @param userId2 the email of the other user
   * @return the id of the chatroom between these users
   */
  public static String getChatroomId(String userId1, String userId2) {
    // We don't want to store "Alice chatting with Bob" differently from "Bob chatting with Alice",
    // so this ensures the same chatroom id is returned for either case.
    if (userId1.hashCode() < userId2.hashCode()) {
      return userId1 + "_" + userId2;
    } else {
      return userId2 + "_" + userId1;
    }
  }

  public static String getTradeId(String userId1, String userId2) {
    // TODO: This might need to be changed after the trade document gets finalized,
    //  for example if "A trades with B" must be distinct from "B trades with A"
    if (userId1.hashCode() < userId2.hashCode()) {
      return userId1 + "_" + userId2;
    } else {
      return userId2 + "_" + userId1;
    }
  }

  public static DocumentReference getTradeReference(String tradeId) {
    return FirebaseFirestore.getInstance().collection("trades").document(tradeId);
  }

  public static void deleteChatroom(String chatroomId) {
    FirebaseFirestore.getInstance()
        .collection("chatrooms")
        .document(chatroomId)
        .delete()
        .addOnSuccessListener(v -> Log.d(TAG, "Chatroom successfully deleted."))
        .addOnFailureListener(e -> Log.w(TAG, "Chatroom could not be deleted.", e));
  }

  public static DocumentReference getUserReference(String userId) {
    final FirebaseFirestore FIRESTORE_INSTANCE = FirebaseFirestore.getInstance();
    return FIRESTORE_INSTANCE.collection("users").document(userId);
  }

  public static CollectionReference getUserItemsCollection(String userId) {
    return getUserReference(userId).collection("items");
  }
}
