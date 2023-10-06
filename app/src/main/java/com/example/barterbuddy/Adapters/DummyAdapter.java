package com.example.barterbuddy.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barterbuddy.Models.RecyclerItemModel;
import com.example.barterbuddy.R;

import java.util.ArrayList;

public class DummyAdapter extends RecyclerView.Adapter<DummyAdapter.MyViewHolder> {
    Context context;
    ArrayList<RecyclerItemModel> recyclerItemModels;
    public DummyAdapter(Context context, ArrayList<RecyclerItemModel> recyclerItemModels){
        this.context = context;
        this.recyclerItemModels = recyclerItemModels;
    }
    @NonNull
    @Override
    public DummyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //sets look of item card
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerow,parent, false);
        return new DummyAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DummyAdapter.MyViewHolder holder, int position) {
    // sets data to card
        holder.item.setText(recyclerItemModels.get(position).getItem_name());
        holder.itemDesc.setText(recyclerItemModels.get(position).getDescription());
        holder.itemPrice.setText(recyclerItemModels.get(position).getPrice());
        holder.itemPoster.setText(recyclerItemModels.get(position).getPoster());
    }

    @Override
    public int getItemCount() {
        return recyclerItemModels.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        //grabs data fields from xml file and assigns variable names
        TextView item,itemDesc,itemPoster,itemPrice;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.ItemName);
            itemDesc = itemView.findViewById(R.id.description);
            itemPoster = itemView.findViewById(R.id.user);
            itemPrice = itemView.findViewById(R.id.value);
        }
    }

}
