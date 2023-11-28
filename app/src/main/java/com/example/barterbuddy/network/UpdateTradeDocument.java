package com.example.barterbuddy.network;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.barterbuddy.models.Trade;
import com.example.barterbuddy.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

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
    final String TRADE_ID = FirebaseUtil.getTradeId(trade);
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

  public static void closeRelatedTrades(@NonNull Trade trade) {
    final FirebaseFirestore FIRESTORE_INSTANCE = FirebaseFirestore.getInstance();
    CollectionReference collectionReference = FIRESTORE_INSTANCE.collection("trades");

    collectionReference
        .get()
        .addOnCompleteListener(
            v -> {
              for (QueryDocumentSnapshot tradeDoc : v.getResult()) {
                if ((!tradeDoc.toObject(Trade.class).equals(trade)
                    && tradeDoc.toObject(Trade.class).getPosterItem().equals(trade.getPosterItem())
                    && tradeDoc
                        .toObject(Trade.class)
                        .getPosterEmail()
                        .equals(trade.getPosterEmail()))
                || (tradeDoc.toObject(Trade.class).getPosterItem().equals(trade.getOfferingItem())) &&
                        tradeDoc.toObject(Trade.class).getPosterEmail().equals(trade.getOfferingEmail())) {
                  setStateToCanceled(tradeDoc.toObject(Trade.class));
                }
              }
            });

    collectionReference
            .get()
            .addOnCompleteListener(
                    v -> {
                      for (QueryDocumentSnapshot tradeDoc : v.getResult()) {
                        if ((!tradeDoc.toObject(Trade.class).equals(trade)
                                && tradeDoc.toObject(Trade.class).getOfferingItem().equals(trade.getOfferingItem())
                                && tradeDoc
                                .toObject(Trade.class)
                                .getOfferingEmail()
                                .equals(trade.getOfferingEmail()))
                                || (tradeDoc.toObject(Trade.class).getOfferingItem().equals(trade.getPosterItem())) &&
                                tradeDoc.toObject(Trade.class).getOfferingEmail().equals(trade.getPosterEmail())) {
                          setStateToCanceled(tradeDoc.toObject(Trade.class));
                        }
                      }
                    });

    trade.getPosterItem().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
              @Override
              public void onSuccess(Void aVoid) {
                Log.d(TAG, "Poster item successfully deleted!");
              }
            })
            .addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error deleting Poster item", e);
              }
            });

    trade.getOfferingItem().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
              @Override
              public void onSuccess(Void aVoid) {
                Log.d(TAG, "Offering item successfully deleted!");
              }
            })
            .addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error deleting Offering item", e);
              }
            });
  }
}
