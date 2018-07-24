package com.sandwhich.tuna.projectlucidity.models;

import android.support.v7.widget.AppCompatImageView;

public class ItemModel {
    String parentWebsite,timeStamp,articleHeadline;
    AppCompatImageView headerImage;

    public ItemModel() {
    }

    public ItemModel(String parentWebsite, String timeStamp, String articleHeadline) {
        this.parentWebsite = parentWebsite;
        this.timeStamp = timeStamp;
        this.articleHeadline = articleHeadline;
    }

    public String getParentWebsite() {
        return parentWebsite;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getArticleHeadline() {
        return articleHeadline;
    }
}
