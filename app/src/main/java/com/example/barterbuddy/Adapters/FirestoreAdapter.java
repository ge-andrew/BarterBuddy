package com.example.barterbuddy.Adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;

import com.example.barterbuddy.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for displaying the results of a Firestore [Query].
 * <p>
 * Note that this class forgoes some efficiency to gain simplicity. For example, the result of
 * [DocumentSnapshot.toObject] is not cached so the same object may be deserialized
 * many times as the user scrolls.
 */
public abstract class FirestoreAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>
        implements EventListener<QuerySnapshot> {

    private Query query;
    private ListenerRegistration registration;
    private final List<DocumentSnapshot> snapshots = new ArrayList<>();
    private final int layoutRedId;

    public FirestoreAdapter(Query query) {

        this.query = query;
        this.layoutRedId = R.layout.recyclerow;
    }

    public void startListening() {
        if (registration == null) {
            registration = query.addSnapshotListener(this);
        }
    }

    public void stopListening() {
        if (registration != null) {
            registration.remove();
            registration = null;
        }

        snapshots.clear();
        notifyDataSetChanged();
    }

    public void setQuery(Query query) {
        // Stop listening
        stopListening();

        // Clear existing data
        snapshots.clear();
        notifyDataSetChanged();

        // Listen to new query
        this.query = query;
        startListening();
    }

    private void onDocumentAdded(DocumentChange change) {
        snapshots.add(change.getNewIndex(), change.getDocument());
        notifyItemInserted(change.getNewIndex());
    }

    private void onDocumentModified(DocumentChange change) {
        if (change.getOldIndex() == change.getNewIndex()) {
            // Item changed but remained in the same position
            snapshots.set(change.getOldIndex(), change.getDocument());
            notifyItemChanged(change.getOldIndex());
        } else {
            // Item changed and changed position
            snapshots.remove(change.getOldIndex());
            snapshots.add(change.getNewIndex(), change.getDocument());
            notifyItemMoved(change.getOldIndex(), change.getNewIndex());
        }
    }

    private void onDocumentRemoved(DocumentChange change) {
        snapshots.remove(change.getOldIndex());
        notifyItemRemoved(change.getOldIndex());
    }

    @Override
    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
        // Handle errors
        if (e != null) {
            Log.w(TAG, "onEvent:error", e);
            return;
        }

        // Dispatch the event
        if (documentSnapshots != null) {
            for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
                switch (change.getType()) {
                    case ADDED:
                        onDocumentAdded(change);
                        break;
                    case MODIFIED:
                        onDocumentModified(change);
                        break;
                    case REMOVED:
                        onDocumentRemoved(change);
                        break;
                }
            }
        }

        onDataChanged();
    }

    public void onError(FirebaseFirestoreException e) {
        Log.w(TAG, "onError", e);
    }

    public void onDataChanged() {
    }

    @Override
    public int getItemCount() {
        return snapshots.size();
    }

    protected DocumentSnapshot getSnapshot(int index) {
        return snapshots.get(index);
    }

    private static final String TAG = "FirestoreAdapter";
}
