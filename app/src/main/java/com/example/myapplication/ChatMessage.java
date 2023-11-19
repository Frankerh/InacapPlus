package com.example.myapplication;

import com.google.firebase.Timestamp;

public class ChatMessage {
    private String senderUid;
    private String receiverUid;
    private String message;
    private Timestamp timestamp;

    // Constructores, getters y setters

    public ChatMessage() {
        // Constructor vacío necesario para Firestore
    }

    public ChatMessage(String senderUid, String receiverUid, String message, Timestamp timestamp) {
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
