package com.example.barterbuddy.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
import com.example.barterbuddy.models.Trade;
import com.example.barterbuddy.models.User;
import com.example.barterbuddy.network.UpdateTradeDocument;
import com.example.barterbuddy.utils.AuthenticationUtil;
import com.example.barterbuddy.utils.FirebaseUtil;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.Arrays;
import java.util.Objects;

public class ChatPage extends AppCompatActivity {
  private static final String TAG = "ChatPage";

  String chatroomId;
  String currentUserId;
  String tradeId;
  Trade currentTrade;
  User otherUser;
  String offeringEmail;
  String posterEmail;
  ChatroomModel chatroomModel;

  EditText messageInput;
  ImageButton sendMessageButton;
  ImageButton backArrow;
  Button completeTradeButton;
  Button cancelTradeButton;
  RecyclerView chatRecyclerView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat_page);

    currentUserId = AuthenticationUtil.getCurrentUserEmail();
    otherUser = (User) getIntent().getSerializableExtra("otherUser");
    assert otherUser != null;
    setOtherChatterName(otherUser.getUsername());
    chatroomId = FirebaseUtil.getChatroomId(currentUserId, otherUser.getEmail());
    tradeId = posterEmail + "_" + offeringEmail;    //FirebaseUtil.getTradeId(currentUserId, otherUser.getEmail());

    FirebaseUtil.getTradeReference(tradeId)
        .get()
        .addOnSuccessListener(
            documentSnapshot -> {
              if (documentSnapshot.exists()) {
                currentTrade = documentSnapshot.toObject(Trade.class);
                assert currentTrade != null;
                if (Objects.equals(currentTrade.getStateOfCompletion(), "COMPLETED") || Objects.equals(currentTrade.getStateOfCompletion(), "CANCELED")) {
                  hideButtons();
                }
              }
            });

    completeTradeButton = findViewById(R.id.complete_trade_button);
    cancelTradeButton = findViewById(R.id.cancel_trade_button);

    messageInput = findViewById(R.id.chat_edit_text);
    sendMessageButton = findViewById(R.id.send_button);
    chatRecyclerView = findViewById(R.id.chat_recycler_view);
    backArrow = findViewById(R.id.back_arrow);

    backArrow.setOnClickListener(view -> finish());

    completeTradeButton.setOnClickListener(v -> getCompletionConfirmation());

    cancelTradeButton.setOnClickListener(v -> getCancellationConfirmation());

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

  private void getCompletionConfirmation() {
    getConfirmation(
        "Are you sure you want to close this trade?", "~ Trade has been completed ~", "COMPLETED");
  }

  private void getCancellationConfirmation() {
    getConfirmation(
        "Are you sure you want to cancel this trade?", "~ Trade has been cancelled ~", "CANCELLED");
  }

  private void getConfirmation(String warningMessage, String confirmationMessage, String newState) {
    Dialog dialog = new Dialog(this);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setCancelable(true);
    dialog.setContentView(R.layout.activity_confirmation_dialog_box);
    if (dialog.getWindow() != null)
      dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    TextView message = dialog.findViewById(R.id.warning_message);
    message.setText(warningMessage);
    Button confirm = dialog.findViewById(R.id.confirm_button);
    Button deny = dialog.findViewById(R.id.deny_button);

    confirm.setOnClickListener(
        v -> {
          UpdateTradeDocument.setTradeState(currentTrade, newState);
          sendMessageToUser(confirmationMessage);
          hideButtons();
          dialog.dismiss();
        });

    deny.setOnClickListener(v -> dialog.dismiss());
    dialog.show();
  }

  private void hideButtons() {
    cancelTradeButton.setVisibility(View.GONE);
    completeTradeButton.setVisibility(View.GONE);
    messageInput.setVisibility(View.GONE);
    sendMessageButton.setVisibility(View.GONE);
  }
}
