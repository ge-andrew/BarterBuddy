package com.example.barterbuddy.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.barterbuddy.R;
import com.example.barterbuddy.activities.LoginPage;
import com.example.barterbuddy.activities.PublicItemsDetailPage;
import com.example.barterbuddy.adapters.PublicItemsRecyclerAdapter;
import com.example.barterbuddy.interfaces.RecyclerViewInterface;
import com.example.barterbuddy.models.Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class PublicItemsPageFragment extends Fragment implements RecyclerViewInterface {

  private static final String TAG = "ItemsAvailable";
  private final ArrayList<Bitmap> ITEM_IMAGES = new ArrayList<>();
  private final FirebaseFirestore FIRESTORE_INSTANCE = FirebaseFirestore.getInstance();
  private final FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();
  private FirebaseUser currentUser;
  private RecyclerView publicItemsRecycler;
  private SwipeRefreshLayout publicItemsSwipeRefreshLayout;
  private ArrayList<Item> availableItems = new ArrayList<>();
  private String currentUserUsername;
  private String currentUserEmail;
  private PublicItemsRecyclerAdapter adapter;
  private Activity parentActivity;

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_public_items, container, false);
  }


  @Override
  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    parentActivity = requireActivity();
    Context parentContext = requireContext();

    //Search bar core functions
    SearchView searchBar = parentActivity.findViewById(R.id.search_bar);
    searchBar.clearFocus();
    searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        return false;
      }

      @Override
      public boolean onQueryTextChange(String searchFilter) {
        filterList(searchFilter);
        return false;
      }
    });



    //Fire base Auth
    getCurrentUser();
    if (currentUser == null) {
      goToLoginPage();
    }
    getCurrentUserInfo();

    getXmlElements();

    adapter = new PublicItemsRecyclerAdapter(parentActivity, availableItems, this, ITEM_IMAGES);
    publicItemsRecycler.setAdapter(adapter);
    publicItemsRecycler.setLayoutManager(new LinearLayoutManager(parentContext));

    // Set up recyclerView
    // RecyclerView setup inside this method to prevent late loading of Firebase data from
    // onComplete
    setUpItems();

    publicItemsSwipeRefreshLayout.setOnRefreshListener(
        () -> {
          setUpItems();
          publicItemsSwipeRefreshLayout.setRefreshing(false);
        });
  }

  //Search Bar Filtering
  private void filterList(String searchFilter) {
    ArrayList<Item> filteredList = new ArrayList<>();
    for (Item item : availableItems) {
      if (item.getTitle().toLowerCase().contains(searchFilter.toLowerCase())) {
        filteredList.add(item);
      }
    }

    // Update the adapter with the filtered list
    adapter.updateItems(filteredList);

    // If the filtered list is empty, show a Toast message
    if (filteredList.isEmpty()) {
      Toast.makeText(parentActivity, "No items match your search", Toast.LENGTH_SHORT).show();
    }
  }


  // Take arraylist of items to load recyclerView of user's items
  private void setUpItems() {
    // retrieve and insert firebase data into items
    FIRESTORE_INSTANCE
        .collectionGroup("items")
        .whereEqualTo("active", true)
        .get()
        .addOnCompleteListener(
            task -> {
              availableItems = new ArrayList<>();
              if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                  // don't add item if is user's own item
                  if ((document.get("email") != null && document.get("username") != null)
                      && !(document.get("email").equals(currentUserEmail)
                          && document.get("username").equals(currentUserUsername))) {
                    availableItems.add((document.toObject(Item.class)));
                  }
                }
                adapter.updateItems(availableItems);
              } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
              }
            });
  }

  // take position of clicked card in recyclerView to start and send correct data to
  // PublicItemDetailPage
  // activity
  @Override
  public void onItemClick(int position) {
    Intent intent = new Intent(parentActivity, PublicItemsDetailPage.class);
    intent.putExtra("itemId", availableItems.get(position).getImageId());
    intent.putExtra("username", availableItems.get(position).getUsername());
    intent.putExtra("email", availableItems.get(position).getEmail());
    startActivity(intent);
  }

  private void getCurrentUser() {
    currentUser = AUTHENTICATION_INSTANCE.getCurrentUser();
  }

  private void goToLoginPage() {
    Intent intent = new Intent(parentActivity.getApplicationContext(), LoginPage.class);
    startActivity(intent);
    parentActivity.finish();
  }

  private void getCurrentUserInfo() {
    currentUserUsername = currentUser.getDisplayName();
    currentUserEmail = currentUser.getEmail();
  }

  private void getXmlElements() {
    publicItemsRecycler = parentActivity.findViewById(R.id.PublicItemsRecyclerView);
    publicItemsSwipeRefreshLayout = parentActivity.findViewById(R.id.public_items_swipeRefresh);
  }
}
