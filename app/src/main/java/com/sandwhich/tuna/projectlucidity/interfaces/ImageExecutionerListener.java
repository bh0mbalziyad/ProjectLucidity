package com.sandwhich.tuna.projectlucidity.interfaces;

import android.graphics.Bitmap;

import java.util.Map;

public interface ImageExecutionerListener {
    public void onImageExecutionCompleteListener(Map<String,Bitmap> loadedBitmaps, int responseCode);
}
