package com.example.barterbuddy.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.barterbuddy.R;
import com.example.barterbuddy.interfaces.RecyclerViewInterface;
import com.example.barterbuddy.models.Item;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

public class PublicItemsRecyclerAdapter
    extends RecyclerView.Adapter<PublicItemsRecyclerAdapter.MyViewHolder> {
  private final RecyclerViewInterface recyclerViewInterface;
  private final FirebaseStorage IMAGE_STORAGE_INSTANCE = FirebaseStorage.getInstance();
  Context context;
  ArrayList<Item> userItems;
  ArrayList<Bitmap> itemImages;

  public PublicItemsRecyclerAdapter(
      Context context,
      ArrayList<Item> userItems,
      RecyclerViewInterface recyclerViewInterface,
      ArrayList<Bitmap> itemImages) {
    this.context = context;
    this.userItems = userItems;
    this.recyclerViewInterface = recyclerViewInterface;
    this.itemImages = itemImages;
  }

  @NonNull
  @Override
  public PublicItemsRecyclerAdapter.MyViewHolder onCreateViewHolder(
      @NonNull ViewGroup parent, int viewType) {
    // sets look of item card
    LayoutInflater inflater = LayoutInflater.from(context);
    View view = inflater.inflate(R.layout.public_items_recycler_card, parent, false);
    return new PublicItemsRecyclerAdapter.MyViewHolder(view, recyclerViewInterface);
  }

  @Override
  public void onBindViewHolder(
          @NonNull PublicItemsRecyclerAdapter.MyViewHolder holder, int position) {
    holder.itemTitle.setText(userItems.get(position).getTitle());
    holder.itemPoster.setText(userItems.get(position).getUsername());
    holder.itemDescription.setText(userItems.get(position).getDescription());

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

    ShapeableImageView imageView;
    TextView itemTitle, itemDescription, itemPoster;

    public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
      super(itemView);

      imageView = itemView.findViewById(R.id.public_items_recycler_card);
      itemTitle = itemView.findViewById(R.id.ItemName);
      itemPoster = itemView.findViewById(R.id.user);
      itemDescription = itemView.findViewById(R.id.private_item_recycler_card_description);
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
