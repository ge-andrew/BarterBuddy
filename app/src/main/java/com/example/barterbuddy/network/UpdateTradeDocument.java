package com.example.barterbuddy.network;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.barterbuddy.models.Trade;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

  public static boolean hasAcceptedTrade(String posterEmail) {
    final FirebaseFirestore FIRESTORE_INSTANCE = FirebaseFirestore.getInstance();

    CollectionReference allTrades = FIRESTORE_INSTANCE.collection("trades");

    allTrades
        .whereEqualTo("posterEmail", posterEmail)
        .whereEqualTo("stateOfCompletion", "BARTERING")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
              @Override
              public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                  for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    if(document.get("stateOfCompletion", String.class).equals("BARTERING")) {

                    }
                  }
                } else {
                  Log.d(TAG, "Error getting documents: ", task.getException());
                }
              }
            });

    return false;
  }
}
