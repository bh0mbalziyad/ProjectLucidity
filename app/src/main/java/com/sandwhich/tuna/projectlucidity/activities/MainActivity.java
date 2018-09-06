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
import com.sandwhich.tuna.projectlucidity.interfaces.ItemClickListener;
import com.sandwhich.tuna.projectlucidity.interfaces.LikeDislikeComplete;
import com.sandwhich.tuna.projectlucidity.models.Post;
import com.sandwhich.tuna.projectlucidity.models.PostResult;
import com.sandwhich.tuna.projectlucidity.models.User;
import com.sandwhich.tuna.projectlucidity.models.UserStatus;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;

public class MainActivity extends AppCompatActivity {
    User currentUser;
    FirebaseUser user;
    DatabaseReference postRef;
    Dialog dialog;
    Button submitPost;
    TextInputEditText postURL;
    RecyclerView newsFeed;
    RecyclerAdapter newsFeedAdapter;
    List<Post> retrievedPosts;
    Intent startViewPost;
//    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
    //todo add transactions for post like and dislike
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = FirebaseAuth.getInstance().getCurrentUser();
        postRef = FirebaseDatabase.getInstance().getReference("posts");
        retrievedPosts = new ArrayList<>();
        if(user == null){
            Intent i = new Intent(MainActivity.this,WelcomeScreenActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

        }
        startViewPost = new Intent(MainActivity.this,ViewPostActivity.class);
        initUI();
        initRecycler();
    }


//  inflate recycler view
    private void initRecycler() {
        FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        newsFeed = findViewById(R.id.newsFeedRecycler);
        newsFeedAdapter = new RecyclerAdapter(MainActivity.this, new ItemClickListener() {
            @Override
            public void onClick(final View v, final int position) {
                final DatabaseReference databaseReference;
                final Post post = retrievedPosts.get(position);
                databaseReference = FirebaseDatabase.getInstance().getReference();
                switch (v.getId()){
                    case R.id.postLike:
                        Log.i("Clicked","Like at pos:"+position);
                        UserStatus u1 = getUserStatusOnPost(post,currentUser);
                        likePost(databaseReference, post, u1, Post.getUrlForFirebasePath(post.getPostUrl()), new LikeDislikeComplete() {
                            @Override
                            public void onTaskComplete(Task<Void> task, PostResult p) {
                                switch (p){
                                    case LIKED:
                                        //change icon to liked
                                    case DISLIKED:
                                        //change icon to disliked
                                    case NEUTRAl:
                                        //change icon to neutral
                                }
                            }
                        });
                        break;
                    case R.id.postDislike:
                        Log.i("Clicked","Dislike at pos:"+position);
                        UserStatus u2 = getUserStatusOnPost(post,currentUser);
                        dislikePost(databaseReference, post, u2, Post.getUrlForFirebasePath(post.getPostUrl()), new LikeDislikeComplete() {
                            @Override
                            public void onTaskComplete(Task<Void> task, PostResult p) {
                                switch (p){
                                    case LIKED:
                                        //change icon to liked
                                    case DISLIKED:
                                        //change icon to disliked
                                    case NEUTRAl:
                                        //change icon to neutral
                                }
                            }
                        });
                        break;
                    default:
                        Log.i("Clicked","Default clicked at pos:"+position);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("post-obj",post);
                        startViewPost.putExtras(bundle);
                        startActivity(startViewPost);
                        break;

                }

            }
        });
        newsFeed.setAdapter(newsFeedAdapter);
        newsFeed.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                makeToast("An error occurred while retrieving user data");
            }
        });
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot posts) {
                //todo read data here from database
                    retrievedPosts.clear();
                    for (DataSnapshot post : posts.getChildren() ){ //get all post instances from database
                        retrievedPosts.add(post.getValue(Post.class)); //add posts to list object
                    }
                    newsFeedAdapter.clearItems();
                    newsFeedAdapter.addItems(retrievedPosts); //add list object to adapter
                    newsFeed.setAdapter(newsFeedAdapter);
                    makeOP("FireDB","Added items from database");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                    makeToast("Failed to load data.\nPlease check your network connection :(");
            }
        });
    }
