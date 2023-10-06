package com.example.barterbuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.barterbuddy.Adapters.DummyAdapter;
import com.example.barterbuddy.Models.RecyclerItemModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    
  // declaring temp itemId (for testing and demonstration)
  private static final String sampleItemId = "2KQyKs0TWNc4ABmev3IP";
  private static final String samplePosterId = "lRpydQcIPq4bIo1cvcl4";

  // declaring temp buttons
  Button details_button;
  Button add_item_button;


  ArrayList<RecyclerItemModel> RecyclerItemModels = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    RecyclerView recyclerView = findViewById(R.id.Recycler);

      //sets up dummy data for now, will be used to create items using firestore data
    setUpRecyclerItems();

    DummyAdapter adapter = new DummyAdapter(this, RecyclerItemModels);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));




    // initializing temp buttons
    details_button = findViewById(R.id.go_to_details_button);
    details_button.setOnClickListener(
        view -> {
          // creates an intent that switches to the ItemDetailPage activity and passes the item id
          // to the new activity
          Intent intent = new Intent(MainActivity.this, ItemDetailPage.class);
          intent.putExtra("item_id", sampleItemId);
          intent.putExtra("poster_id", samplePosterId);
          startActivity(intent);
        });

    add_item_button = findViewById(R.id.go_to_add_item_button);
    add_item_button.setOnClickListener(
        view -> {
          Intent intent = new Intent(MainActivity.this, AddNewItem.class);
          intent.putExtra("user_id", "temp_value");
          startActivity(intent);
        });
  }
  private void setUpRecyclerItems(){
      //Arrays to hold dummy data for recycler view and creates them using recycler model class
      String[] RecyclerItemName = getResources().getStringArray(R.array.items);
      String[] RecyclerItemDescription = getResources().getStringArray(R.array.item_descriptions);
      String[] RecyclerItemPoster = getResources().getStringArray(R.array.poster);
      int[] RecyclerItemPrice = getResources().getIntArray(R.array.item_prices);
      for (int i = 0; i < RecyclerItemName.length; i ++ ){
          RecyclerItemModels.add(new RecyclerItemModel(RecyclerItemName[i], RecyclerItemDescription[i], RecyclerItemPoster[i], RecyclerItemPrice[i]));

      }
  }
}
