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

  public static void sendCounteroffer(@NonNull Trade trade) {
    final FirebaseFirestore FIRESTORE_INSTANCE = FirebaseFirestore.getInstance();
    final String TRADE_ID = trade.getPosterEmail() + "_" + trade.getOfferingEmail();
    DocumentReference tradeDocRef = FIRESTORE_INSTANCE.collection("trades").document(TRADE_ID);
    tradeDocRef
        .update(
            "money",
            trade.getMoney(),
            "numberCounteroffersLeft",
            trade.getNumberCounteroffersLeft() - 1)
        .addOnSuccessListener(e -> Log.d(TAG, "Counteroffer update succeeded"))
        .addOnFailureListener(e -> Log.w(TAG, "Counteroffer update failed"));
  }
}
