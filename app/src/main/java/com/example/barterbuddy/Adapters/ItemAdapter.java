package com.example.barterbuddy.Adapters;

import static android.os.Build.VERSION_CODES.R;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.barterbuddy.R;


import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

/**
 *
 */
public class ItemAdapter extends FirestoreAdapter<ItemAdapter.ViewHolder> {

    private final OnItemSelectedListener listener;

    public interface OnItemSelectedListener {
        void onItemSelected(DocumentSnapshot skateboard);
    }

    public ItemAdapter(Query query, OnItemSelectedListener listener) {
        super(query);
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout.recyle_row, parent, false); // Use recycle_row.xml here
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), listener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemRestaurantBinding binding;

        public ViewHolder(ItemRestaurantBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final DocumentSnapshot snapshot, final OnItemSelectedListener listener) {
            Restaurant restaurant = snapshot.toObject(Restaurant.class);
            if (restaurant == null) {
                return;
            }

            Resources resources = binding.getRoot().getResources();

            // Load image
            Glide.with(binding.restaurantItemImage.getContext())
                    .load(restaurant.getPhoto())
                    .into(binding.restaurantItemImage);

            int numRatings = restaurant.getNumRatings();

            binding.restaurantItemName.setText(restaurant.getName());
            binding.restaurantItemRating.setRating(restaurant.getAvgRating());
            binding.restaurantItemCity.setText(restaurant.getCity());
            binding.restaurantItemCategory.setText(restaurant.getCategory());
            binding.restaurantItemNumRatings.setText(resources.getString(
                    R.string.fmt_num_ratings,
                    numRatings));
            binding.restaurantItemPrice.setText(RestaurantUtil.getPriceString(restaurant));

            // Click listener
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onRestaurantSelected(snapshot);
                    }
                }
            });
        }
    }
}

