package com.example.barterbuddy.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.barterbuddy.R;
import com.example.barterbuddy.models.ChatMessageModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatRecyclerViewAdapter
    extends FirestoreRecyclerAdapter<
        ChatMessageModel, ChatRecyclerViewAdapter.ChatModelViewHolder> {
  FirestoreRecyclerOptions<ChatMessageModel> chatMessages;
  // TODO: Explicit user id is temporary
  String currentUserId = "me@google.com";

  // constructor
  public ChatRecyclerViewAdapter(FirestoreRecyclerOptions<ChatMessageModel> chatMessages) {
    super(chatMessages);
    this.chatMessages = chatMessages;
  }

  @NonNull
  @Override
  public ChatRecyclerViewAdapter.ChatModelViewHolder onCreateViewHolder(
      @NonNull ViewGroup parent, int viewType) {
    // Inflate layout and give look to each row
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.chat_message_recycler_row, parent, false);
    return new ChatRecyclerViewAdapter.ChatModelViewHolder(
        view
        );
  }

  @Override
  public void onBindViewHolder(
      @NonNull ChatModelViewHolder holder, int position, ChatMessageModel model) {
    // put my messages on the right side, your messages on the left side

    if (model.getSenderId().equals(currentUserId)) {
      holder.leftChatCardview.setVisibility(View.GONE);
      holder.rightChatCardview.setVisibility(View.VISIBLE);
      holder.rightMessage.setText(model.getMessage());
    } else {
      holder.leftChatCardview.setVisibility(View.VISIBLE);
      holder.rightChatCardview.setVisibility(View.GONE);
      holder.leftMessage.setText(model.getMessage());
    }
  }

  @Override
  public int getItemCount() {
    return chatMessages.getSnapshots().size();
  }

  public static class ChatModelViewHolder extends RecyclerView.ViewHolder {
    // this class is responsible for creating the views of this RecyclerView

    CardView leftChatCardview;
    CardView rightChatCardview;
    TextView leftMessage;
    TextView rightMessage;

    public ChatModelViewHolder(@NonNull View itemView) {
      super(itemView);

      leftChatCardview = itemView.findViewById(R.id.left_chat_cardview);
      rightChatCardview = itemView.findViewById(R.id.right_chat_cardview);
      leftMessage = itemView.findViewById(R.id.left_chat_textview);
      rightMessage = itemView.findViewById(R.id.right_chat_textview);
    }
  }
}
