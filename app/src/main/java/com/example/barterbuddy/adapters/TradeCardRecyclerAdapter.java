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
import com.example.barterbuddy.models.TradeWithRef;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class TradeCardRecyclerAdapter extends RecyclerView.Adapter<TradeCardRecyclerAdapter.MyViewHolder> {
  private final RecyclerViewInterface recyclerViewInterface;
  private final FirebaseStorage IMAGE_STORAGE_INSTANCE = FirebaseStorage.getInstance();
  Context context;
  ArrayList<TradeWithRef> userTrades;
  private DecimalFormat currencyFormat = new DecimalFormat("0.00");

  public TradeCardRecyclerAdapter(
          Context context,
          ArrayList<TradeWithRef> userTrades,
          RecyclerViewInterface recyclerViewInterface) {
    this.context = context;
    this.userTrades = userTrades;
    this.recyclerViewInterface = recyclerViewInterface;
  }

  @NonNull
  @Override
  public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    // Sets the look of the item card
    LayoutInflater inflater = LayoutInflater.from(context);
    View view = inflater.inflate(R.layout.trade_recycler_card, parent, false);
    return new MyViewHolder(view, recyclerViewInterface);
  }

  @Override
  public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    TradeWithRef trade = userTrades.get(position);
    double money = trade.getMoney();
    if (money < 0){
      money = -1 * money;

      if(money == 0)
      {
        holder.posterMoneyTextView.setText("");
      }
      else
      {
        holder.posterMoneyTextView.setText("$" + currencyFormat.format(money));
      }
    } else {

      if(money == 0)
      {
        holder.offeringMoneyTextView.setText("");
      }
      else
      {
        holder.offeringMoneyTextView.setText("$" + currencyFormat.format(money));
      }

    }

    String posterItemId = userTrades.get(position).getPosterItem().getId();
    String offeringItemId = userTrades.get(position).getOfferingItem().getId();

    StorageReference imageReferencePoster = IMAGE_STORAGE_INSTANCE.getReference()
            .child("users/" + userTrades.get(position).getPosterEmail()
                    + "/" + posterItemId + ".jpg");
    StorageReference imageReferenceOfferer = IMAGE_STORAGE_INSTANCE.getReference()
            .child("users/" + userTrades.get(position).getOfferingEmail()
                    + "/" + offeringItemId + ".jpg");

    long FIVE_MEGABYTES = 1024 * 1024 * 5;
    imageReferencePoster.getBytes(FIVE_MEGABYTES).addOnSuccessListener(bytes -> {
      Bitmap itemImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
      holder.wantedItemImageView.setImageBitmap(itemImage);
    });
    imageReferenceOfferer.getBytes(FIVE_MEGABYTES).addOnSuccessListener(bytes -> {
      Bitmap itemImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
      holder.yourItemImageView.setImageBitmap(itemImage);
    });
  }

  public void updateTrades(ArrayList<TradeWithRef> updatedTrades) {
    this.userTrades.clear();
    this.userTrades.addAll(updatedTrades);
    notifyDataSetChanged();
  }

  @Override
  public int getItemCount() {
    return userTrades.size();
  }

  public static class MyViewHolder extends RecyclerView.ViewHolder {
    ShapeableImageView yourItemImageView, wantedItemImageView;
    TextView posterMoneyTextView, offeringMoneyTextView;

    public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
      super(itemView);

      yourItemImageView = itemView.findViewById(R.id.your_item_image);
      wantedItemImageView = itemView.findViewById(R.id.wanted_item_image);
      posterMoneyTextView = itemView.findViewById(R.id.poster_money);
      offeringMoneyTextView = itemView.findViewById(R.id.offering_money);

      itemView.setOnClickListener(view -> {
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
