package com.sandwhich.tuna.projectlucidity.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sandwhich.tuna.projectlucidity.R;
import com.sandwhich.tuna.projectlucidity.adapters.RecyclerAdapter;
import com.sandwhich.tuna.projectlucidity.interfaces.ImageExecutionerListener;
import com.sandwhich.tuna.projectlucidity.interfaces.ItemClickListener;
import com.sandwhich.tuna.projectlucidity.interfaces.TinyDB;
import com.sandwhich.tuna.projectlucidity.models.Post;
import com.sandwhich.tuna.projectlucidity.models.PostResult;
import com.sandwhich.tuna.projectlucidity.models.User;
import com.sandwhich.tuna.projectlucidity.models.UserStatus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    User currentUser;
    Vibrator mVibrator;
    FirebaseUser user;
    DatabaseReference postRef;
    RecyclerView newsFeed;
    RecyclerAdapter newsFeedAdapter;
    List<Post> retrievedPosts;
    List<Post> newPosts;
    Intent startViewPost;
    Map<String,String> imgLoadingTasks;
    DrawerLayout mDrawerLayout;
//    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
    //todo add transactions for post like and dislike
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = FirebaseAuth.getInstance().getCurrentUser();
        imgLoadingTasks = new HashMap<>();
        currentUser = new User(user.getEmail(),user.getUid());
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        postRef = FirebaseDatabase.getInstance().getReference("posts");
        retrievedPosts = new ArrayList<>();
        newPosts = new ArrayList<>();
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
        newsFeed = findViewById(R.id.newsFeedRecycler);
        newsFeedAdapter = new RecyclerAdapter(MainActivity.this,currentUser, new ItemClickListener() {
            @Override
            public void onClick(final View v, final int position,final View completeView) {
                final DatabaseReference databaseReference;
                final Post post = retrievedPosts.get(position);
                databaseReference = FirebaseDatabase.getInstance().getReference();
                switch (v.getId()){
                    case R.id.postLike:
                        mVibrator.vibrate(100);
                        Log.i("Clicked","Like at pos:"+position);
                        UserStatus u1 = getUserStatusOnPost(post,currentUser);
                        likePost(databaseReference, post, u1, Post.getUrlForFirebasePath(post.getPostUrl()),position,newsFeedAdapter);
                        break;
                    case R.id.postDislike:
                        mVibrator.vibrate(100);
                        Log.i("Clicked","Dislike at pos:"+position);
                        UserStatus u2 = getUserStatusOnPost(post,currentUser);
                        dislikePost(databaseReference, post, u2, Post.getUrlForFirebasePath(post.getPostUrl()),position,newsFeedAdapter);
                        break;
                    case R.id.article_header_image:
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
        FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                newsFeedAdapter.setCurrentUser(currentUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                makeToast("An error occurred while retrieving user data");
            }
        });
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot posts) {
                //todo read data here from database
                    newPosts.clear();
                    for (DataSnapshot post : posts.getChildren() ){ //get all post instances from database
                        newPosts.add(post.getValue(Post.class)); //add posts to list object
                    }
//                    newPosts.removeAll(retrievedPosts)

                    retrievedPosts.clear();
                    retrievedPosts.addAll(newPosts);
                    retrievedPosts.sort(new Comparator<Post>() {
                        @Override
                        public int compare(Post o1, Post o2) {
                            String dateString1 = o1.getPostDate().getDate()+"_"+o1.getPostDate().getTime();
                            String date2String2 = o2.getPostDate().getDate()+"_"+o2.getPostDate().getTime();
                            try{
                                Date date1 = simpleDateFormat.parse(dateString1);
                                Date date2 = simpleDateFormat.parse(date2String2);
                                if (date1.compareTo(date2)>0){
                                        //date 1 is after date 2 so object 1 is greater
                                        return -1;
                                }
                                else if(date1.compareTo(date2)<0){
                                        //date 2 is after date 1 so object 2 is greater
                                        return 1;
                                }
                                else{
                                        //both dates are equal
                                        return 0;
                                }
                            }
                            catch (ParseException ex){
                                ex.printStackTrace();
                            }
                            return 0;
                        }
                    });
                    for(Post p : newPosts){
                        imgLoadingTasks.put(p.getPostUrl(),p.getImageUrl());
                    }
                    newsFeedAdapter.addItems(retrievedPosts); //add list object to adapter
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        supportActionBar.setTitle("Project Lucidity");
        mDrawerLayout = findViewById(R.id.navDrawer);
        NavigationView navBar = findViewById(R.id.navBar);
        navBar.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.signout:
                        makeToast("Signout");
                        item.setChecked(true);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.my_account:
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        startActivity(new Intent(MainActivity.this,MyAccountActivity.class));
                        return true;
                    case R.id.likedPosts:
                        makeToast("Liked posts");
                        item.setChecked(true);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.about_app:
                        makeToast("About app");
                        item.setChecked(true);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    default:
                            return true;
                }
            }
        });
    }

