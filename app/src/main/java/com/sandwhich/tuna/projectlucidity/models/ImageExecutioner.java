package com.sandwhich.tuna.projectlucidity.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sandwhich.tuna.projectlucidity.interfaces.ImageExecutionerListener;
import com.sandwhich.tuna.projectlucidity.interfaces.TinyDB;

import java.util.Map;

public class ImageExecutioner implements Runnable {
    Map<String,String> tasks;
    ImageLoader imgldr;
    TinyDB tdb;
    ImageExecutionerListener imgListener;

    public ImageExecutioner(Map<String,String> tasks, Context context){
        this.tasks = tasks;
        this.imgldr = ImageLoader.getInstance();
        this.imgldr.init(ImageLoaderConfiguration.createDefault(context));
        this.tdb = new TinyDB(context);
    }

    public void setImgListener(ImageExecutionerListener imgListener){
        this.imgListener = imgListener;
    }

    @Override
    public void run() {
        for (final String key : tasks.keySet()){
            imgldr.loadImage(tasks.get(key), new ImageSize(200, 140), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    tdb.putObject(key,loadedImage);

                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                }
            });
        }
        imgListener.onImageExecutionCompleteListener(300);
    }
}