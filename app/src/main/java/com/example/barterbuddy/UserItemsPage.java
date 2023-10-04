package com.example.barterbuddy;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

public class UserItemsPage extends AppCompatActivity  implements RecyclerViewInterface{

  private static final String TAG = "UserItemsPage";
  private final FirebaseFirestore db = FirebaseFirestore.getInstance();
  private final FirebaseStorage imageStorage = FirebaseStorage.getInstance();
  FirebaseUser user;
  Button add_item_button;
  private ArrayList<Item> items = new ArrayList<Item>();
  private CollectionReference collectionReference;
  private StorageReference imageReference;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_user_items);

    // TODO: include after authentication has been implemented
    // user = FirebaseAuth.getInstance().getCurrentUser();

    setUpItems(this);

    add_item_button = findViewById(R.id.add_item_button);
    add_item_button.setOnClickListener(
        view -> {
          Intent intent = new Intent(UserItemsPage.this, AddNewItem.class);
          intent.putExtra("user_id", "temp user id");
          startActivity(intent);
        });
  }

  private void setUpItems(Context context) {
    db.collection("users")
        .document("lRpydQcIPq4bIo1cvcl4")// TODO: change documentPath placeholder to variable
        .collection("items")
        .get()
        .addOnCompleteListener(
            new OnCompleteListener<QuerySnapshot>() {
              // retrieve and insert firebase data into items
              @Override
              public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<Item> newItems = new ArrayList<Item>();
                if (task.isSuccessful()) {
                  for (QueryDocumentSnapshot document :
                      task.getResult()) { // add data from each document (1 currently)
                    // TODO: need to map to object instead of individual fields when everything fully set up
                    newItems.add((document.toObject(Item.class)));
                  }
                } else {
                  Log.d(TAG, "Error getting documents: ", task.getException());
                }
                items = newItems;

                RecyclerView recyclerView = findViewById(R.id.recycler_view);

                UserItemsRecyclerViewAdapter adapter =
                    new UserItemsRecyclerViewAdapter(context, items, (RecyclerViewInterface) context);

                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
              }
            });
  }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(UserItemsPage.this, ItemDetailPage.class);

        intent.putExtra("item_id", items.get(position).getTitle());
        intent.putExtra("poster_id", "lRpydQcIPq4bIo1cvcl4"); // TODO: replace hardcoded with variable

        startActivity(intent);
    }
}
