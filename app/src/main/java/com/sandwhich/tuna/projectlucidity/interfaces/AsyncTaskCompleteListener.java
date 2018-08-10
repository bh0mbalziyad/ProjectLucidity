package com.sandwhich.tuna.projectlucidity.interfaces;

import android.app.ProgressDialog;

import com.sandwhich.tuna.projectlucidity.models.ArticleDataModel;

public interface AsyncTaskCompleteListener {
    public void onTaskComplete(ArticleDataModel result, ProgressDialog pd, int responseCode);
}
