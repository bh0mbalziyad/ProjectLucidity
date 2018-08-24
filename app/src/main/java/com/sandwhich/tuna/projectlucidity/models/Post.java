package com.sandwhich.tuna.projectlucidity.models;
import android.util.Log;
import com.google.firebase.database.Exclude;

public class Post{
    String headline;
    String description;
    String imageUrl;
    String host;
    String postUrl;
    DateTimeModel postDate; //todo add time function

    public DateTimeModel getPostDate() {
        return postDate;
    }

    public void setPostDate(DateTimeModel postDate) {
        this.postDate = postDate;
    }

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

    public Post(){
        //empty default constructor for firebase
    }

    public Post(String headline, String description, String imageUrl, String host, String postUrl,String date) {
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
        this.postDate = new DateTimeModel();
        try{

            this.postDate.setDate(date.split("_")[0]);
            this.postDate.setTime(date.split("_")[1]);
            Log.i("Assigning date","1");
        }
        catch(Exception ex){
            ex.printStackTrace();
            Log.e("Error assigning date","1");
        }

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