//  inflate UI components
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


    //function which adds post to Firebase database
    private void makePost() {
        String url = postURL.getText().toString();
        HttpUrl resolvedUrl = HttpUrl.parse(url);
        try{
            assert resolvedUrl != null;
            new NetworkRequest(MainActivity.this, new AsyncTaskCompleteListener() {
                @Override
                //async listener for callback from async task
                public void onTaskComplete(Post result, final ProgressDialog pd, int responseCode) {
                    //todo stuff here for adding web page data to database
                    if (responseCode == 404){ //bad url
                        makeToast("There was a problem with the url you supplied.");
                        if (pd.isShowing()) pd.dismiss();
                        dialog.dismiss();
                    }
                    else if (responseCode == 300){ //amazing url
                        postRef.child(result.urlForFirebasePath).setValue(result).addOnCompleteListener(new OnCompleteListener<Void>() {
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

//    inflate action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_post,menu);
        return true;
    }
//    listen for action bar clicks
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

    public UserStatus getUserStatusOnPost(Post p,User currentUser){
        if (p.getUsersWhoLiked().containsKey(currentUser.getUid())){
            switch (p.getUsersWhoLiked().get(currentUser.getUid())){
                case LIKED:
                    return UserStatus.LIKED;
//                    break;
                case NEUTRAl:
                    return UserStatus.NEUTRAL;
//                    break;
                case DISLIKED:
                    return UserStatus.DISLIKED;
//                    break;
                default:
                    return UserStatus.NEUTRAL;
            }
        }
        else {
            return UserStatus.NEUTRAL;
        }

    }





//    function to like a given post
    public void likePost(DatabaseReference ref, Post post, UserStatus status, String postUrl, final LikeDislikeComplete ld){
        Map<String,Object> tasks = new HashMap<>();
        switch (status){
            case LIKED:
                tasks.put("/posts/"+postUrl+"/postLikeCount",post.getPostLikeCount()-1);
                tasks.put("/users/"+currentUser.getUid()+"/likedPosts/"+postUrl,PostResult.NEUTRAl);
                tasks.put("/posts/"+postUrl+"/usersWhoLiked/"+currentUser.getUid(),PostResult.NEUTRAl);
                ref.updateChildren(tasks).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ld.onTaskComplete(task, PostResult.NEUTRAl);
                    }
                });
                break;
            case NEUTRAL:
                tasks.put("/posts/"+postUrl+"/postLikeCount",post.getPostLikeCount()+1);
                tasks.put("/users/"+currentUser.getUid()+"/likedPosts/"+postUrl,PostResult.LIKED);
                tasks.put("/posts/"+postUrl+"/usersWhoLiked/"+currentUser.getUid(),PostResult.LIKED);
                ref.updateChildren(tasks).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ld.onTaskComplete(task,PostResult.LIKED);
                    }
                });
                break;
            case DISLIKED:
                tasks.put("/posts/"+postUrl+"/postLikeCount",post.getPostLikeCount()+1);
                tasks.put("/users/"+currentUser.getUid()+"/likedPosts/"+postUrl,PostResult.LIKED);
                tasks.put("/posts/"+postUrl+"/usersWhoLiked/"+currentUser.getUid(),PostResult.LIKED);
                ref.updateChildren(tasks).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ld.onTaskComplete(task,PostResult.LIKED);
                    }
                });
                break;
        }
    }
    //    function to dislike a given post
    public void dislikePost(DatabaseReference ref, Post post, UserStatus status, String postUrl, final LikeDislikeComplete ld){
        Map<String,Object> tasks = new HashMap<>();
        switch (status){
            case LIKED:
                tasks.put("/posts/"+postUrl+"/postLikeCount",post.getPostLikeCount()-1);
                tasks.put("/users/"+currentUser.getUid()+"/likedPosts/"+postUrl,PostResult.DISLIKED);
                tasks.put("/posts/"+postUrl+"/usersWhoLiked/"+currentUser.getUid(),PostResult.DISLIKED);
                ref.updateChildren(tasks).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ld.onTaskComplete(task,PostResult.DISLIKED);
                    }
                });
                break;
            case NEUTRAL:
                tasks.put("/posts/"+postUrl+"/postLikeCount",post.getPostLikeCount()-1);
                tasks.put("/users/"+currentUser.getUid()+"/likedPosts/"+postUrl,PostResult.DISLIKED);
                tasks.put("/posts/"+postUrl+"/usersWhoLiked/"+currentUser.getUid(),PostResult.DISLIKED);
                ref.updateChildren(tasks).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ld.onTaskComplete(task,PostResult.DISLIKED);
                    }
                });
                break;
            case DISLIKED:
                tasks.put("/posts/"+postUrl+"/postLikeCount",post.getPostLikeCount()+1);
                tasks.put("/users/"+currentUser.getUid()+"/likedPosts/"+postUrl,PostResult.NEUTRAl);
                tasks.put("/posts/"+postUrl+"/usersWhoLiked/"+currentUser.getUid(),PostResult.NEUTRAl);
                ref.updateChildren(tasks).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ld.onTaskComplete(task,PostResult.NEUTRAl);
                    }
                });
                break;
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
//  dummy func for toasts
    void makeToast(String s){
        Toast.makeText(MainActivity.this,""+s,Toast.LENGTH_SHORT).show();
    }
//  dummy func for logs
    static void makeOP(String tag,String s){
        Log.i(tag,s);
    }
// async task for network requests
    private static class NetworkRequest extends AsyncTask<String,Void,Post>{
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
