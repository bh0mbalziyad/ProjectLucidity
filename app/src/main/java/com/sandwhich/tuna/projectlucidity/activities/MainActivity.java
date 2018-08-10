package com.sandwhich.tuna.projectlucidity.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sandwhich.tuna.projectlucidity.R;
import com.sandwhich.tuna.projectlucidity.adapters.RecyclerAdapter;
import com.sandwhich.tuna.projectlucidity.interfaces.AsyncTaskCompleteListener;
import com.sandwhich.tuna.projectlucidity.models.ArticleDataModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import okhttp3.HttpUrl;

public class MainActivity extends AppCompatActivity {
    DatabaseReference mRef;
    Dialog dialog;
    Button submitPost;
    TextInputEditText postURL;
    RecyclerView newsFeed;
    RecyclerAdapter newsFeedAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference("posts");
        if(user == null){
            Intent i = new Intent(MainActivity.this,WelcomeScreenActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

        }
        initUI();
        initRecycler();
    }

    private void initRecycler() {
        newsFeed = findViewById(R.id.newsFeedRecycler);
        newsFeedAdapter = new RecyclerAdapter(MainActivity.this);
        newsFeed.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //todo read data here from database
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initUI() {
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.add_post_dialog);
        dialog.setTitle("CREATE NEW POST.");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);

        postURL = dialog.findViewById(R.id.input_url);

        submitPost = dialog.findViewById(R.id.submitPost);
        //button which is clicked to add post by user
        submitPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePost();
            }
        });


    }


    //function which adds post to firebase database
    private void makePost() {
        String url = postURL.getText().toString();
        HttpUrl resolvedUrl = HttpUrl.parse(url);
        try{
            assert resolvedUrl != null;
            new NetworkRequest(MainActivity.this, new AsyncTaskCompleteListener() {
                @Override
                //async listener for callback from async task
                public void onTaskComplete(ArticleDataModel result,final ProgressDialog pd,int responseCode) {
                    //todo stuff here for adding web page data to database
                    if (responseCode == 404){ //bad url
                        makeToast("There was a problem with the url you supplied.");
                        if (pd.isShowing()) pd.dismiss();
                        dialog.dismiss();
                    }
                    else if (responseCode == 300){ //amazing url
                        mRef.child(result.urlForFirebasePath).setValue(result).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    if(pd.isShowing()) pd.dismiss();
                                    makeToast("Completed adding post!");
                                }
                                else makeToast("An error occurred.\nPlease try adding the post later.");
                            }
                        });
                    }
                }
            }).execute(resolvedUrl.toString()); //passes url to asynctask
        }
        catch (NullPointerException ex){
            makeToast(ex.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_post,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.addPost:
                dialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    boolean doubleBackPressedToExit = false;
    @Override
    public void onBackPressed() {
        if (doubleBackPressedToExit) finish();

        doubleBackPressedToExit = true;
        Toast.makeText(MainActivity.this,"Press back again to exit",Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackPressedToExit=false;
            }
        }, 2000);

    }

    void makeToast(String s){
        Toast.makeText(MainActivity.this,""+s,Toast.LENGTH_SHORT).show();
    }

    static void makeOP(String tag,String s){
        Log.i(tag,s);
    }

    private static class NetworkRequest extends AsyncTask<String,Void,ArticleDataModel>{
        public AsyncTaskCompleteListener asyncTaskCompleteListener;
        private ProgressDialog pd;
        public NetworkRequest(Context c,AsyncTaskCompleteListener cb){
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
        protected ArticleDataModel doInBackground(String... strings) {
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


                return new ArticleDataModel(headline.attr("content"),desc.attr("content"),
                        imageUrl.attr("content"),parentWebsite.attr("content"), url);

            }
            catch (IOException | NullPointerException ex){
                ex.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(ArticleDataModel s) {
//            super.onPostExecute(s);
            if (s == null){
                asyncTaskCompleteListener.onTaskComplete(s,pd,404);
            }
            else asyncTaskCompleteListener.onTaskComplete(s,pd,300);
        }
    }


}
