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

import com.example.barterbuddy.interfaces.RecyclerViewInterface;
import com.example.barterbuddy.models.Item;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public abstract class ItemRecyclerViewAdapter
    extends RecyclerView.Adapter<ItemRecyclerViewAdapter.MyViewHolder> {
  private final RecyclerViewInterface recyclerViewInterface;
  private final FirebaseStorage IMAGE_STORAGE_INSTANCE = FirebaseStorage.getInstance();
  private final int itemRecyclerCardLayoutId;
  Context context;
  ArrayList<Item> userItems;
  ArrayList<Bitmap> itemImages;

  public ItemRecyclerViewAdapter(
          Context context,
          ArrayList<Item> userItems,
          RecyclerViewInterface recyclerViewInterface,
          int itemRecyclerCardLayoutId, ArrayList<Bitmap> itemImages) {
    this.context = context;
    this.userItems = userItems;
    this.recyclerViewInterface = recyclerViewInterface;
    this.itemRecyclerCardLayoutId = itemRecyclerCardLayoutId;
    this.itemImages = itemImages;
  }

  @NonNull
  @Override
  public ItemRecyclerViewAdapter.MyViewHolder onCreateViewHolder(
      @NonNull ViewGroup parent, int viewType) {
    // sets look of each item card
    LayoutInflater inflater = LayoutInflater.from(context);
    View view = inflater.inflate(itemRecyclerCardLayoutId, parent, false);
    return new ItemRecyclerViewAdapter.MyViewHolder(view, recyclerViewInterface);
  }

  @Override
  public void onBindViewHolder(
          @NonNull ItemRecyclerViewAdapter.MyViewHolder holder, int position) {
     setTextOnCards(holder);

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

    long FIVE_MEGABYTES = 1024 * 1024 * 5;
    imageReference
            .getBytes(FIVE_MEGABYTES)
            .addOnSuccessListener(
                    bytes -> {
                      Bitmap itemImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                      holder.imageView.setImageBitmap(itemImage);
                    });
  }

  public abstract void setTextOnCards(@NonNull ItemRecyclerViewAdapter.MyViewHolder holder);

  public void updateItems(ArrayList<Item> updatedItems) {
    this.userItems.clear();
    this.userItems.addAll(updatedItems);
    notifyDataSetChanged();
  }

  @Override
  public int getItemCount() { return userItems.size(); }

  public abstract static class MyViewHolder extends RecyclerView.ViewHolder {
    ShapeableImageView imageView;
    TextView itemTitle;

    public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
      super(itemView);

      setAllXMLViews();

      itemView.setOnClickListener(
              view -> {
                if (recyclerViewInterface != null) {
                  int pos = getAdapterPosition();

                  if (pos != RecyclerView.NO_POSITION) {
                    recyclerViewInterface.onItemClick(pos);
                  }
                }
              }
      );
    }

    public abstract void setAllXMLViews();
  }
}
