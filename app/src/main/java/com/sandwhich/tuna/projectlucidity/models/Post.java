package com.sandwhich.tuna.projectlucidity.models;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unused", "WeakerAccess"})

public class Post implements Parcelable{

    private String headline;
    private String description;
    private String imageUrl;
    private String host;
    private String postUrl;
    private DateTimeModel postDate; //todo add time function
    private Map<String,Boolean> usersWhoLiked = new HashMap<>();

    public Map<String, Boolean> getUsersWhoLiked() {
        return usersWhoLiked;
    }

    public void setUsersWhoLiked(Map<String, Boolean> usersWhoLiked) {
        this.usersWhoLiked = usersWhoLiked;
    }

    private long postLikeCount;
//    private HashMap<String,String> likedPosts;

    protected Post(Parcel in) {
        headline = in.readString();
        description = in.readString();
        imageUrl = in.readString();
        host = in.readString();
        postUrl = in.readString();
        postDate = in.readParcelable(DateTimeModel.class.getClassLoader());
        postLikeCount = in.readInt();
        urlForFirebasePath = in.readString();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };


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

    public static String getUrlForFirebasePath(String postUrl){
        return postUrl
                .replace(".","-")
                .replace("$","-")
                .replace("[","-")
                .replace("]","-")
                .replace("#","-")
                .replace("/","-");
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
        this.postLikeCount = 0;
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

    public long getPostLikeCount() {
        return postLikeCount;
    }

    public void setPostLikeCount(long postLikeCount) {
        this.postLikeCount = postLikeCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(headline);
        parcel.writeString(description);
        parcel.writeString(imageUrl);
        parcel.writeString(host);
        parcel.writeString(postUrl);
        parcel.writeParcelable(postDate, i);
        parcel.writeLong(postLikeCount);
        parcel.writeString(urlForFirebasePath);
    }
}
