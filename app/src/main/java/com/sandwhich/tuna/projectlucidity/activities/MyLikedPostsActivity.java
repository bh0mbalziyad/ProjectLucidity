package com.sandwhich.tuna.projectlucidity.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sandwhich.tuna.projectlucidity.R;
import com.sandwhich.tuna.projectlucidity.adapters.LikedPostRecycler;
import com.sandwhich.tuna.projectlucidity.interfaces.ItemClickListener;
import com.sandwhich.tuna.projectlucidity.models.MyAccountUser;
import com.sandwhich.tuna.projectlucidity.models.Post;
import com.sandwhich.tuna.projectlucidity.models.PostResult;
import com.sandwhich.tuna.projectlucidity.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MyLikedPostsActivity extends AppCompatActivity {
    RecyclerView postRecycler;
    LikedPostRecycler postAdapter;
    DrawerLayout mDrawer;
    NavigationView navBar;
    Toolbar toolbar;
    MyAccountUser currentUser;
    FirebaseUser firebaseUser;
    List<Post> postList;
    int close = GravityCompat.START;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylikedposts);
        initUI();

    }

    private void initUI() {
        //initializing variables
        postList = new ArrayList<>();
        postRecycler = findViewById(R.id.likedPostsRecycler);
        mDrawer = findViewById(R.id.navDrawer);
        navBar = findViewById(R.id.nav_bar);
        toolbar = findViewById(R.id.toolbar);

        //setting up recycler view
        postAdapter = new LikedPostRecycler(getBaseContext(), new ItemClickListener() {
            @Override
            public void onClick(View v, int position, View completeView) {
                Log.i("Liked posts","Clicked-"+position);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(postList.get(position).getPostUrl())));
            }
        });
        postRecycler.setLayoutManager(new LinearLayoutManager(getBaseContext(),LinearLayoutManager.VERTICAL,false));
        postRecycler.setAdapter(postAdapter);



        //setting up toolbar
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setTitle("Liked Posts");

        //navigation drawer listener setup
        navBar.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getBaseContext(),MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        mDrawer.closeDrawer(close);
                        return true;
                    case R.id.signout:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getBaseContext(),WelcomeScreenActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        mDrawer.closeDrawer(close);
                        return true;
                    case R.id.my_account:
                        startActivity(new Intent(getBaseContext(),MyAccountActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        mDrawer.closeDrawer(close);
                        return true;
                    case R.id.likedPosts:
                        mDrawer.closeDrawer(close);
                        return true;
                }

                return true;
            }
        });


        //setting up the recycler view
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.child("users/"+firebaseUser.getUid()).getValue(MyAccountUser.class);
                for(DataSnapshot post : dataSnapshot.child("posts").getChildren()){
                    postList.add(post.getValue(Post.class));
                }
                postList = postList.stream()
                        .filter(p -> PostResult.LIKED == p.getUsersWhoLiked().get(currentUser.getUid()))
                        .collect(Collectors.toList());
                Log.i("Post filter","size-"+postList.size());
                postAdapter.addItems(postList);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    boolean doubleBackPressedToExit = false;
    @Override
    public void onBackPressed() {
        if (doubleBackPressedToExit) finish();

        doubleBackPressedToExit = true;
        Toast.makeText(getBaseContext(),"Press back again to exit",Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackPressedToExit=false;
            }
        }, 2000);

    }

    //listener for toolbar events
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
