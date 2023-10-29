package com.example.barterbuddy.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barterbuddy.R;
import com.example.barterbuddy.adapters.ChatRecyclerViewAdapter;
import com.example.barterbuddy.models.ChatMessageModel;
import com.example.barterbuddy.models.ChatroomModel;
import com.example.barterbuddy.models.User;
import com.example.barterbuddy.utils.AuthenticationUtil;
import com.example.barterbuddy.utils.FirebaseUtil;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.Arrays;

public class ChatPage extends AppCompatActivity {
  /*
      Citation: This activity is modeled after one from EasyTuto on YouTube: https://youtu.be/E7s542TJDE4?feature=shared
  */

  private static final String TAG = "ChatPage";

  String chatroomId;
  String currentUserId;
  User otherUser;
  ChatroomModel chatroomModel;

  EditText messageInput;
  ImageButton sendMessageButton;
  ImageButton backArrow;
  RecyclerView chatRecyclerView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat_page);

    currentUserId = AuthenticationUtil.getCurrentUserEmail();
    otherUser = (User) getIntent().getSerializableExtra("otherUser");
    setOtherChatterName(otherUser.getUsername());
    chatroomId = FirebaseUtil.getChatroomId(currentUserId, otherUser.getEmail());

    messageInput = findViewById(R.id.chat_edit_text);
    sendMessageButton = findViewById(R.id.send_button);
    chatRecyclerView = findViewById(R.id.chat_recycler_view);
    backArrow = findViewById(R.id.back_arrow);

    backArrow.setOnClickListener(view -> finish());

    sendMessageButton.setOnClickListener(
        v -> {
          String message = messageInput.getText().toString().trim();
          if (message.isEmpty()) return;
          sendMessageToUser(message);
        });

    getOrCreateChatroomModel();
    setupChatRecyclerView();
  }

  private void setOtherChatterName(String otherChatterName) {
    TextView otherChatterNameView = findViewById(R.id.chat_username);
    otherChatterNameView.setText(otherChatterName);
  }

  private void setupChatRecyclerView() {
    Query query =
        FirebaseUtil.getChatroomMessageReference(chatroomId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50);

    FirestoreRecyclerOptions<ChatMessageModel> options =
        new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
            .setQuery(query, ChatMessageModel.class)
            .build();

    ChatRecyclerViewAdapter adapter = new ChatRecyclerViewAdapter(options, currentUserId);
    LinearLayoutManager manager = new LinearLayoutManager(this);
    manager.setReverseLayout(true);
    chatRecyclerView.setLayoutManager(manager);
    chatRecyclerView.setAdapter(adapter);

    adapter.startListening();
  }

  private void sendMessageToUser(String message) {
    // update the chatroom model in Firestore with this new message
    chatroomModel.setLastMessageTimestamp(Timestamp.now());
    chatroomModel.setLastMessageSenderId(currentUserId);
    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

    ChatMessageModel chatMessage = new ChatMessageModel(message, currentUserId, Timestamp.now());
    FirebaseUtil.getChatroomMessageReference(chatroomId)
        .add(chatMessage)
        .addOnSuccessListener(
            documentReference -> {
              // if it's successful we want to clear the typing text field
              messageInput.setText("");
            })
        .addOnFailureListener(e -> Log.w(TAG, "Sending chat message failed"));
  }

  private void getOrCreateChatroomModel() {
    FirebaseUtil.getChatroomReference(chatroomId)
        .get()
        .addOnSuccessListener(
            documentSnapshot -> {
              chatroomModel = documentSnapshot.toObject(ChatroomModel.class);
              // see if we need to create a new chatroom for these users
              if (!documentSnapshot.exists()) {
                chatroomModel =
                    new ChatroomModel(
                        chatroomId,
                        Arrays.asList(currentUserId, otherUser.getEmail()),
                        Timestamp.now(),
                        "");
                // Add this chatroom to firebase
                FirebaseUtil.getChatroomReference(chatroomId)
                    .set(chatroomModel)
                    .addOnSuccessListener(e -> Log.d(TAG, "Chatroom creation successful"))
                    .addOnFailureListener(e -> Log.w(TAG, "Chatroom creation failed"));
              }
            })
        .addOnFailureListener(e -> Log.w(TAG, "Chatroom access failed"));
  }
}
