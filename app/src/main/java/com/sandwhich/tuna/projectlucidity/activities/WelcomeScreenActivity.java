package com.sandwhich.tuna.projectlucidity.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sandwhich.tuna.projectlucidity.R;

public class WelcomeScreenActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        //todo optimize this please.
        if (user!=null){
            startActivity(new Intent(WelcomeScreenActivity.this,LoginActivity.class));
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
                startActivity(new Intent(WelcomeScreenActivity.this,LoginActivity.class));
                break;
            case R.id.signup:
                p("Clicked sign up");
                startActivity(new Intent(WelcomeScreenActivity.this,SignUpActivity.class));
                break;
            default:

        }
    }
}
