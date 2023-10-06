package com.example.barterbuddy.Query;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


public class FireStoreQueryHelper {
    private FirebaseFirestore db;
    private CollectionReference collectionRef;

    public void FirestoreQueryHelper() {
        db = FirebaseFirestore.getInstance();
        collectionRef = db.collection("your_collection_name");
    }

    public Query getTop10ItemsByTimestamp() {
        return collectionRef.orderBy("timestamp").limit(10);
    }

}
