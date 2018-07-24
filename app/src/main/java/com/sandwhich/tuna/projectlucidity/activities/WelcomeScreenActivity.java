package com.sandwhich.tuna.projectlucidity.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.sandwhich.tuna.projectlucidity.R;

public class WelcomeScreenActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initUI();
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
