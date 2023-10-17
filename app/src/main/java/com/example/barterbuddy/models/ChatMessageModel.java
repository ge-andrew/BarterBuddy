package com.example.barterbuddy.models;

import com.google.firebase.Timestamp;

public class ChatMessageModel {
    /*
        Citation: This model is adapted from one by EasyTuto on YouTube: https://www.youtube.com/watch?v=Ia6BBS-Jvkw
    */
    private String message;
    private String senderId;
    private Timestamp timestamp; // the time this message was sent

    public ChatMessageModel() {}

    public ChatMessageModel(String message, String senderId, Timestamp timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
