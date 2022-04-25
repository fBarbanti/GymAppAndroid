package com.example.newgymapp;

public class UserProfileData {

    private String userId;
    private String username;
    private String email;
    private String imageUrl;


    public UserProfileData(String userId, String username, String email, String imageUrl) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.imageUrl = imageUrl;
    }

    public UserProfileData() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
