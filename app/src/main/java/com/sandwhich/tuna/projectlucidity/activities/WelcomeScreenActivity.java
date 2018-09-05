package com.sandwhich.tuna.projectlucidity.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.sandwhich.tuna.projectlucidity.models.User;

import java.util.HashMap;
import java.util.Map;

public class WelcomeScreenActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        //todo optimize this please.
        if (user!=null){
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()){
                        Map<String,Object> tasks = new HashMap<>();
                        tasks.put(user.getUid(),new User(user.getEmail(),user.getUid()));
                        ref.child("users").updateChildren(tasks).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.i("UserAdd","added firebase user +"+user.getEmail());
                                startActivity(new Intent(WelcomeScreenActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                        });
                    }
                    else{
                        startActivity(new Intent(WelcomeScreenActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
//            startActivity(new Intent(WelcomeScreenActivity.this,MainActivity.class));
        }
        initUI();
    }
    boolean doubleBackPressedToExit = false;
    @Override
    public void onBackPressed() {
        if(doubleBackPressedToExit){
            super.onBackPressed();
        }

        doubleBackPressedToExit = true;

        Toast.makeText(WelcomeScreenActivity.this,"Press back again to exit.",Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackPressedToExit = false;
            }
        }, 2000);
    }

    private void p(String s){
        //function to print data to logcat without need of specified tag
        Log.i("BasicI/O",s);
    }

    private void initUI() {
    findViewById(R.id.signin).setOnClickListener(this);
    findViewById(R.id.signup).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signin:
                p("Clicked sign in");
                startActivity(new Intent(this,LoginActivity.class));
                break;
            case R.id.signup:
                p("Clicked sign up");
                break;
            default:

        }
    }
}
