package com.sandwhich.tuna.projectlucidity.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.sandwhich.tuna.projectlucidity.R;
import com.sandwhich.tuna.projectlucidity.models.MyAccountUser;
import com.sandwhich.tuna.projectlucidity.models.PostResult;
import com.sandwhich.tuna.projectlucidity.models.User;

import java.util.HashMap;
import java.util.Map;

public class MyAccountActivity extends AppCompatActivity {
    DatabaseReference userRef;
    FirebaseUser mFirebaseUser;
    MyAccountUser currentUser;
    Toolbar toolbar;
    Map<String,PostResult> postResultMap;
    DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    TextView totalLikedPosts,totalDislikedPosts,totalKarma;
    TextView userName,userEmail;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myaccount);
        initUI();
        postResultMap = new HashMap<>();
        userRef = FirebaseDatabase.getInstance().getReference("users");
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef.child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(MyAccountUser.class);
                updateUI(currentUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateUI(MyAccountUser currentUser) {
        Map<String, PostResult> postResultMap;
        postResultMap = currentUser.getLikedPosts();
        int likedPosts=0,dislikedPosts=0,karmaCount=0;
        for (String key : postResultMap.keySet()){
            switch (postResultMap.get(key)){
                case LIKED:
                    likedPosts+=1;
                    break;
                case NEUTRAl:
                    break;
                case DISLIKED:
                    dislikedPosts+=1;
                    break;
                    default:
            }
        }
        karmaCount = likedPosts+dislikedPosts;

        totalLikedPosts.setText(""+likedPosts);
        totalDislikedPosts.setText(""+dislikedPosts);
        totalKarma.setText(""+karmaCount);
        userEmail.setText(currentUser.getEmail());
        userName.setText(currentUser.getEmail().split("@")[0]);



    }

    private void initUI() {
        userEmail = findViewById(R.id.user_email);
        userName = findViewById(R.id.username);
        totalLikedPosts = findViewById(R.id.likedPostNumber);
        totalDislikedPosts = findViewById(R.id.dislikedPostNumber);
        totalKarma = findViewById(R.id.totalKarmaNumber);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_bar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.signout:
                        item.setChecked(true);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.my_account:
                        makeToast("My account");
                        item.setChecked(true);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //  dummy func for toasts
    void makeToast(String s){
        Toast.makeText(MyAccountActivity.this,""+s,Toast.LENGTH_SHORT).show();
    }
}
