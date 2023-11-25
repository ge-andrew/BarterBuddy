package com.example.barterbuddy.network;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.barterbuddy.models.Trade;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateTradeDocument {
  private static final String TAG = "UpdateTradeDocument";

  public static void setStateToInProgress(@NonNull Trade trade) {
    setTradeState(trade, "IN_PROGRESS");
  }

  public static void setStateToCompleted(@NonNull Trade trade) {
    setTradeState(trade, "COMPLETED");
  }

  public static void setStateToCanceled(@NonNull Trade trade) {
    setTradeState(trade, "CANCELED");
  }

  public static void setStateToBartering(@NonNull Trade trade) {
    setTradeState(trade, "BARTERING");
  }

  public static void setStateToChatting(@NonNull Trade trade) {
    setTradeState(trade, "CHATTING");
  }

  public static void setTradeState(@NonNull Trade trade, String newState) {
    trade.setStateOfCompletion(newState);
    final FirebaseFirestore FIRESTORE_INSTANCE = FirebaseFirestore.getInstance();
    final String TRADE_ID = trade.getPosterEmail() + "_" + trade.getOfferingEmail();
    DocumentReference tradeDocRef = FIRESTORE_INSTANCE.collection("trades").document(TRADE_ID);
    tradeDocRef
        .update("stateOfCompletion", newState)
        .addOnSuccessListener(e -> Log.d(TAG, "State update succeeded"))
        .addOnFailureListener(e -> Log.w(TAG, "State update failed"));
  }

  public static void decrementCounteroffer(@NonNull Trade trade) {
    if(trade.getNumberCounteroffersLeft() <= 0) {
      Log.d(TAG, "Cannot send counteroffer, no counteroffers left");
      return;
    }
    else {
      trade.setNumberCounteroffersLeft(trade.getNumberCounteroffersLeft() - 1);
      final FirebaseFirestore FIRESTORE_INSTANCE = FirebaseFirestore.getInstance();
      final String TRADE_ID = trade.getPosterEmail() + "_" + trade.getOfferingEmail();
      DocumentReference tradeDocRef = FIRESTORE_INSTANCE.collection("trades").document(TRADE_ID);
      tradeDocRef
              .update("numberCounteroffersLeft", trade.getNumberCounteroffersLeft())
              .addOnSuccessListener(e -> Log.d(TAG, "numberCounteroffersLeft update succeeded"))
              .addOnFailureListener(e -> Log.w(TAG, "numberCounteroffersLeft update failed"));
    }
  }

  public static void setMoney(@NonNull Trade trade, double money) {
    trade.setMoney(money);
    final FirebaseFirestore FIRESTORE_INSTANCE = FirebaseFirestore.getInstance();
    final String TRADE_ID = trade.getPosterEmail() + "_" + trade.getOfferingEmail();
    DocumentReference tradeDocRef = FIRESTORE_INSTANCE.collection("trades").document(TRADE_ID);
    tradeDocRef
            .update("money", money)
            .addOnSuccessListener(e -> Log.d(TAG, "Money update succeeded"))
            .addOnFailureListener(e -> Log.w(TAG, "Money update failed"));
  }
}
