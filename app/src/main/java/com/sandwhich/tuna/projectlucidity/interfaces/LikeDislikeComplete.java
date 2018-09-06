package com.sandwhich.tuna.projectlucidity.interfaces;

import com.google.android.gms.tasks.Task;
import com.sandwhich.tuna.projectlucidity.models.PostResult;

public interface LikeDislikeComplete {
    public void onTaskComplete(Task<Void> task, PostResult p);
}
