package com.example.barterbuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.barterbuddy.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class IncomingOffersPage extends AppCompatActivity{
    private Button incoming_offers_button;
    private Button your_offers_button;
    private Button your_items_button;
    private String username;
    private String email;
    private final FirebaseFirestore DB = FirebaseFirestore.getInstance();
    private final FirebaseStorage IMAGE_STORAGE = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incoming_offers_page);
        your_offers_button = findViewById(R.id.your_offers_button);
        incoming_offers_button= findViewById(R.id.incoming_offers_button);
        your_items_button = findViewById(R.id.your_items_button);

        your_items_button.setOnClickListener(
                v -> {
                    Intent your_items_page = new Intent(IncomingOffersPage.this, UserItemsPage.class);
                    startActivity(your_items_page);

                }
        );

        //Takes you to your offers
        your_offers_button.setOnClickListener(
                v -> {
                    Intent your_offers_page = new Intent(IncomingOffersPage.this, YourOffersPage.class);
                    startActivity(your_offers_page);
                }
        );

        //Take you to your incoming offers
        incoming_offers_button.setOnClickListener(
                v -> {
                    Intent incoming_offers_page = new Intent(IncomingOffersPage.this,IncomingOffersPage.class);
                    Toast toast = Toast.makeText(this, "Refreshing", Toast.LENGTH_LONG);
                    toast.show();
                    startActivity(incoming_offers_page);
                }
        );

        private void createOffers(Context context){

        }

    }
}
