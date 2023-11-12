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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.barterbuddy.R;
import com.example.barterbuddy.activities.AddNewItemPage;
import com.example.barterbuddy.activities.LoginPage;
import com.example.barterbuddy.activities.PersonalItemsDetailPage;
import com.example.barterbuddy.adapters.PersonalItemsRecyclerViewAdapter;
import com.example.barterbuddy.interfaces.RecyclerViewInterface;
import com.example.barterbuddy.models.Item;
import com.example.barterbuddy.utils.FirebaseUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class UserItemsPageFragment extends Fragment implements RecyclerViewInterface {

  private static final String TAG = "UserItems";
  private final ArrayList<Bitmap> ITEM_IMAGES = new ArrayList<>();
  private final FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();
  private FirebaseUser currentUser;
  private String currentUserEmail;
  private String currentUserUsername;
  private final int REQUEST_CODE = 1002;
  private FloatingActionButton add_item_button;
  private PersonalItemsRecyclerViewAdapter adapter;
  private RecyclerView personalItemsRecycler;
  private SwipeRefreshLayout personalItemsSwipeRefreshLayout;
  private ArrayList<Item> itemsFromFirestore = new ArrayList<>();
  private final MutableLiveData<ArrayList<Item>> itemsLiveData =
      new MutableLiveData<>(new ArrayList<>());
  private Context parentContext;
  private Activity parentActivity;

  // This page initally just shows a recycler upon hitting your items button it pulls up orginial
  // page
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

    add_item_button.setOnClickListener(
            buttonView -> {
              Intent intent = new Intent(parentContext, AddNewItemPage.class);
              intent.putExtra("username", currentUserUsername);
              intent.putExtra("email", currentUserEmail);

              // allows this page to refresh if an item was added
              startActivityForResult(intent, REQUEST_CODE);
            });

    // Set up recyclerView
    // RecyclerView setup inside this method to prevent late loading of Firebase data from
    // onComplete
    setUpItems();

    personalItemsSwipeRefreshLayout.setOnRefreshListener(
        () -> {
          setUpItems();
          personalItemsSwipeRefreshLayout.setRefreshing(false);
        });
  }

  // Take arraylist of items to load recyclerView of user's items
  private void setUpItems() {
    // TODO: This should show the user's items, not the items from other people
    // retrieve and insert firebase data into items
    CollectionReference userItemsCollection = FirebaseUtil.getUserItemsCollection(currentUserEmail);
    userItemsCollection
        .get()
        .addOnSuccessListener(
            task -> {
              ArrayList<Item> userItems = new ArrayList<>();
              for (QueryDocumentSnapshot document : task) {
                userItems.add(document.toObject(Item.class));
              }
              itemsLiveData.setValue(userItems);
              itemsFromFirestore = userItems;
            })
        .addOnFailureListener(
            e -> Log.d(TAG, "Error getting documents: ", e));
  }

  // take position of clicked card in recyclerView to start and send correct data to
  // PersonalItemDetailPage
  // activity
  @Override
  public void onItemClick(int position) {
    Intent intent = new Intent(parentContext, PersonalItemsDetailPage.class);
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
    add_item_button = parentActivity.findViewById(R.id.add_item_button);
  }
}
