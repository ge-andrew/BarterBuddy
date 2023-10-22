package com.example.barterbuddy.adapters;

import android.content.Context; // If errors, this import may be wrong
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.barterbuddy.R;
import com.example.barterbuddy.models.ChatMessageModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

//import java.util.ArrayList;

// Based on UserItemsRecyclerViewAdapter class and EasyTuto

public class ChatRecyclerAdapter
    extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {
  //private final RecyclerViewInterface recyclerViewInterface;
  //Context context;
  FirestoreRecyclerOptions<ChatMessageModel> chatMessages;
  // TODO: Explicit user id is temporary
  String currentUserId = "me@google.com";

  // constructor
  public ChatRecyclerAdapter(
          //Context context,
          FirestoreRecyclerOptions<ChatMessageModel> chatMessages
          //RecyclerViewInterface recyclerViewInterface
     ) {
    super(chatMessages);
    //this.recyclerViewInterface = recyclerViewInterface;
    //this.context = context;
    this.chatMessages = chatMessages;
  }

  @NonNull
  @Override
  public ChatRecyclerAdapter.ChatModelViewHolder onCreateViewHolder(
      @NonNull ViewGroup parent, int viewType) {
    // Inflate layout and give look to each row
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.chat_message_recycler_row, parent, false);
    return new ChatRecyclerAdapter.ChatModelViewHolder(view //recyclerViewInterface
    );
  }

  @Override
  public void onBindViewHolder(
      @NonNull ChatModelViewHolder holder, int position, ChatMessageModel model) {
    // put my messages on the right side, your messages on the left side

    if (model.getSenderId().equals(currentUserId)) {
      holder.leftChatLayout.setVisibility(View.GONE);
    holder.rightChatLayout.setVisibility(View.VISIBLE);
    holder.rightMessage.setText(model.getMessage());
  } else {
        holder.leftChatLayout.setVisibility(View.VISIBLE);
        holder.rightChatLayout.setVisibility(View.GONE);
        holder.leftMessage.setText(model.getMessage());
    }
    }

  @Override
  public int getItemCount() {
    return chatMessages.getSnapshots().size();
  }

  public static class ChatModelViewHolder extends RecyclerView.ViewHolder {
    // this class is responsible for creating the views of this RecyclerView

    LinearLayout leftChatLayout;
    LinearLayout rightChatLayout;
    TextView leftMessage;
    TextView rightMessage;

    public ChatModelViewHolder(
        @NonNull View itemView //RecyclerViewInterface recyclerViewInterface
    ) {
      super(itemView);

      leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
      rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
      leftMessage = itemView.findViewById(R.id.left_chat_textview);
      rightMessage = itemView.findViewById(R.id.right_chat_textview);
    }
  }
}
