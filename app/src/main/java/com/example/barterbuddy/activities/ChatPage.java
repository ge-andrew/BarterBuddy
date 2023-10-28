package com.example.barterbuddy.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barterbuddy.R;
import com.example.barterbuddy.adapters.ChatRecyclerViewAdapter;
import com.example.barterbuddy.models.ChatMessageModel;
import com.example.barterbuddy.models.ChatroomModel;
import com.example.barterbuddy.models.Trade;
import com.example.barterbuddy.models.User;
import com.example.barterbuddy.utils.AuthenticationUtil;
import com.example.barterbuddy.utils.FirebaseUtil;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
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
  Trade currentTrade;
  ChatroomModel chatroomModel;

  EditText messageInput;
  ImageButton sendMessageButton;
  RecyclerView chatRecyclerView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat_page);



    currentUserId = AuthenticationUtil.getCurrentUserEmail();
    // TODO: get the current trade from an intent
    currentTrade = new Trade("you@google.com", null, currentUserId, null, 3.25);
    setOtherUsernameDisplay(currentUserId, currentTrade);
    // TODO: Try doing this by getting the other user's id from the intent as well
    chatroomId = FirebaseUtil.getChatroomId(currentUserId, otherUser.getEmail());

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

  private void setOtherUsernameDisplay(String currentUserId, Trade currentTrade) {
    String otherUsername;
    if (currentTrade.getOfferingEmail() == currentUserId) {
      otherUsername = currentTrade.getPosterEmail();
    } else {
      otherUsername = currentTrade.getOfferingEmail();
    }
    DocumentReference userDocRef = FirebaseUtil.getUserReference(otherUsername);
    userDocRef
            .get()
            .addOnSuccessListener(
                    userDocSnapshot -> {
                      User user;
                      if (userDocSnapshot.exists()) {
                        user = userDocSnapshot.toObject(User.class);
                        Log.d(TAG, "User information: " + user);

                        setOtherChatterName(user.getUsername());
                      } else {
                        throw new NullPointerException("User object is null");
                      }
                    });
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
