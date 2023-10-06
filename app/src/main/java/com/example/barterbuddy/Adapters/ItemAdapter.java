/*package com.example.barterbuddy.Adapters;

import static android.os.Build.VERSION_CODES.R;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.barterbuddy.Item;
import com.example.barterbuddy.Models.RecyclerItem;
import com.example.barterbuddy.Query.FireStoreQueryHelper;
import com.example.barterbuddy.databinding.RecyclerowBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.example.barterbuddy.R;

/**
 *
 */
/*public class ItemAdapter extends FirestoreAdapter<ItemAdapter.ViewHolder> {

    private final OnItemSelectedListener listener;
    private final FireStoreQueryHelper queryHelper;

    public  ItemAdapter(RecyclerowBinding binding){
        super(itemBinding.getRoot());
        this.binding = binding;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(DocumentSnapshot skateboard);
    }

    public ItemAdapter(Query query, OnItemSelectedListener listener) {
        super(query);
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), listener);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_row, parent, false);
        return new ViewHolder(view);
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RecycleRowBinding binding;

        public ViewHolder(RecycleRowBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }

        public void bind(final DocumentSnapshot snapshot, final OnItemSelectedListener listener) {

            Item item = snapshot.toObject(Item.class);
            if (item == null) {
                return;
            }

            Resources resources = binding.getRoot().getResources();

            // Load image
            ImageView imageView = itemView.findViewById(R.id.image); // Use findViewById
            String imageUrl = item.getImageUri(); // Replace with the actual method to get the image URL
            Glide.with(itemView.getContext()) // Use itemView.getContext() instead of 'this'
                    .load(imageUrl)
                    .into(imageView);

            binding.title.setText(item.getTitle());
            binding.RecycleRowDescription.setText(item.getDescription());

            // Click listener
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemSelected(snapshot);
                    }
                }
            });
        }
    }
}
*/
