package com.sandwhich.tuna.projectlucidity.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.Toast;

import com.sandwhich.tuna.projectlucidity.R;
import com.sandwhich.tuna.projectlucidity.models.Post;

public class ViewPostActivity extends AppCompatActivity {
    WebView webView;
    Post post;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        try{
            assert bundle != null;
            post = (Post) bundle.getParcelable("post-obj");
        }
        catch (NullPointerException ex){

        }
        catch(Exception ex){
            ex.printStackTrace();
            finish();
        }
        initUI();


    }

    private void initUI() {
        webView = findViewById(R.id.loadPage);
        try{
            webView.loadUrl(post.getPostUrl());
        }
        catch (Exception ex){
            Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
}
