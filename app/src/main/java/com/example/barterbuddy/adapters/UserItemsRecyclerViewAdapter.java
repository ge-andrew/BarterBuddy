package com.example.barterbuddy.adapters;

import android.content.Context; // If errors, this import may be wrong
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.barterbuddy.R;
import com.example.barterbuddy.interfaces.RecyclerViewInterface;
import com.example.barterbuddy.models.Item;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

// This class is mainly standard setup for recyclerView

public class UserItemsRecyclerViewAdapter
    extends RecyclerView.Adapter<UserItemsRecyclerViewAdapter.MyViewHolder> {
  private final FirebaseStorage IMAGE_STORAGE_INSTANCE = FirebaseStorage.getInstance();
  private final RecyclerViewInterface recyclerViewInterface;
  Context context;
  ArrayList<Item> userItems;
  ArrayList<Bitmap> itemImages;

  // constructor
  public UserItemsRecyclerViewAdapter(
      Context context,
      ArrayList<Item> userItems,
      RecyclerViewInterface recyclerViewInterface,
      ArrayList<Bitmap> itemImages) {
    this.recyclerViewInterface = recyclerViewInterface;
    this.context = context;
    this.userItems = userItems;
    this.itemImages = itemImages;
  }

  @NonNull
  @Override
  public UserItemsRecyclerViewAdapter.MyViewHolder onCreateViewHolder(
      @NonNull ViewGroup parent, int viewType) {
    // Inflate layout and give look to each row
    LayoutInflater inflater = LayoutInflater.from(context);
    View view = inflater.inflate(R.layout.private_items_recycler_card, parent, false);
    return new UserItemsRecyclerViewAdapter.MyViewHolder(view, recyclerViewInterface);
  }

  @Override
  public void onBindViewHolder(
      @NonNull UserItemsRecyclerViewAdapter.MyViewHolder holder, int position) {
    // assigning values to each of the views as they are recycled back onto the screen
    // values from recycler_view_row.xml layout file
    // based on position of recycler view

    holder.itemTitle.setText(userItems.get(position).getTitle());

    StorageReference imageReference;
    imageReference =
        IMAGE_STORAGE_INSTANCE
            .getReference()
            .child(
                "users/"
                    + userItems.get(position).getEmail()
                    + "/"
                    + userItems.get(position).getImageId()
                    + ".jpg");

    long ONE_MEGABYTE = 1024 * 1024 * 5;
    imageReference
        .getBytes(ONE_MEGABYTE)
        .addOnSuccessListener(
            bytes -> {
              Bitmap itemImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
              holder.imageView.setImageBitmap(itemImage);
            });
  }

  @Override
  public int getItemCount() {
    return userItems.size();
  }

  public static class MyViewHolder extends RecyclerView.ViewHolder {
    // this method very very roughly equates to onCreate() from recyclerView
    // sets up image and text views

    ImageView imageView;
    TextView itemTitle;

    public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
      super(itemView);

      imageView = itemView.findViewById(R.id.image_view);
      itemTitle = itemView.findViewById(R.id.item_title);

      itemView.setOnClickListener(
          view -> {
            if (recyclerViewInterface != null) {
              int pos = getAdapterPosition();

              if (pos != RecyclerView.NO_POSITION) {
                recyclerViewInterface.onItemClick(pos);
              }
            }
          });
    }
  }
}
