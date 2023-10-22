package com.example.barterbuddy.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barterbuddy.R;
import com.example.barterbuddy.adapters.ChatRecyclerAdapter;
import com.example.barterbuddy.models.ChatMessageModel;
import com.example.barterbuddy.models.ChatroomModel;
import com.example.barterbuddy.models.User;
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
  RecyclerView chatRecyclerView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat_page);

    // get User models
    // TODO: replace these lines with actual user account integration
    otherUser = new User("you", "you@google.com", "password1");
    currentUserId = "me@gmail.com";
    chatroomId = FirebaseUtil.getChatroomId("me@gmail.com", otherUser.getEmail());

    messageInput = findViewById(R.id.chat_edit_text);
    sendMessageButton = findViewById(R.id.send_button);
    chatRecyclerView = findViewById(R.id.chat_recycler_view);

    sendMessageButton.setOnClickListener(
        v -> {
          String message = messageInput.getText().toString().trim();
          if (message.isEmpty()) return;
          sendMessageToUser(message);
        });

    getOrCreateChatroomModel();
    setupChatRecyclerView();
  }

  void setupChatRecyclerView() {
    Query query =
        FirebaseUtil.getChatroomMessageReference(chatroomId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50);

    FirestoreRecyclerOptions<ChatMessageModel> options =
        new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
            .setQuery(query, ChatMessageModel.class)
            .build();

    ChatRecyclerAdapter adapter = new ChatRecyclerAdapter(options);
    LinearLayoutManager manager = new LinearLayoutManager(this);
    manager.setReverseLayout(true);
    chatRecyclerView.setLayoutManager(manager);
    chatRecyclerView.setAdapter(adapter);
    adapter.startListening();
  }

  void sendMessageToUser(String message) {
    // update the chatroom model in Firestore with this new message
    chatroomModel.setLastMessageTimestamp(Timestamp.now());
    chatroomModel.setLastMessageSenderId(currentUserId);
    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

    ChatMessageModel chatMessage = new ChatMessageModel(message, currentUserId, Timestamp.now());
    FirebaseUtil.getChatroomMessageReference(chatroomId)
        .add(chatMessage)
        .addOnSuccessListener(
            documentReference -> {
              // if it's successful we want to clear the text field in the activity
              messageInput.setText("");
            })
        .addOnFailureListener(e -> Log.w(TAG, "Sending chat message failed"));
  }

  void getOrCreateChatroomModel() {
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
