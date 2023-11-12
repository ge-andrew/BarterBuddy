package com.example.barterbuddy.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.barterbuddy.R;
import com.example.barterbuddy.activities.LoginPage;
import com.example.barterbuddy.activities.PublicItemsDetailPage;
import com.example.barterbuddy.adapters.PersonalItemsRecyclerViewAdapter;
import com.example.barterbuddy.interfaces.RecyclerViewInterface;
import com.example.barterbuddy.models.Item;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UserItemsPageFragment extends Fragment implements RecyclerViewInterface {

  private static final String TAG = "UserItems";
  private final FirebaseFirestore FIRESTORE_INSTANCE = FirebaseFirestore.getInstance();
  private final ArrayList<Bitmap> ITEM_IMAGES = new ArrayList<>();
  private final FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();
  private FirebaseUser currentUser;
  private String currentUserEmail;
  private String currentUserUsername;
  private PersonalItemsRecyclerViewAdapter adapter;
  private RecyclerView personalItemsRecycler;
  private SwipeRefreshLayout personalItemsSwipeRefreshLayout;
  private ArrayList<Item> itemsFromFirestore = new ArrayList<>();
  private final MutableLiveData<ArrayList<Item>> itemsLiveData = new MutableLiveData<>(new ArrayList<>());
  private Context parentContext;
  private Activity parentActivity;
  // This page initally just shows a recycler upon hitting your items button it pulls up orginial page
//    with ability to add post new item

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_user_items, container, false);

    parentContext = requireContext();
    parentActivity = requireActivity();

    // Initialize RecyclerView and Adapter
    personalItemsRecycler = view.findViewById(R.id.recycler_view);
    personalItemsSwipeRefreshLayout = view.findViewById(R.id.user_items_swipeRefresh);
    adapter =
        new PersonalItemsRecyclerViewAdapter(parentContext, new ArrayList<>(), this, ITEM_IMAGES);
    personalItemsRecycler.setAdapter(adapter);
    personalItemsRecycler.setLayoutManager(new LinearLayoutManager(parentContext));

    // Observe changes in the LiveData and update the adapter
    itemsLiveData.observe(
        getViewLifecycleOwner(),
        items -> {
          adapter.updateItems(items);
          adapter.notifyDataSetChanged();
        });

    return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    getCurrentUser();
    if (currentUser == null) {
      goToLoginPage();
    }
    getCurrentUserInfo();

    getXmlElements();

    // Set up recyclerView
    // RecyclerView setup inside this method to prevent late loading of Firebase data from
    // onComplete
    setUpItems(parentActivity);

    personalItemsSwipeRefreshLayout.setOnRefreshListener(
        () -> {
          setUpItems(parentActivity);
          personalItemsSwipeRefreshLayout.setRefreshing(false);
        });
  }

  // Take arraylist of items to load recyclerView of user's items
  private void setUpItems(Context context) {
    // TODO: This should show the user's items, not the items from other people
    // retrieve and insert firebase data into items
    FIRESTORE_INSTANCE
        .collectionGroup("items")
        .whereEqualTo("active", true)
        .get()
        .addOnCompleteListener(
            task -> {
              ArrayList<Item> availableItems = new ArrayList<>();
              if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                  // don't add item if is user's own item
                  if ((document.get("email") != null && document.get("username") != null)
                      && !(document.get("email").equals(currentUserEmail)
                          && document.get("username").equals(currentUserUsername))) {
                    availableItems.add((document.toObject(Item.class)));
                  }
                }
                itemsLiveData.setValue(availableItems);
                itemsFromFirestore = availableItems;
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
    Intent intent = new Intent(parentContext, PublicItemsDetailPage.class);
    intent.putExtra("itemId", itemsFromFirestore.get(position).getImageId());
    intent.putExtra("username", itemsFromFirestore.get(position).getUsername());
    intent.putExtra("email", itemsFromFirestore.get(position).getEmail());
    startActivity(intent);
  }

  private void getCurrentUser() {
    currentUser = AUTHENTICATION_INSTANCE.getCurrentUser();
  }

  private void goToLoginPage() {
    Intent intent = new Intent(parentContext.getApplicationContext(), LoginPage.class);
    startActivity(intent);
    parentActivity.finish();
  }

  private void getCurrentUserInfo() {
    currentUserUsername = currentUser.getDisplayName();
    currentUserEmail = currentUser.getEmail();
  }

  private void getXmlElements() {
    personalItemsRecycler = parentActivity.findViewById(R.id.recycler_view);
    personalItemsSwipeRefreshLayout = parentActivity.findViewById(R.id.user_items_swipeRefresh);
  }
}
