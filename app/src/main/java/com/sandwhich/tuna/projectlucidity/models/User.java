package com.sandwhich.tuna.projectlucidity.models;

import java.util.HashMap;

public class User {
    String email,uid;
    HashMap<String,String> likedPosts;

    public User() {
    }

    public User(String email, String uid, HashMap<String, String> likedPosts) {
        this.email = email;
        this.uid = uid;
        this.likedPosts = likedPosts;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public HashMap<String, String> getLikedPosts() {
        return likedPosts;
    }

    public void setLikedPosts(HashMap<String, String> likedPosts) {
        this.likedPosts = likedPosts;
    }
}
