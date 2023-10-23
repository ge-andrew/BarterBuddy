package com.example.barterbuddy.adapters;

import android.content.Context;
import android.graphics.Bitmap;
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
import java.util.ArrayList;

public class ItemsToTradeRecyclerAdapter
    extends RecyclerView.Adapter<ItemsToTradeRecyclerAdapter.MyViewHolder> {
  private final RecyclerViewInterface recyclerViewInterface;
  Context context;
  ArrayList<Item> userItems;
  ArrayList<Bitmap> itemImages;

  public ItemsToTradeRecyclerAdapter(
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
  public ItemsToTradeRecyclerAdapter.MyViewHolder onCreateViewHolder(
      @NonNull ViewGroup parent, int viewType) {
    // sets look of item card
    LayoutInflater inflater = LayoutInflater.from(context);
    View view = inflater.inflate(R.layout.public_items_recycler_card, parent, false);
    return new ItemsToTradeRecyclerAdapter.MyViewHolder(view, recyclerViewInterface);
  }

  @Override
  public void onBindViewHolder(
      @NonNull ItemsToTradeRecyclerAdapter.MyViewHolder holder, int position) {
    holder.itemTitle.setText(userItems.get(position).getTitle());
    holder.itemDescription.setText(userItems.get(position).getDescription());
    holder.itemPoster.setText(userItems.get(position).getUsername());
    // holder.imageView.setImageBitmap(itemImages.get(position));                   // to be
    // implemented after authentication, will fail anyways
  }

  @Override
  public int getItemCount() {
    return userItems.size();
  }

  public static class MyViewHolder extends RecyclerView.ViewHolder {
    // this method very very roughly equates to onCreate() from recyclerView
    // sets up image and text views

    ImageView imageView;
    TextView itemTitle, itemDescription, itemPoster;

    public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
      super(itemView);

      imageView = itemView.findViewById(R.id.image);
      itemTitle = itemView.findViewById(R.id.ItemName);
      itemDescription = itemView.findViewById(R.id.description);
      itemPoster = itemView.findViewById(R.id.user);

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
