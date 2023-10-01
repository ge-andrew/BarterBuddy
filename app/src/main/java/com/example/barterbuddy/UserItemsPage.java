package com.example.barterbuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class UserItemsPage extends AppCompatActivity{

    FirebaseUser user;
    ArrayList<Item> items = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_items);

        // TODO: include after authentication has been implemented
        //user = FirebaseAuth.getInstance().getCurrentUser();

        setUpItems();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        UserItemsRecyclerViewAdapter adapter = new UserItemsRecyclerViewAdapter(this, items);

        recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setUpItems() {
        // TODO: these are dummy, hardcoded values. Need to link to Firebase
        items.add(new Item("cube", "just a cube", "STRINGURIHERE", true));
        items.add(new Item("sphere", "just a sphere", "STRINGURIHERE", true));
        items.add(new Item("small child", "just a child", "STRINGURIHERE", true));
    }
}