//        listen for action bar clicks


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_post,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
//                makeToast("Clicked homeasup");

                return true;

            case R.id.addPost:
                startActivity(new Intent(MainActivity.this,AddPostPageActivity.class));

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
    public void likePost(DatabaseReference ref, Post post, UserStatus status, String postUrl, int position, RecyclerAdapter adp){
        Map<String,Object> tasks = new HashMap<>();
        switch (status){
            //post already liked
            case LIKED:
                tasks.put("/posts/"+postUrl+"/postLikeCount",post.getPostLikeCount()-1);
                tasks.put("/users/"+currentUser.getUid()+"/likedPosts/"+postUrl,PostResult.NEUTRAl);
                tasks.put("/posts/"+postUrl+"/usersWhoLiked/"+currentUser.getUid(),PostResult.NEUTRAl);
//                post.setPostLikeCount(post.getPostLikeCount()-1);
//                adp.updateItem(post,position);
                ref.updateChildren(tasks).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
                break;
            //post in neutral state
            case NEUTRAL:
                tasks.put("/posts/"+postUrl+"/postLikeCount",post.getPostLikeCount()+1);
                tasks.put("/users/"+currentUser.getUid()+"/likedPosts/"+postUrl,PostResult.LIKED);
                tasks.put("/posts/"+postUrl+"/usersWhoLiked/"+currentUser.getUid(),PostResult.LIKED);
//                post.setPostLikeCount(post.getPostLikeCount()+1);
//                adp.updateItem(post,position);
                ref.updateChildren(tasks).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
                break;
            //post already disliked
            case DISLIKED:
                tasks.put("/posts/"+postUrl+"/postLikeCount",post.getPostLikeCount()+2);
                tasks.put("/users/"+currentUser.getUid()+"/likedPosts/"+postUrl,PostResult.LIKED);
                tasks.put("/posts/"+postUrl+"/usersWhoLiked/"+currentUser.getUid(),PostResult.LIKED);
//                post.setPostLikeCount(post.getPostLikeCount()+2);
//                adp.updateItem(post,position);
                ref.updateChildren(tasks).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
                break;
        }
    }
    //    function to dislike a given post
    public void dislikePost(DatabaseReference ref, Post post, UserStatus status, String postUrl,int position, RecyclerAdapter adp){
        Map<String,Object> tasks = new HashMap<>();
        switch (status){
            //post already liked
            case LIKED:
                tasks.put("/posts/"+postUrl+"/postLikeCount",post.getPostLikeCount()-2);
                tasks.put("/users/"+currentUser.getUid()+"/likedPosts/"+postUrl,PostResult.DISLIKED);
                tasks.put("/posts/"+postUrl+"/usersWhoLiked/"+currentUser.getUid(),PostResult.DISLIKED);
//                post.setPostLikeCount(post.getPostLikeCount()-2);
//                adp.updateItem(post,position);
                ref.updateChildren(tasks).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
                break;
            //post in neutral state
            case NEUTRAL:
                tasks.put("/posts/"+postUrl+"/postLikeCount",post.getPostLikeCount()-1);
                tasks.put("/users/"+currentUser.getUid()+"/likedPosts/"+postUrl,PostResult.DISLIKED);
                tasks.put("/posts/"+postUrl+"/usersWhoLiked/"+currentUser.getUid(),PostResult.DISLIKED);
//                post.setPostLikeCount(post.getPostLikeCount()-1);
//                adp.updateItem(post,position);
                ref.updateChildren(tasks).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
                break;
            //post already disliked
            case DISLIKED:
                tasks.put("/posts/"+postUrl+"/postLikeCount",post.getPostLikeCount()+1);
                tasks.put("/users/"+currentUser.getUid()+"/likedPosts/"+postUrl,PostResult.NEUTRAl);
                tasks.put("/posts/"+postUrl+"/usersWhoLiked/"+currentUser.getUid(),PostResult.NEUTRAl);
//                post.setPostLikeCount(post.getPostLikeCount()+1);
//                adp.updateItem(post,position);
                ref.updateChildren(tasks).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

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

}
