package com.sandwhich.tuna.projectlucidity.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sandwhich.tuna.projectlucidity.R;
import com.sandwhich.tuna.projectlucidity.interfaces.AsyncTaskCompleteListener;
import com.sandwhich.tuna.projectlucidity.interfaces.TinyDB;
import com.sandwhich.tuna.projectlucidity.models.Post;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.HttpUrl;

import static com.sandwhich.tuna.projectlucidity.activities.MainActivity.makeOP;

public class AddPostPageActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseStorage fStorage;
    DatabaseReference postRef;
    StorageReference fRef;
    TextInputEditText postUrlSrc;
    String postUrlVal;
    ImageView articleImage;
    TextView parentWebsite,timeStamp,articleHeadline;
    ImageLoader imgLoader;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_post_page);
        initUI();
    }

    private void initUI() {
        postRef = FirebaseDatabase.getInstance().getReference("posts");
        fStorage = FirebaseStorage.getInstance();
        fRef = fStorage.getReference();
        imgLoader = ImageLoader.getInstance();
        imgLoader.init(ImageLoaderConfiguration.createDefault(AddPostPageActivity.this));
        postUrlSrc = findViewById(R.id.newsUrlVal);
        postUrlSrc.setTextIsSelectable(true);
        postUrlSrc.setFocusable(true);
        postUrlSrc.setFocusableInTouchMode(true);
        parentWebsite = findViewById(R.id.parent_website);
        articleImage = findViewById(R.id.article_header_image);
        timeStamp = findViewById(R.id.timestamp);
        articleHeadline = findViewById(R.id.article_header_content);
        findViewById(R.id.submit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.submit){
            final Button submit = findViewById(R.id.submit);
            if (submit.getText().toString().toLowerCase().equals("submit post")){
                submit.setEnabled(false);
                postUrlVal = postUrlSrc.getText().toString();
                if (postUrlVal.equals("")) {
                    Toast.makeText(AddPostPageActivity.this,"Please provide non-empty URL.",Toast.LENGTH_SHORT).show();
                    submit.setEnabled(true);
                }
                else{
                    HttpUrl cleanUrl = HttpUrl.parse(postUrlVal);
                    NetworkRequest makeAPost = new NetworkRequest(AddPostPageActivity.this, new AsyncTaskCompleteListener() {
                        @Override
                        public void onTaskComplete(final Post result, ProgressDialog pd, int responseCode) {
                            if (pd.isShowing()) pd.dismiss();
                            if (responseCode==300){ //amazing url
                                timeStamp.setText(result.getPostDate().getDate());
                                parentWebsite.setText(result.getHost());
                                articleHeadline.setText(result.getHeadline());
                                imgLoader.loadImage(result.getImageUrl(), new ImageSize(200, 140), new ImageLoadingListener() {
                                    @Override
                                    public void onLoadingCancelled(String imageUri, View view) {
                                        submit.setEnabled(true);
                                    }

                                    @Override
                                    public void onLoadingStarted(String imageUri, View view) {
                                        Log.i("imgldr","Started loading image from:"+result.getImageUrl());
                                        submit.setEnabled(false);
                                    }

                                    @Override
                                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                        Log.i("imgldr","Failed to load because:\n"+failReason.toString());
                                        submit.setEnabled(true);
                                    }

                                    @Override
                                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                        Log.i("imgldr","Completed loading image");
                                        articleImage.setImageBitmap(loadedImage);
                                        StorageReference bmpRef = fRef.child("bitmaps/"+result.urlForFirebasePath+".bmp");
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        loadedImage.compress(Bitmap.CompressFormat.PNG,100,baos);
                                        byte[] data = baos.toByteArray();
                                        TinyDB db = new TinyDB(AddPostPageActivity.this);
                                        db.putObject("bitmaps/"+result.getUrlForFirebasePath()+".bmp",loadedImage);
                                        UploadTask uploadTask = bmpRef.putBytes(data);
                                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                Log.i("imgldr","finished uploading bitmap");
                                            }
                                        });
                                        uploadTask.addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i("imgldr","Failed to upload bitmap");
                                            }
                                        });

                                        // finally allow user to create post
                                        postRef.child(result.getUrlForFirebasePath()).setValue(result).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                //back to main activity
                                                if (task.isSuccessful()){
                                                    submit.setText("Confirm");
                                                    submit.setEnabled(true);
                                                }
                                            }
                                        });
                                    }


                                });

                            }
                            else if (responseCode==404){ //something went bad
                                if (pd.isShowing()) pd.dismiss();
                                Toast.makeText(AddPostPageActivity.this,"There was a problem with the url which you supplied.",Toast.LENGTH_SHORT).show();
                                submit.setEnabled(true);
                            }
                        }
                    });
                    makeAPost.execute(cleanUrl.toString());
                }

            }
            else if (submit.getText().toString().toLowerCase().equals("confirm")){
                startActivity(new Intent(AddPostPageActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        }

    }

    // async task for network requests
    private static class NetworkRequest extends AsyncTask<String,Void,Post> {
        public AsyncTaskCompleteListener asyncTaskCompleteListener;
        private ProgressDialog pd;
        public NetworkRequest(Context c, AsyncTaskCompleteListener cb){
            this.asyncTaskCompleteListener = cb;
            pd = new ProgressDialog(c);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Adding post...");
            pd.show();
        }

        @Override
        protected Post doInBackground(String... strings) {
            String url = strings[0];
            makeOP("OG URL NIGG",url);
            try{
                Document doc = Jsoup.connect(url).get();
                Element headline = doc.selectFirst("meta[property=og:title]");
                makeOP("headline",headline.attr("content"));
                Element desc = doc.selectFirst("meta[property=og:description]");
                makeOP("Description",desc.attr("content"));
                Element imageUrl = doc.selectFirst("meta[property=og:image]");
                makeOP("Image url",imageUrl.attr("content"));
                Element parentWebsite = doc.selectFirst("meta[property=og:site_name]");
                makeOP("Host",parentWebsite.attr("content"));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");


                return new Post(headline.attr("content"),desc.attr("content"),
                        imageUrl.attr("content"),parentWebsite.attr("content"), url,sdf.format(new Date()) );

            }
            catch (IOException | NullPointerException ex){
                ex.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(Post s) {
//            super.onPostExecute(s);
            if (s == null){
                asyncTaskCompleteListener.onTaskComplete(s,pd,404);
            }
            else asyncTaskCompleteListener.onTaskComplete(s,pd,300);
        }
    }
}
