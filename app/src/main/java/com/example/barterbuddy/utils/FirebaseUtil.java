package com.example.barterbuddy.utils;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/** Utilities class with many helpful features for simplifying common Firebase functions */
public class FirebaseUtil {
  /*
     Citation: The idea for this class comes from Easy Tuto on YouTube: https://youtu.be/fx_WtPtT6gY?feature=shared
  */

  /**
   * Get a document reference to a Chatroom saved in Firestore
   *
   * @param chatroomId the id of the chatroom
   * @return the document reference
   */
  public static DocumentReference getChatroomReference(String chatroomId) {
    return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
  }

  /**
   * Get a collection reference to a Chatroom Message Collection saved in Firestore
   *
   * @param chatroomId the id of the chatroom
   * @return the collection reference
   */
  public static CollectionReference getChatroomMessageReference(String chatroomId) {
    return getChatroomReference(chatroomId).collection("chats");
  }

  /**
   * Get a unique chatroom id, given two chatting users
   *
   * @param userId1 the email of one of the users
   * @param userId2 the email of the other user
   * @return the id of the chatroom with these users
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
}
