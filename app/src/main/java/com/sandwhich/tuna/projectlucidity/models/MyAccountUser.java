package com.sandwhich.tuna.projectlucidity.models;

import java.util.Map;

public class MyAccountUser {
    String email,uid;
    Map<String,PostResult> likedPosts;

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

    public Map<String, PostResult> getLikedPosts() {
        return likedPosts;
    }

    public void setLikedPosts(Map<String, PostResult> likedPosts) {
        this.likedPosts = likedPosts;
    }

    public MyAccountUser(){}
}
