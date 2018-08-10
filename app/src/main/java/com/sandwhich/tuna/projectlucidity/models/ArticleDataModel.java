package com.sandwhich.tuna.projectlucidity.models;

import com.google.firebase.database.Exclude;

public class ArticleDataModel {
    String headline,description,imageUrl,host,postUrl;
    @Exclude public String urlForFirebasePath;

    @Exclude public String getUrlForFirebasePath() {
        return urlForFirebasePath;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public ArticleDataModel(){
        //empty default constructor for firebase
    }

    public ArticleDataModel(String headline, String description, String imageUrl, String host, String postUrl) {
        this.headline = headline;
        this.description = description;
        this.imageUrl = imageUrl;
        this.host = host;
        this.postUrl = postUrl;
        this.urlForFirebasePath = postUrl
                .replace(".","-")
                .replace("$","-")
                .replace("[","-")
                .replace("]","-")
                .replace("#","-")
                .replace("/","-");

    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
