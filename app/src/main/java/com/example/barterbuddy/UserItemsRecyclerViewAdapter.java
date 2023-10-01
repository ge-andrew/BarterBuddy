package com.example.barterbuddy;

import android.content.Context; // If errors, this import may be wrong
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserItemsRecyclerViewAdapter extends RecyclerView.Adapter<UserItemsRecyclerViewAdapter.MyViewHolder> {

    Context context;
    ArrayList<Item> items;


    public UserItemsRecyclerViewAdapter(Context context, ArrayList<Item> items) {
        this.context = context;
        this.items = items;
    }
    @NonNull
    @Override
    public UserItemsRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout and give look to each row
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new UserItemsRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserItemsRecyclerViewAdapter.MyViewHolder holder, int position) {
        // assigning values to each of the views as they are recycled back onto the screen
        // values from recycler_view_row.xml layout file
        // based on position of recycler view

        holder.itemTitle.setText(items.get(position).getTitle());
        //holder.imageView.setImageURI();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // this method very very roughly equates to onCreate() from recyclerView
        // sets up image and text views

        ImageView imageView;
        TextView itemTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view);
            itemTitle = itemView.findViewById(R.id.item_title);
        }
    }
}
