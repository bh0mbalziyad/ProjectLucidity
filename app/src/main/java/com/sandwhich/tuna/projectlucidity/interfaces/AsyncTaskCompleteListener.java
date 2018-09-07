package com.sandwhich.tuna.projectlucidity.interfaces;

import android.app.ProgressDialog;

import com.sandwhich.tuna.projectlucidity.models.Post;

public interface AsyncTaskCompleteListener {
    void onTaskComplete(Post result, ProgressDialog pd, int responseCode);
}
