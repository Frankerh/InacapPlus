package com.example.myapplication;

public class Messager {
    private String text;
    private String senderId;
    private String receiverId;

    public Messager() {

    }

    public Messager(String text, String senderId, String receiverId) {
        this.text = text;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
}