package com.example.myapplication;

public class User {
    private String name;
    private String profileImageUrl;
    private String username;
    private String uid;

    public User() {
        // Constructor vacío requerido por Firebase
    }

    public User(String name, String profileImageUrl, String uid) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.uid = uid;
    }

    // Getters y setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

