package com.example.barterbuddy.network;

import android.util.Log;
import com.example.barterbuddy.models.Trade;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateTrades {

  private static final String TAG = "UpdateTrades";

  public static boolean setTradeCancelled(Trade trade) {
    if (trade.isNonNull()) {
      return false;
    }
    trade.setStateOfCompletion("CANCELLED");
    setFirebaseStateOfCompletion(trade);
    return true;
  }

  public static boolean setTradeInProgress(Trade trade) {
    if (trade.isNonNull()) {
      return false;
    }
    trade.setStateOfCompletion("IN-PROGRESS");
    setFirebaseStateOfCompletion(trade);
    return true;
  }

  public static boolean setTradeNegotiating(Trade trade) {
    if (trade.isNonNull()) {
      return false;
    }
    trade.setStateOfCompletion("NEGOTIATING");
    setFirebaseStateOfCompletion(trade);
    return true;
  }

  public static boolean setTradeCompleted(Trade trade) {
    if (trade.isNonNull()) {
      return false;
    }
    trade.setStateOfCompletion("COMPLETED");
    setFirebaseStateOfCompletion(trade);
    return true;
  }

  private static void setFirebaseStateOfCompletion(Trade trade) {
    FirebaseFirestore FIRESTORE_INSTANCE = FirebaseFirestore.getInstance();

    DocumentReference tradeDocument =
        FIRESTORE_INSTANCE
            .collection("trades")
            .document(trade.getPosterEmail() + "-" + trade.getOfferingEmail());

    tradeDocument
        .update("stateOfCompletion", "NEGOTIATING")
        .addOnSuccessListener(e -> Log.d(TAG, "stateOfCompletion update succeeded"))
        .addOnFailureListener(e -> Log.w(TAG, "stateOfCompletion update failed"));
  }

  /**
   * Finalizes trade, including updating all trades that were not approved or denied to
   * stateOfCompletion = Cancelled, removes items from completed trade from database
   */
  public static void finalizeTrade(Trade trade) {
    // TODO: complete
  }
}
